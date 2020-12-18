package com.intsig.scanner.log4aproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.intsig.log4a.Appender;
import com.intsig.log4a.Log4A;
import com.intsig.log4a.PropertyConfigure;

import java.io.File;
import java.nio.MappedByteBuffer;
import java.util.Properties;
import java.util.Random;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TEST_GEN_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CamScannerTest/";
    private static final long KB = 1024;
    private static final long MB = 1024 * KB;
    private static final long DEFAULT_CACHE_SIZE = 30;
    File cacheFile;
    File logFile;

    private com.intsig.log4a.Logger mLogger;
    private com.intsig.log4a.Logger mExtraLogger;
    private long mFileLength;
    private TextView mTvLength;
    private Random mRandom;
    private MappedByteBuffer mCacheMbb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn_init).setOnClickListener(this);
        findViewById(R.id.btn_log_d).setOnClickListener(this);
        findViewById(R.id.btn_log_e).setOnClickListener(this);
        findViewById(R.id.btn_crazy_write).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
        mTvLength = findViewById(R.id.tv_lenght);
        cacheFile = new File(TEST_GEN_DIR, "cacheMMAP" + System.currentTimeMillis() + ".log");
        logFile = new File(TEST_GEN_DIR, "testMMAP" + System.currentTimeMillis() + ".log");
    }

    private void initLog4a() {
        Properties prop = new Properties();
        prop.put(PropertyConfigure.PROP_LEVEL, PropertyConfigure.VAL_LEVEL_DEBUG);
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_DIR, TEST_GEN_DIR);
        prop.put(PropertyConfigure.PROP_APPENDER, PropertyConfigure.VAL_APPENDER_FAST_FILE);
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
                    refreshEnable(R.id.btn_log_d, R.id.btn_log_e, R.id.btn_crazy_write, R.id.btn_test);
                }
                break;
            case R.id.btn_log_d:
                LOG_D("测试D", "随即写入logD =======>" + mRandom.nextInt());
                break;
            case R.id.btn_log_e:
                LOG_E("测试E", "随即写入logE =================>" + mRandom.nextInt());
                break;
            case R.id.btn_crazy_write:
                crazyWrite();
                break;
            case R.id.btn_test:
//                crazyWriteF();
                break;
            default:
                break;
        }
    }


    private void refreshEnable(int... ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (view != null) {
                view.setEnabled(true);
            }
        }
    }

    private static final int CRAZY_W_TIMES = 100000;

    /**
     * 压力测试
     */
    private void crazyWrite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long startTime = System.currentTimeMillis();
                Log4A.setWriteListener(new Appender.WriteListener() {
                    @Override
                    public void finish() {
                        final long consume = System.currentTimeMillis() - startTime;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String logFilePath = Log4A.getLogFile();
                                if (!TextUtils.isEmpty(logFilePath)) {
                                    File file = new File(logFilePath);
                                    long fileLength = file.length() / 1024;
                                    logDisplay("文件名：" + file.getName() + "\nlog长度：" + fileLength + "kb\n写入完成，耗时：" + consume + "ms", false);
                                }
                            }
                        });
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logDisplay("开始写入文件", false);
                    }
                });
                for (int i = 0; i < CRAZY_W_TIMES; i++) {
                    LOG_D("测试D", "随即写入logD =======>" + mRandom.nextInt());
                    LOG_E("测试E", "随即写入logE =================>" + mRandom.nextInt());
                }
            }
        }).start();
    }

    private void refreshLength(long newLength) {
        mFileLength += newLength;
        logDisplay("log长度：" + mFileLength + " L", true);
    }

    private void logDisplay(String msg, boolean clean) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (clean) {
            mTvLength.setText(msg);
        } else {
            String his = mTvLength.getText().toString();
            if (TextUtils.isEmpty(his)) {
                mTvLength.setText(msg);
            } else {
                mTvLength.setText(his + "\n" + msg);
            }
        }
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
