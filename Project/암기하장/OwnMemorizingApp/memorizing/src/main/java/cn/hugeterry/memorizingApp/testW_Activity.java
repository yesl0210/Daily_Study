package cn.hugeterry.memorizingApp;


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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class testW_Activity extends AppCompatActivity {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    Button wrong,correct;
    TextView contents;
    int index;

    int click=0; // 의미 보기!

    String category_name;

    private ArrayList<String> spelling, meaning,category; // 화면에 보일 단어와 뜻




    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_word);

        // View 초기화

        wrong = (Button)findViewById(R.id.wrong);
        correct = (Button)findViewById(R.id.correct);
        contents = (TextView)findViewById(R.id.txtContents);

        init();

        // 몰라요 -> wrong_N
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

        // 알아요
        correct.setBackgroundResource(R.drawable.obtn);
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(spelling.size()==0) { // 저장된 내용이 없을때
                    Toast.makeText(getApplicationContext(),"저장된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(index != spelling.size()-1) { // index 가 마지막이 아닐때만 다음으로 가기 -> 마지막이면 다음으로 넘어가면 안됨
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
        });

        // 텍스트뷰 클릭 -> 의미, 철자 보여주기
        contents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(spelling.size() ==0) { // 저장된 내용이 없을때
                    Toast.makeText(getApplicationContext(),"저장된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(click ==0){ // 의미 보여주기
                        contents.setText("[Meaning]\n"+meaning.get(index));
                        click =1;
                    }
                    else if(click==1){ // 철자 보여주기
                        contents.setText("[Spelling]\n"+spelling.get(index));
                        click=0;
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


        // 초기 컨텐츠 설정하기
        if(spelling.size() == 0){ // 내용이 하나도 없을 때
            contents.setText("내용을 추가해주세요!");
        }
        else { // 화면에 띄울 내용이 있을때
            contents.setText("[Spelling]\n"+spelling.get(index));
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
            Log.i("Show Contents","Contents - Spelling: "+c.getString(0));
            Log.i("Show Contents","Contents - Meaning: "+c.getString(1));
        }

        if(type.compareTo("랜덤으로")==0){ // <-- 나중에 라디오로 바꾸기
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
        contents.setText("[Spelling]\n"+spelling.get(index));
    }




    private void updateContents(){ // 애초에 컨텐츠가 여러 개라는 조건
        if (index == spelling.size()-1){ // 현재 위치가 마지막일 때 -> index를 그 전껄로 설정해야 함.
            index -=1;
        }
        // 현재 Index 로 내용 설정 다시 하기
        contents.setText("[Spelling]\n"+spelling.get(index));
    }


    // 옵션 생성하기 (툴바에)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void UpdateWrongN(String spelling){
        db = helper.getWritableDatabase();
        String sql = "update contents set wrong_N = wrong_N+1 where category_name='"+category_name+ "' and spelling='"+spelling+"';";
        db.execSQL(sql);
    }

}