package cn.hugeterry.coordinatortablayoutdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class manual_main extends AppCompatActivity {
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_main);

        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new f1();
                case 1:
                    return new f2();
                case 2:
                    return new f3();
                case 3:
                    return new f4();
                case 4:
                    return new f5();
                case 5:
                    return new f6();
                case 6:
                    return new f7();
                case 7:
                    return new f8();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    }
}
