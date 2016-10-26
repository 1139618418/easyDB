package me.happy.easydemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.happy.easydb.SQLiteDB;

public class DemoActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        SQLiteDB.getInstance().openOrCreateDB(this,"easy.db","easy");

        List<Test> testList = new ArrayList<Test>();
        Test test = new Test();
        test.name="小米1";
        test.age = 20;
        testList.add(test);
        test = new Test();
        test.name="小米2";
        test.sex = "女";
        testList.add(test);
        test = new Test();
        test.name = "小迷3";
        test.likeBook = "android开发";
        testList.add(test);
        SQLiteDB.getInstance().createTable(Test.class);
//        SQLiteDB.getInstance().close();
//        SQLiteDB.getInstance().open();
//      SQLiteDB.getInstance().alterTableColumn(Test.class)
//        SQLiteDB.getInstance().delete("delete from test",null);
//        SQLiteDB.getInstance().insert(testList);
//        long index = SQLiteDB.getInstance().insert(test);
//        Log.e("zzzzzz",index+"");
//        int i = SQLiteDB.getInstance().queryCount(Test.class);
//        Log.e("zzz",i+"");


//        Test test2 = SQLiteDB.getInstance().queryOne(Test.class,"1");
//        Log.e("zzzz",test2.name);

//        test.name = "小米3";

//        long i = SQLiteDB.getInstance().update(test);
//        Log.e("zzz",i+"");

//        SQLiteDB.getInstance().delete(Test.class,"3");

//        SQLiteDB.getInstance().insert(testList);

        List<Test> tests = SQLiteDB.getInstance().queryPage(Test.class,1,100);
        for(Test item :tests){
            Log.e(item.id+"",item.name);
        }
    }


}
