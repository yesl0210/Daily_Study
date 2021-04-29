package cn.hugeterry.coordinatortablayoutdemo;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class testS_Activity extends AppCompatActivity {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    Button wrong,correct;
    TextView contents,txt;
    int index;

    String[] split_sentence;
    int click=0; // 의미 보기!

    String category_name;

    private ArrayList<String> spelling, meaning,category; // 화면에 보일 단어와 뜻

    ArrayList<TableRow> tr;
    ArrayList<EditText> edit;
    ArrayList<Integer> problem;
    TableLayout tl;

    TableLayout.LayoutParams tlp;
    TableRow.LayoutParams lp;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_sentence);

        // View 초기화

        wrong = (Button)findViewById(R.id.wrong);
        correct = (Button)findViewById(R.id.correct);
        //contents = (TextView)findViewById(R.id.txtContents);
        tl = (TableLayout) findViewById(R.id.myTableLayout);

        init();

        // 몰라요 -> wrong_N 자동 추가 (시험지 채점 X)
        wrong.setBackgroundResource(R.drawable.xbtn);
        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateWrongN(spelling.get(index));
                if(index != spelling.size()-1){ // index가 마지막이 아닐 때 -> wrong_N + 1 하고 다음 내용으로 넘어가기
                    // worng +1 하기
                    index +=1;
                    click=0;
                    moveContents(); // 이때 철자로 보여줌
                }
                else { // index 가 마지막 일 때
                    Toast.makeText(getApplicationContext(),"테스트가 종료됩니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });

        // 알아요 --> 정답이랑 맞는지 확인 후, 넘어가야 함.
        correct.setBackgroundResource(R.drawable.obtn);
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spelling.size()==0) { // 저장된 내용이 없을때
                    Toast.makeText(getApplicationContext(),"저장된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    scoreTest(); // 시험지 채점하기
                    if(index != spelling.size()-1) { // index 가 마지막이 아닐때만 다음으로 가기 -> 마지막이면 다음으로 넘어가면 안됨
                        // 저장된 내용없으면 우측이 -1 되버림.
                        index +=1;
                        click=0;
                        moveContents(); // 이때 철자로 보여줌
                    }
                    else { // index 가 마지막일 때
                        Toast.makeText(getApplicationContext(),"테스트가 종료됩니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    } // onCreate


    /*
    초기화 하기
    DB에 내용이 있다면 가져와서 세팅
    Test 세팅하기
     */
    private void init(){
        // DB 를 사용하기 위한 준비 < --  다 같이 써야하니 일단 여따가.
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null,1);

        Intent intent = getIntent();
        int size = Integer.parseInt(intent.getStringExtra("TestNum"));

        String type = intent.getStringExtra("TestType"); // 랜덤 or 순서대로
        category_name = intent.getStringExtra("category_name"); // 카테고리 이름 -> 카테고리에 맞는 내용 뽑아오기
        // 그 전 액티비티에서 컨텐츠 받아오지 말기.


        index = 0; // 화면에 보일 컨텐츠 인덱스 설정 <-- 북마크가 없다면 0으로 시작
        // *** 나중에 북마크 method 만들어야 함

        setToolBar(); // 툴바 설정하기

        dataInit(type, size); // 초기 데이터 불러오기

        // 처음에 하는 것

        // 동적 추가를 위해
        tr = new ArrayList<>();
        edit = new ArrayList<>();
        problem = new ArrayList<>();

        tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);
        lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10); // 왼, 위, 오, 아

        // 초기 컨텐츠 설정하기
        if(spelling.size() == 0){ // 내용이 하나도 없을 때
            //contents.setText("내용을 추가해주세요!");
        }
        else { // 화면에 띄울 내용이 있을때
            //contents.setText("[Spelling]\n"+spelling.get(index));

            // spelling 띄어쓰기 기준으로 분열시켜서 저장하기
            split_sentence = spelling.get(index).split(" ");
            setProblem();
            // *********** 여기서 세팅을 다르게 해줘야 함!
        }





    }

    private void dataInit(String type, int testSize){
        db=helper.getReadableDatabase(); // 데이터 초기에 불러와야 함.

        spelling = new ArrayList<>();
        meaning = new ArrayList<>();
        ArrayList<String> mSpelling = new ArrayList<>();
        ArrayList<String> mMeaning = new ArrayList<>();
        Cursor c=null;

        // 카테고리 이름이 같은 모든 내용 뽑아오기
        c = db.rawQuery("select spelling,meaning from contents where category_name='"+category_name+"';", null);

        while(c.moveToNext()){
            mSpelling.add(c.getString(0)); // 철자 얻기
            mMeaning.add(c.getString(1)); // 의미 얻기
        }

        if(type.compareTo("랜덤으로")==0){
            for (int i=0; i < testSize ; i++){ // 랜덤으로 .size() 내에서 testSize 만큼 뽑기
                int item = new Random().nextInt(mSpelling.size());
                spelling.add(mSpelling.get(item));
                meaning.add(mMeaning.get(item));
                mSpelling.remove(item);
                mMeaning.remove(item);
            }
        }
        else if (type.compareTo("순서대로")==0){ // Test 크기 만큼 컨텐츠 뽑기
            for (int i=0; i < testSize ; i++){
                spelling.add(mSpelling.get(i));
                meaning.add(mMeaning.get(i));
            }
        }
    } // dataInit

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //툴바 설정
        toolbar.setTitleTextColor(Color.parseColor("#ffff33")); //제목의 칼라
        toolbar.setSubtitle("Test : "+category_name); //부제목 넣기
        toolbar.setNavigationIcon(R.mipmap.ic_launcher); //제목앞에 아이콘 넣기
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.
    }

    public void moveContents(){
        // 지정해준 컨텐츠(index) 로 가기 -> 지정해준 index 의 spelling 으로 설정
       // contents.setText("[Spelling]\n"+spelling.get(index));

        // spelling 띄어쓰기 기준으로 분열시켜서 저장하기
        split_sentence = spelling.get(index).split(" ");
        setProblem();
    }


    public void UpdateWrongN(String mSpelling){
        db = helper.getWritableDatabase();
        mSpelling = mSpelling.replace("''","''''"); // 따옴표가 있다면, 따옴표를 모두 따옴표 두개로 치환

        String sql = "update contents set wrong_N = wrong_N+1 where category_name='"+category_name+ "' and spelling='"+mSpelling+"';";
        db.execSQL(sql);
    }

    private void setProblem(){ // index 바뀔때마다 해줘야함.
        // 분열된 문장 이용해서 세팅하기
        // 사이즈 계산해서 그 만큼만 하기.
        // 무조건 Row에는 5개의 컬럼만!
        int i,j,k=0;
        int splitNum = split_sentence.length;
        int splitSize ;

        // splitSize = splitNum/5
        // splitNum % 5 != 0 ---> splitSize +1
        // 두번째 for 에서 (i == splitSize -1 (마지막 row)) && (splitNum % 5 != 0 )
        // ---> j == (splitNum % 5 ) break;

        // 초기화
        tl.removeAllViews(); // tl에 있는 모든 view 초기화
        tr = new ArrayList<>();
        edit = new ArrayList<>();
        problem = new ArrayList<>();

        splitSize = splitNum / 5; // 한 Row 당 5개의 EidtText
        if((splitNum % 5) != 0){ // 나머지가 있다면
            splitSize +=1;
        }

        for (i=0; i<splitSize ; i++){ // 5개씩 나타내기
            tr.add(new TableRow(this)); // 새로운 로우 추가
            tr.get(i).setLayoutParams(lp); // 그 새로운 Row  초기화

            for (j=k ; j<k+5;j++){ // 5개씩 나타내기
                // 두번째 for 에서 (i == splitSize -1 (마지막 row)) && (splitNum % 5 != 0 )
                // ---> j == (splitNum % 5 ) break;

                if((  (i == splitSize-1) && (splitNum % 5 != 0) && (j % 5 == splitNum % 5)  )){
                    break;
                }

                edit.add(new EditText(this)); // 새로운 eidttext 추가
                edit.get(j).setLayoutParams(lp); // 만들어진 새로운 editText 초기화
                edit.get(j).setText(split_sentence[j]); // **** 맞는지 확인, 문장으로 설정해줌


                final int editIndex = j;
                edit.get(j).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) { // 꾹 누르면 사라짐
                        Toast.makeText(getApplicationContext(),edit.get(editIndex).getText().toString(), Toast.LENGTH_SHORT).show();
                        edit.get(editIndex).setText(""); // 안 보이게 지우기
                        problem.add(editIndex); // eidtText 인덱스 추가

                        return true;
                    }
                });
                tr.get(i).addView(edit.get(j)); // 위에서 만들어진 Table Row에 EditText추가
            }
            k = j;
            tl.addView(tr.get(i), tlp);

        } // end for

    } // end setProblem

    // 시험지 채점 -> 틀린 갯수만큼 Toast 메시지에 띄우고 wrong_N +1
    // 하나도 안 틀렸을 경우, wrong_N 그대로
    private void scoreTest(){
        int wrong=0; // 틀린 갯수
        ArrayList<String> wrongP = new ArrayList<>();

        // 정렬 도와줌
        Comparator<Integer> compare = new Comparator<Integer>()
        {
            @Override
            public int compare(Integer lhs, Integer rhs)
            {
                return lhs.compareTo(rhs);
            }
        };
        Collections.sort(problem, compare);


        // 맞는지 안 맞는 지 확인
        for(int p=0; p<problem.size();p++){
            if(edit.get(problem.get(p)).getText().toString().compareTo(split_sentence[problem.get(p)])==0){ // 정답
                //Toast.makeText(getApplicationContext(),"맞았습니다!",Toast.LENGTH_SHORT).show();
            }
            else { // 오답
                wrong +=1;
                wrongP.add(split_sentence[problem.get(p)]+" "); // 오답에 대한 올바른 답, 리스트에 추가
                //Toast.makeText(getApplicationContext(),"틀렸습니다!",Toast.LENGTH_SHORT).show();
            }

            //String check = txt.getText().toString() + "\n"+edit.get(problem.get(p)).getText().toString();
            //txt.setText(check);

        }

        if(wrong != 0){
            // 틀린 갯수만큼 Toast 메시지에 띄우기 + 틀린 단어 띄우기
            Toast.makeText(getApplicationContext(),"틀린 갯수: "+wrong+", 틀린 문제: "+wrongP
                    ,Toast.LENGTH_SHORT).show();
            // Wrong_N +1 하기
            UpdateWrongN(spelling.get(index));
        }

        // 안 틀렸으면 그대로

    } // end scoreTest

}