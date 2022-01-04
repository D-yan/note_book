package com.example.finaltext01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Result;

public class new_cost extends AppCompatActivity {
    private DBHelper helper;
    private EditText et_cost_title;
    private EditText et_cost_money;
    private TextView Address;
    private DatePicker dp_cost_date;
    private Button openbutton;
//    private Button okbutton;

    public static new_cost newcost;//这个静态变量用于关闭newcost activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cost);
        initView();

        //map botton
        openbutton=(Button)findViewById(R.id.LocateButton);
        openbutton.setOnClickListener(new ButtonListener());

//        okbutton=(Button)findViewById(R.id.okbotton);
//        okbutton.setOnClickListener(new ButtonListener2());
//        Intent intent = getIntent();
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            //setClass函数的第一个参数是一个Context对象
            //Context是一个类,Activity是Context类的子类,也就是说,所有的Activity对象都可以向上转型为Context对象
            //setClass函数的第二个参数是Class对象,在当前场景下,应该传入需要被启动的Activity的class对象
            intent.setClass(new_cost.this, MapActivity.class);
            startActivity(intent);
        }
    }

//    private class ButtonListener2 implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            Intent intent=new Intent();
//            //setClass函数的第一个参数是一个Context对象
//            //Context是一个类,Activity是Context类的子类,也就是说,所有的Activity对象都可以向上转型为Context对象
//            //setClass函数的第二个参数是Class对象,在当前场景下,应该传入需要被启动的Activity的class对象
//            intent.setClass(new_cost.this, MapActivity.class);
//            startActivity(intent);
//        }
//    }

    private void initView() {
//        Intent intent = getIntent();
//        String data1 = intent.getStringExtra("LocationInfo");

        helper = new DBHelper(new_cost.this);
        et_cost_title = findViewById(R.id.et_cost_title);
        et_cost_money = findViewById(R.id.et_cost_money);
        dp_cost_date = findViewById(R.id.dp_cost_date);
        Address = findViewById(R.id.Address);
        Intent intent = getIntent();
        String data1 = intent.getStringExtra("LocationInfo");

        Address.setText(data1);

    }


    public void okButton(View view) {
        String titleStr = et_cost_title.getText().toString().trim();
        String moneyStr = et_cost_money.getText().toString().trim();
        String locStr = Address.getText().toString().trim();
        String dateStr = dp_cost_date.getYear() + "-" + (dp_cost_date.getMonth() + 1) + "-"
                + dp_cost_date.getDayOfMonth();//这里getMonth会比当前月份少一个月，所以要+1
        if ("".equals(moneyStr)) {//可以不填写Title但是不能不填金额
            Toast toast = Toast.makeText(this, "请填写金额", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Title", titleStr);
            values.put("Money", moneyStr);
            values.put("Date", dateStr);
            values.put("Location", locStr);
            long account = db.insert("account", null, values);
            if (account > 0) {
                Toast toast = Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

//                Intent intent=new Intent(new_cost.this,MainActivity.class);
//                Intent intent=new Intent();
//                intent.setClass(new_cost.this, MainActivity.class);
//                startActivity(intent);
                if(MapActivity.mapactivity!=null){
                    MapActivity.mapactivity.finish();
                }
                setResult(1);
                finish();
//                Intent intent=new Intent();
//                intent.setClass(new_cost.this, MainActivity.class);
//                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(this, "请重试", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                db.close();
            }
//            Intent intent=new Intent(new_cost.this,MainActivity.class);
//            Intent intent=new Intent();
//            intent.setClass(new_cost.this, MainActivity.class);
//            startActivity(intent);

            setResult(1);
            finish();

//            Intent intent=new Intent();
//            intent.setClass(new_cost.this, MainActivity.class);
//            startActivity(intent);
        }

    }
}
