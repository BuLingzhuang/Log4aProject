package com.intsig.scanner.log4aproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.intsig.log4a.Log4A;
import com.intsig.log4a.PropertyConfigure;

import java.io.File;
import java.util.Properties;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private com.intsig.log4a.Logger mLogger;
    private com.intsig.log4a.Logger mExtraLogger;
    private long mFileLength;
    private TextView mTvLength;
    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_init).setOnClickListener(this);
        findViewById(R.id.btn_log_d).setOnClickListener(this);
        findViewById(R.id.btn_log_e).setOnClickListener(this);
        mTvLength = findViewById(R.id.tv_lenght);
    }

    private void initLog4a() {
        Properties prop = new Properties();
        prop.put(PropertyConfigure.PROP_LEVEL, PropertyConfigure.VAL_LEVEL_DEBUG);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_DIR, externalStorageDirectory.getAbsolutePath() + "/CamScannerTest/");
        prop.put(PropertyConfigure.PROP_APPENDER, PropertyConfigure.VAL_APPENDER_FILE);
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_MAXSIZE, "2M");
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_NUMBERS, "3");
        com.intsig.log4a.Log4A.init(prop);
        String logFilePath = Log4A.getLogFile();
        long fileLength = 0L;
        if (!TextUtils.isEmpty(logFilePath)) {
            fileLength = new File(logFilePath).length();
        }
        refreshLength(fileLength);
        mRandom = new Random();
        mLogger = com.intsig.log4a.Log4A.getLogger("LOG");
        mExtraLogger = com.intsig.log4a.Log4A.getExtraLogger("ExtraLog");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_init:
                int checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233);
                } else {
                    initLog4a();
                }
                break;
            case R.id.btn_log_d:
                LOG_D("测试D", "随即写入logD =======>" + mRandom.nextInt());
                break;
            case R.id.btn_log_e:
                LOG_E("测试E", "随即写入logE =================>" + mRandom.nextInt());
                break;
        }
    }

    private void refreshLength(long newLength) {
        mFileLength += newLength;
        mTvLength.setText("log长度：" + mFileLength + " L");
    }

    private void LOG_D(String tag, String msg) {
        mLogger.debug(tag, msg);
        if (mExtraLogger != null) {
            mExtraLogger.debug(tag, msg);
        }
    }

    private void LOG_E(String tag, String msg) {
        mLogger.error(tag, msg);
        if (mExtraLogger != null) {
            mExtraLogger.error(tag, msg);
        }
    }
}
