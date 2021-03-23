package com.google.sample.cloudvision;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.os.Build.VERSION_CODES.N;

public class mainDeleteActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int DELETE_JOB_KEY = 123;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "mainDeleteActivity";

    private Button mButtonDeleteImages;
    private Spinner mSpinner;
    private TextView mTextAutomaticDeletion;
    private TextView mTextPeriodSettings;
    private Switch mSwitchAutomaticDeletion;
    private Uri mImageUri;
    private NotificationReceiver mReceiver = new NotificationReceiver();
    private NotificationManager mNotifyManager;
    private final String CHANNEL_ID = "APP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_main);
        createNotificationChannel();

        sharedPreferences = getSharedPreferences("LastSetting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final int spinnerSelected = sharedPreferences.getInt("LastClick", 0);


        // Set up UI
        mSpinner = findViewById(R.id.spinner);
        mTextAutomaticDeletion = findViewById(R.id.text_automatic_deletion);
        mTextPeriodSettings = findViewById(R.id.text_peroid_settings);

        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean silent = settings.getBoolean("switchkey", false);

        //Switch
        mSwitchAutomaticDeletion = findViewById(R.id.switch_automatic_deletion);
        mSwitchAutomaticDeletion.setChecked(silent);
        mSwitchAutomaticDeletion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTextAutomaticDeletion.setEnabled(true);
                    mSpinner.setEnabled(true);
                    mTextPeriodSettings.setEnabled(true);
                    int days = Integer.parseInt(mSpinner.getSelectedItem().toString());
                    Toast.makeText(mainDeleteActivity.this, "자동 삭제 ON", Toast.LENGTH_SHORT).show();
                    scheduleJob(days);

                } else {
                    mTextAutomaticDeletion.setEnabled(false);
                    mSpinner.setEnabled(false);
                    mTextPeriodSettings.setEnabled(false);
                    Toast.makeText(mainDeleteActivity.this, "자동 삭제 OFF", Toast.LENGTH_SHORT).show();

                    //cancel JobScheduler
                    JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    scheduler.cancel(DELETE_JOB_KEY);
                    Log.d(TAG, "Job cancelled");
                }

                //keep current
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked).apply();
            }
        });

        // Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(spinnerSelected);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("LastClick", position).apply();
                Toast.makeText(mainDeleteActivity.this, "삭제 주기 " + mSpinner.getSelectedItem() + "일", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Button
        mButtonDeleteImages = (Button) findViewById(R.id.button_delete_images);
        mButtonDeleteImages.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                performFileSearch();
            }
        });


    } //onCreate()

    @RequiresApi(api = N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mImageUri = data.getData();
                Log.e("URI==>", String.valueOf(mImageUri));
                DocumentsContract.deleteDocument(getContentResolver(), mImageUri);
                Toast.makeText(
                        mainDeleteActivity.this, String.valueOf(mImageUri)+"삭제되었습니다",
                        Toast.LENGTH_SHORT).show();
            }  // try

            catch (Exception ignored) {
            } // catch
        } // if

    } // onActivityResult()

    public void createNotificationChannel() {
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");
            registerReceiver(mReceiver,new IntentFilter(NotificationReceiver.ACTION_YES));
            registerReceiver(mReceiver,new IntentFilter(NotificationReceiver.ACTION_NO));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Uri uri = Uri.parse(String.valueOf(getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES+"/Screenshots/")));
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);
    } // performFileSearch()


    @RequiresApi(api = N)
    public void scheduleJob(int days) {
        Log.e(TAG,"scheduleJob");

        JobScheduler js =
                (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(
                DELETE_JOB_KEY,
                new ComponentName(mainDeleteActivity.this, MyJobService.class));
        builder.addTriggerContentUri(
                new JobInfo.TriggerContentUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("days", days);
        builder.setExtras(bundle);
        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);
        js.schedule(builder.build());

    } // scheduleJob()



}
