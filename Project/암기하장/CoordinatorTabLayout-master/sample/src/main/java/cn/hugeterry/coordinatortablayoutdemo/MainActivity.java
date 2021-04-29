package cn.hugeterry.coordinatortablayoutdemo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;


import java.util.ArrayList;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

/**
 * Created by hugeterry(http://hugeterry.cn)
 */
public class MainActivity extends AppCompatActivity {
    private CoordinatorTabLayout mCoordinatorTabLayout;
    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Words", "Sentences"};
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_instruction:
                        //Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Intent manual = new Intent(getApplicationContext(), manual_main.class);
                        startActivity(manual);
                        break;

                    case R.id.navigation_item_wrongN:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Intent wrongNote = new Intent(getApplicationContext(), wrongNote_Activity.class);
                        startActivity(wrongNote);
                        break;

                    case R.id.navigation_item_developer:
                        LayoutInflater inflater = getLayoutInflater(); // Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                        final View dialogView = inflater.inflate(R.layout.developer_info, null);
                        final AlertDialog.Builder altTestBld = new AlertDialog.Builder(MainActivity.this); // Test를 위한 인풋 받아주는 Alert Dialog 빌더
                        altTestBld.setTitle("개발자 정보"); // 타이틀 설정
                        altTestBld.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                        altTestBld.setCancelable(false);
                        altTestBld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertTest = altTestBld.create();
                        alertTest.show();
                        break;

                    case R.id.navigation_item_license:
                        LayoutInflater inflater2 = getLayoutInflater(); // Layout xml 리소스 파일을 View 객체로 부불려 주는(inflate) LayoutInflater 객체 생성
                        final View dialogView2 = inflater2.inflate(R.layout.license_info, null);
                        final AlertDialog.Builder altTestBld2 = new AlertDialog.Builder(MainActivity.this); // Test를 위한 인풋 받아주는 Alert Dialog 빌더
                        altTestBld2.setTitle("라이센스 정보"); // 타이틀 설정
                        altTestBld2.setView(dialogView2); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                        altTestBld2.setCancelable(false);
                        altTestBld2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertTest2 = altTestBld2.create();
                        alertTest2.show();
                        break;

                }

                return true;
            }
        });


        initFragments();
        initViewPager();
        mImageArray = new int[]{
                R.mipmap.bg_word,
                R.mipmap.bg_sentence,
                R.mipmap.bg_js,
                R.mipmap.bg_other};
        mColorArray = new int[]{
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light
        };

        mCoordinatorTabLayout = (CoordinatorTabLayout) findViewById(R.id.coordinatortablayout);
        mCoordinatorTabLayout.setTranslucentStatusBar(this)
                .setTitle("암기하장")
                .setBackEnable(true)
                .setImageArray(mImageArray, mColorArray)
                .setupWithViewPager(mViewPager);

    }



    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(MainFragment.getInstance(title));
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null, 1);
        db = helper.getReadableDatabase();

        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("카테고리 검색");


        // 리스너 구현
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                String sql = "select category_name,spelling, meaning from contents where spelling='" + s + "' or meaning = '" + s + "';"; // 단어
                Cursor c = db.rawQuery(sql, null);

                //Cursor c = db.query("category",null,"type=1",null,null,null,null);
                if (c == null || c.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "다음 내용 없음 : " + s, Toast.LENGTH_SHORT).show();
                } else {
                    while (c.moveToNext()) {
                        Toast.makeText(getApplicationContext(),
                                "category: " + c.getString(0) + ", spelling: " + c.getString(1) + ", meaning: " + c.getString(2),
                                Toast.LENGTH_SHORT).show();
                        Log.i("Search", "Search contents - category : " + c.getString(0) + ", spelling : " + c.getString(1) +
                                ", meaning : " + c.getString(2));
                    }
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //Toast.makeText(getApplicationContext(),"입력중 : "+s,Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        searchView.setOnQueryTextListener(listener);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);


        return true;
    }
}
