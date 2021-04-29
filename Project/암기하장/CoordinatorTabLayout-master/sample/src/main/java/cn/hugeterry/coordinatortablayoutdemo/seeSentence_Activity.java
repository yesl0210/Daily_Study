package cn.hugeterry.coordinatortablayoutdemo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class seeSentence_Activity extends AppCompatActivity {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    Button add,del,befo,next,showM;
    TextView contents;
    int index;
    int click=0;
    String category_name;

    private ArrayList<String> spelling, meaning; // 화면에 보일 문장과 뜻




    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seesentence_main);

        // DB 를 사용하기 위한 준비 < --  다 같이 써야하니 일단 여따가.
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null,1);

        // View 초기화
        add = (Button)findViewById(R.id.addS);
        del = (Button)findViewById(R.id.delS);
        befo = (Button)findViewById(R.id.beforeS);
        next = (Button)findViewById(R.id.nextS);
        //showM = (Button)findViewById(R.id.showMeaningS);
        contents = (TextView)findViewById(R.id.txtContentsS);

        init();

        // 문장 추가하기
        add.setBackgroundResource(R.drawable.plusbtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent(getApplicationContext(),createS_Activity.class); // 문장 추가 페이지로 가기
                intentAdd.putExtra("category_name",category_name); // 카테고리 넘기기
                startActivityForResult(intentAdd,1); // requestCode 의미없음.
            }
        });

        // 문장 삭제하기
        del.setBackgroundResource(R.drawable.deletebtn);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spelling.size() == 0){ // 내용물이 없을 경우 삭제가 안 되도록
                    Toast.makeText(getApplicationContext(),"저장된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else { // 내용물이 한 개 이상일때
                    delete_contents(spelling.get(index)); // DB에서 현재 문장 삭제

                    spelling.remove(index); // ArrayList에서 현재 문장 삭제
                    meaning.remove(index);

                    if(spelling.size() == 0){ // 현재 컨텐츠가 없을때
                        index = 0; // 0으로 초기화
                        contents.setText("내용을 추가해주세요!");
                    }
                    else { //  컨텐츠가 여러 개 일때 (한 개 X)
                        updateContents(); // 컨텐츠 설정 다시 해주기
                    }

                }

            }
        });

        // 이전으로 가기
        befo.setBackgroundResource(R.drawable.leftbtn);
        befo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index != 0){ // index 가 처음이 아닐때만 이전으로 가기 -> index 가 0이면 이전 가면 안됨!
                    index -=1;
                    click=0;
                    moveContents(); // 이때 철자로 보여줌
                }
                else { // 첫번째일때
                    Toast.makeText(getApplicationContext(),"첫번째 내용입니다",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 다음으로 가기
        next.setBackgroundResource(R.drawable.rightbtn);
        next.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(),"마지막 내용입니다",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // 컨텐츠의 의미 보기 or 문장 보기
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

        contents.setOnLongClickListener(new View.OnLongClickListener(){ // *** <-- 북마크 추가하는 기능
            @Override
            public boolean onLongClick(View view) {
                if(addStar() == true){
                    Toast.makeText(getApplicationContext(),"중요한 내용에 추가되었습니다.",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"이미 중요한 내용에 추가되어있습니다.",Toast.LENGTH_SHORT).show();
                }
                return true; // 롱클릭만 인식 되도록
            }
        });

    } // onCreate

    /*
    초기화 하기
    DB에 내용이 있다면 가져와서 세팅
     */
    private void init(){
        // 그 전 activity 로부터 intent 얻기 (카테고리 이름 얻기)
        Intent intent = getIntent();
        category_name = intent.getStringExtra("category_name"); // <-- 툴바에 이걸로 제목 설정
        index = 0; // 화면에 보일 컨텐츠 인덱스 설정 <-- 북마크가 없다면 0으로 시작
        // *** 나중에 북마크 method 만들어야 함

        setToolBar(); // 툴바 설정하기
        dataInit(); // 초기 데이터 불러오기


        // 초기 컨텐츠 설정하기
        if(spelling.size() == 0){ // 내용이 하나도 없을 때
            contents.setText("내용을 추가해주세요!");
        }
        else { // 화면에 띄울 내용이 있을때
            contents.setText("[Spelling]\n"+spelling.get(index));
        }


    }

    private void dataInit(){
        db=helper.getReadableDatabase(); // 데이터 초기에 불러와야 함.

        spelling = new ArrayList<>();
        meaning = new ArrayList<>();

        // 이미 DB에 있는 철자와 뜻 가져오기 (카테고리 이름이 같을때)
        Cursor c = db.rawQuery("select spelling,meaning,category_name from contents where category_name='"+category_name+"';" , null);

        while(c.moveToNext()){
            spelling.add(c.getString(0)); // 철자 얻기
            meaning.add(c.getString(1)); // 의미 얻기
            Log.i("Show Sentence","Sentence - Spelling: "+c.getString(0));
            Log.i("Show Sentence","Sentence - Meaning: "+c.getString(1));
            Log.i("Show Sentence","Sentence - category: "+c.getString(2));
        }
    }

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //툴바 설정
        toolbar.setTitleTextColor(Color.parseColor("#ffff33")); //제목의 칼라
        toolbar.setSubtitle(category_name); //부제목 넣기
        toolbar.setNavigationIcon(R.mipmap.ic_launcher); //제목앞에 아이콘 넣기
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.
    }

    public void moveContents(){
        // 지정해준 컨텐츠(index) 로 가기 -> 지정해준 index 의 spelling 으로 설정
        contents.setText("[Spelling]\n"+spelling.get(index));
    }

    // ActivityOnReulst 에서 받은 거 이용
    private void insert_contents(String mSpelling, String mMeaning) {
        db = helper.getWritableDatabase();
        ContentValues contentsDB = new ContentValues();

        contentsDB.put("category_name", category_name);
        contentsDB.put("spelling",mSpelling);
        contentsDB.put("meaning",mMeaning);
        contentsDB.put("wrong_N",0);
        contentsDB.put("star",0);
        contentsDB.put("type",2);

        db.insert("contents",null,contentsDB);

    }

    // 컨텐츠 삭제
    public void delete_contents(String spelling) {
        db = helper.getWritableDatabase();
        db.delete("contents","spelling=?",new String[]{spelling});
        //Toast 정상 삭제
    }

    private void updateContents(){ // 애초에 컨텐츠가 여러 개라는 조건
        if (index == spelling.size()-1){ // 현재 위치가 마지막일 때 -> index를 그 전껄로 설정해야 함.
            index -=1;
        }
        // 현재 Index 로 내용 설정 다시 하기
        contents.setText("[Spelling]\n"+spelling.get(index));
    }

    // 문장 추가 페이지 -> 문장 보기 페이지로
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // 문장 DB & ArrayList에 추가
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK) { // "추가 완료" 였을 경우

                String mSpelling = data.getStringExtra("spelling"); // 그 전 페이지로부터 철자와 뜻 얻어오기
                String mMeaning = data.getStringExtra("meaning");
                mSpelling = mSpelling.replace("'","''"); // 따옴표가 있다면, 따옴표를 모두 따옴표 두개로 치환

                if(isUnique(mSpelling)== true){ // 존재 X
                    insert_contents(mSpelling,mMeaning);
                    spelling.add(mSpelling); // 리스트에 철자와 의미 추가
                    meaning.add(mMeaning);
                    Log.i("Insert Sentence","Sentence - Spelling: "+mSpelling+", Meaning: "+mMeaning);

                    if(spelling.size()==1){ // 추가해서 1이라면 -> 첫 화면에서 이걸로
                        contents.setText("[Spelling]\n"+spelling.get(index));
                    }
                }
                else { // 이미 존재 O
                    Toast.makeText(getApplicationContext(),"이미 존재하는 내용입니다.",Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){ // "추가 아님" 이었을 경우
                // 반환값이 없을 경우, 유지
            }
        }
    }

    // DB에서 현재 문장이 있는지 없는지 확인
    private boolean isUnique(String mSpelling){
        db =helper.getReadableDatabase();

        // 현재 문장이 DB에 있나?
        Cursor c = db.rawQuery("select spelling from contents where category_name='"+category_name+"' and spelling = '"
                +mSpelling+ "';" , null);

        if(c.getCount() == 0){ // 존재하지 않는다.
            return true;
        }
        else { // 존재한다.
            return false;
        }

    }

    // 옵션 생성하기 (툴바에)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean addStar(){
        // 먼저 star 가 1인지 확인하기
        db = helper.getReadableDatabase();
        String sql = "select star from contents where category_name='"+category_name+ "' and spelling='"+spelling.get(index)+"';";
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            if(Integer.parseInt(c.getString(0))==0){ // star 가 0 이라면
                db = helper.getWritableDatabase();
                sql = "update contents set star = 1 where category_name='"+category_name+ "' and spelling='"+spelling.get(index)+"';";
                db.execSQL(sql);
                return true;
            }
            else { // star 가 1이라면
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.test: // test 내용 받기 &
                LayoutInflater inflater = getLayoutInflater(); // Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                final View dialogView = inflater.inflate(R.layout.test_dialog,null);
                final Intent testIntent = new Intent(getApplicationContext(),testS_Activity.class);
                final AlertDialog.Builder altTestBld = new AlertDialog.Builder(this); // Test를 위한 인풋 받아주는 Alert Dialog 빌더

                altTestBld.setTitle("Test 설정하기"); // 타이틀 설정
                altTestBld.setMessage("Test를 볼 갯수와 설정 옵션을 선택하세요."); // 메시지 할 거 선택
                altTestBld.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

                altTestBld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final EditText numEdit = (EditText)dialogView.findViewById(R.id.dialog_edit);
                        final RadioGroup rg = (RadioGroup)dialogView.findViewById(R.id.dialog_rg);
                        int notDigit =0; // 숫자가 아닐경우 1이상, 숫자맞을경우 0
                        String num = numEdit.getText().toString();
                        int checkedId = rg.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton)rg.findViewById(checkedId);
                        String type = rb.getText().toString();

                        for(char c : num.toCharArray()) {
                            if (!Character.isDigit(c)) { //숫자가 아닐 경우
                                notDigit +=1;
                            }
                        }
                        num = num.trim();
                        if (num.getBytes().length<=0){ // 입력 안 했을 때
                            Toast.makeText(getApplicationContext(),"갯수를 입력해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else if (spelling.size()==0){ // 컨텐츠 사이즈가 0 일때
                            Toast.makeText(getApplicationContext(),"존재하는 내용이 없습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else if (notDigit >= 1) { // 문자를 포함해서 입력했을 때
                            Toast.makeText(getApplicationContext(), "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if (Integer.parseInt(num)==0){ // 입력한 내용이 0일 때
                            Toast.makeText(getApplicationContext(),"테스트 갯수는 1 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else if (Integer.parseInt(num) > spelling.size()){ // 컨텐츠 사이즈보다 클 때
                            Toast.makeText(getApplicationContext(),"입력한 갯수가 현재 내용을 초과했습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            testIntent.putExtra("TestNum",num); // 테스트 볼 갯수 넣기
                            testIntent.putExtra("TestType",type); // 테스트 볼 갯수 넣기
                            testIntent.putExtra("category_name",category_name); // 테스트 볼 갯수 넣기
                            startActivity(testIntent); // test 액티비티로
                        }
                        dialogInterface.dismiss();
                    }
                });
                altTestBld.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertTest = altTestBld.create();
                alertTest.show(); //
                break;
            case R.id.showWrong: // 오답노트 액티비티로 가기
                Intent wrongIntent = new Intent (getApplicationContext(),wrongNote_Activity.class);
                startActivity(wrongIntent);
                break;
            case R.id.share :
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String text = "[카테고리] : "+category_name;

                db = helper.getReadableDatabase();
                String sql = "select spelling,meaning from contents where category_name='"+category_name+"';";
                Cursor c = db.rawQuery(sql, null);
                while(c.moveToNext()){
                    text = text + "\n[Spelling] : "+c.getString(0)+"\n [Meaning] : "+c.getString(1);
                }

                shareIntent.putExtra(Intent.EXTRA_TEXT,text);
                Intent chooser = Intent.createChooser(shareIntent,"친구에게 공유하기");
                startActivity(chooser);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}