package com.intsig.scanner.log4aproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.intsig.scanner.log4aproject.util.Log4aApplication;
import com.intsig.scanner.log4aproject.util.LogUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOnClick(R.id.btn_init, R.id.btn_log_d, R.id.btn_log_e, R.id.btn_for_log_d, R.id.btn_for_log_e);
    }

    private void setOnClick(int... viewIds) {
        for (int viewId : viewIds) {
            findViewById(viewId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init:
                int checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233);
                } else {
                    Log4aApplication.initCamScannerLog(getApplication());
                }
                break;
            case R.id.btn_log_d:
                LogUtils.LOGD(TAG, "测试 单次写入logd");
                break;
            case R.id.btn_log_e:
                LogUtils.LOGD(TAG, "测试 单次写入loge");
                break;
            case R.id.btn_for_log_d:
                for (int i = 0; i < 100000; i++) {
                    LogUtils.LOGD(TAG, "测试 循环写入logd");
                }
                break;
            case R.id.btn_for_log_e:
                for (int i = 0; i < 100000; i++) {
                    LogUtils.LOGD(TAG, "测试 循环写入loge");
                }
                break;
            default:
                break;
        }
    }
}