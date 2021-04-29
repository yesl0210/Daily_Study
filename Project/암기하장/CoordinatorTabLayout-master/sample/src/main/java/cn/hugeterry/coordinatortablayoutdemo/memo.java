package cn.hugeterry.coordinatortablayoutdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class memo extends AppCompatActivity {

    ArrayList<TableRow> tr;
    ArrayList<EditText> edit;
    ArrayList<Integer> problem;

    String answer ="나는 여기에"; // <-- 나중에 ArrayList<String> 으로 바꿔야 함

    TextView txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);

        int size = 20; // 20단어

        Button okBtn = (Button)findViewById(R.id.ok);
        txt = (TextView)findViewById(R.id.textView);
        TableLayout tl = (TableLayout) findViewById(R.id.myTableLayout);


        tr = new ArrayList<>();
        edit = new ArrayList<>();
        problem = new ArrayList<>();
        int index=0,j,i;


        TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);


        lp.setMargins(10,10,10,10); // 왼, 위, 오, 아


        for (i=0; i<size/4 ; i++){ // 5개씩 나타내기
            tr.add(new TableRow(this)); // 새로운 로우 추가
            tr.get(i).setLayoutParams(lp); // 그 새로운 Row  초기화
            //tr.get(i).setBackgroundColor(Color.BLUE);

            for (j=index ; j<index+4;j++){ // 5개씩 나타내기
                edit.add(new EditText(this)); // 새로운 eidttext 추가
                edit.get(j).setLayoutParams(lp); // 만들어진 새로운 editText 초기화
                edit.get(j).setText("나는 여기에");

                final int editIndex = j;
                edit.get(j).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Toast.makeText(getApplicationContext(),edit.get(editIndex).getText().toString(), Toast.LENGTH_SHORT).show();
                        edit.get(editIndex).setText("");
                        problem.add(editIndex); // eidtText 인덱스 추가

                        return true;
                    }
                });
                tr.get(i).addView(edit.get(j)); // 위에서 만들어진 Table Row에 EditText추가
            }
            index = j;
            tl.addView(tr.get(i), tlp);

        } // end for



        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"problem : "+problem,Toast.LENGTH_SHORT).show();


                /*
                for(int p=0; p<problem.size();p++){ // sort 용 리스트에 넣기
                    result.add(problem.get(p));
                }*/

                Comparator<Integer> compare = new Comparator<Integer>()
                {
                    @Override
                    public int compare(Integer lhs, Integer rhs)
                    {
                        return lhs.compareTo(rhs);
                    }
                };
                Collections.sort(problem, compare);



                for(int p=0; p<problem.size();p++){
                    if(edit.get(problem.get(p)).getText().toString().compareTo(answer)==0){
                        Toast.makeText(getApplicationContext(),"맞았습니다!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"틀렸습니다!",Toast.LENGTH_SHORT).show();
                    }

                    String check = txt.getText().toString() + "\n"+edit.get(problem.get(p)).getText().toString();
                    txt.setText(check);

                }
            }
        });





    }
}
