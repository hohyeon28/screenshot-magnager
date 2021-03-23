package com.google.sample.cloudvision;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReadDBActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        TextView path = (TextView)findViewById(R.id.textView1);
        TextView contentView = (TextView)findViewById(R.id.textView3);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select content, path from tb_ocr" +
                "    order by _id desc limit 1", null);


        while (cursor.moveToNext()) {
            contentView.setText(cursor.getString(0));
            path.setText(cursor.getString(1));
        }

        db.close();
    }
}
