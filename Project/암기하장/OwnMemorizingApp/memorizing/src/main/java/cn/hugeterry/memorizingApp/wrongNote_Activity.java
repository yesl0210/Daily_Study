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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class wrongNote_Activity extends AppCompatActivity {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    Button del,befo,next;
    TextView contents;
    int index;
    int click=0; // 의미 보기!

    // **** 별표 -> 중요한 단어 -> DB에 저장
    // 메인 액티비티에 있는 버튼 중 '별표' 누르면 -> 중요한 내용 담긴 곳으로 감
    // 단어와 문장 구분됨.
    // 처음 가져오는 데이터는 DB에서 type 고려해서 별표 되어있는 것만 가져오기
    // 옵션에서 중요한 내용 <-- 선택시 --> 새로운 Activity 로 감 -->
    // **** DB에서 모든
    // 롱클릭 할 경우  별표하기
    // 북마크 해제는 새 액티비티에서 하기

    String category_name;

    private ArrayList<String> spelling, meaning,category; // 화면에 보일 단어와 뜻




    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrong_note);

        // DB 를 사용하기 위한 준비 < --  다 같이 써야하니 일단 여따가.
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null,1);

        // View 초기화
        del = (Button)findViewById(R.id.del);
        befo = (Button)findViewById(R.id.before);
        next = (Button)findViewById(R.id.next);
        contents = (TextView)findViewById(R.id.txtContents);

        init();


        // 중요한 단어에서 삭제하기
        del.setBackgroundResource(R.drawable.deletebtn);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spelling.size() == 0){ // 내용물이 없을 경우 삭제가 안 되도록
                    Toast.makeText(getApplicationContext(),"저장된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else { // 내용물이 한 개 이상일때
                    delete_contents(); // DB에서 현재 단어 삭제

                    spelling.remove(index); // ArrayList에서 현재 단어 삭제
                    meaning.remove(index);
                    category.remove(index);
                    Toast.makeText(getApplicationContext(),"오답 노트에서 삭제되었습니다.",Toast.LENGTH_SHORT).show();
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
     */
    private void init(){
        Intent intent = getIntent();
        category_name = intent.getStringExtra("category_name"); // <-- 툴바에 이걸로 제목 설정

        // 그 전 activity 로부터 intent 얻기 (카테고리 이름 얻기)
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
        category = new ArrayList<>();

        // 이미 DB에 있는 철자와 뜻 가져오기
        Cursor c = db.rawQuery("select spelling,meaning,category_name from contents where wrong_N >= 4" , null);

        while(c.moveToNext()){
            spelling.add(c.getString(0)); // 철자 얻기
            meaning.add(c.getString(1)); // 의미 얻기
            category.add(c.getString(2));
            Log.i("Wrong Note","Spelling: "+c.getString(0));
            Log.i("Wrong Note","Meaning: "+c.getString(1));
            Log.i("Wrong Note","category: "+c.getString(2));

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


    // 컨텐츠 삭제 -> 걍 삭제가 아니라 wrong_N 을 3에서 0으로 설정하고 List에서 제거
    public void delete_contents() {
        db = helper.getWritableDatabase();
        String sql = "update contents set wrong_N = 0 where spelling='"+spelling.get(index)+"' and category_name = '"+category.get(index)+"';";
        db.execSQL(sql);


        // List 에서 제거하기 추가


        //Toast 정상 삭제
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
        MenuItem share = menu.add(0,0,0,"공유하기");
        share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                db = helper.getReadableDatabase();

                String text = "[오답노트]";
                Cursor c = db.rawQuery("select spelling,meaning, category_name from contents where wrong_N >=4", null);

                while(c.moveToNext()){
                    text = text + "\n[Spelling] : "+c.getString(0)+", [Meaning] : "+c.getString(1);
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT,text);
                Intent chooser = Intent.createChooser(shareIntent,"친구에게 공유하기");
                startActivity(chooser);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void addStar(){
        db = helper.getWritableDatabase();
        String sql = "update contents set star = 1 where category_name='"+category_name+ "' and spelling='"+spelling.get(index)+"';";
        db.execSQL(sql);
    }

}