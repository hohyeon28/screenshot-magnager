package com.google.sample.cloudvision;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

public class DeleteActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        delete();
        Intent intent = new Intent(this, mainDeleteActivity.class);
        startActivity(intent);
    }

    public void delete() {
        File pix = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File downloads = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        File screenshots = new File(pix, "Screenshots");
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
        deleteAsyncTask.execute(downloads, screenshots);
    }

    private class DeleteAsyncTask extends AsyncTask<File, Void, String> {
        private final String TAG = DeleteAsyncTask.class.getSimpleName();

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(File... files) {
            Log.i(TAG,"Job Started!");
            for(File dir : files) {
                Log.i(TAG, "dir is " + dir);
                try {
                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (dir.isDirectory()) {
                        String[] children = dir.list();
                        for (int j = 0; j < children.length; j++) {
                            Log.i(TAG, "Deleted " + children[j].toString());
                            new File(dir, children[j]).delete();
                        }
                    } else {
                        Log.i(TAG, "Dir is not directory.");
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            return "Job Finished!";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute: message: " + s);
        }

    }
}
