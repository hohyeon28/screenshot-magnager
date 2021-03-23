package com.google.sample.cloudvision;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.file.Paths;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;


public class FindActivity extends AppCompatActivity {
    private static final String TAG = "FindActivity";
    public DBHelper mDB;
    public EditText mEditText;
    public Button mButtonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mDB = new DBHelper(this);
        mEditText = (EditText)findViewById(R.id.edit);
        mButtonSearch = (Button)findViewById(R.id.fnd_button);
        mButtonSearch.setOnClickListener(v -> {

            String n = mEditText.getText().toString();

            Cursor c = mDB.search(n);
            if (c == null || c.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<String> contents = new ArrayList<>();
            ArrayList<String> paths = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    contents.add(c.getString(0));
                    paths.add(c.getString(1));
                } while (c.moveToNext());
            }

            LayoutInflater layoutInflater = getLayoutInflater();
            LinearLayout layout = findViewById(R.id.resultLayout);

            for(int i=0;i<contents.size();i++) {
                View inflated = layoutInflater.inflate(R.layout.layout_item, null);
                TextView content = inflated.findViewById(R.id.content_CONTENT);
                TextView path = inflated.findViewById(R.id.content_PATH);

                content.setText(contents.get(i));
                path.setText(paths.get(i));
                layout.addView(inflated);
                inflated.invalidate();
                layout.invalidate();
            }
        });

    }

}
