package com.intsig.scanner.log4aproject.util;

import android.app.Application;

/**
 * @author lingzhuang_bu
 * Description:
 * @date 2021/1/4
 */
public class Log4aApplication extends Application {

    public static final String TAG = Log4aApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
//        initCamScannerLog(this);
    }

    /**
     * 初始化log日志
     * > 内部的log库初始化时就会读取SD卡中的配置文件，SDK内部已经有了try catch处理
     */
    public static void initCamScannerLog(Application context) {
        LogUtils.initLogger(context);
        LogUtils.LOGD(TAG, "<-------------The start of CamScanner  ----------------------->");
    }
}
