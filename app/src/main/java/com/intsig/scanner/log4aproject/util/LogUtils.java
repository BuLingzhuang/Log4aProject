package com.intsig.scanner.log4aproject.util;


import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.intsig.log4a.BuildConfig;
import com.intsig.log4a.PropertyConfigure;

import java.util.Properties;

/***
 * 日志的工具类
 *
 * @author Eleven
 *
 */
public class LogUtils {
    private static boolean LOG_DEBUG = true;
    private static boolean LOG_ERROR = true;
    private static boolean LOG_VERBOSE = true;
    private static boolean LOG_INFO = true;
    private static boolean LOG_WARN = true;

    private static com.intsig.log4a.Logger sLogger;
    private static com.intsig.log4a.Logger sExtraLogger;

    private static boolean sUnableSendLog;

    public static void initLogger(Application context) {
        //initCheck();
        if (sUnableSendLog) return;
        Properties prop = new Properties();
        String logPath = context.getFilesDir().getParent() + "/.log/";
        prop.put(PropertyConfigure.PROP_LEVEL, PropertyConfigure.VAL_LEVEL_DEBUG);//指定Log级别为debug
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_DIR, logPath);////指定Log存放路径
        prop.put(PropertyConfigure.PROP_APPENDER, PropertyConfigure.VAL_APPENDER_FAST_FILE);//指定Log存放在文件中
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_MAXSIZE, "5M");
        prop.put(PropertyConfigure.PROP_APPENDER_CACHE_MAXSIZE, "1M");
        prop.put(PropertyConfigure.PROP_APPENDER_FILE_NUMBERS, "3");
        com.intsig.log4a.Log4A.init(prop);
        sLogger = com.intsig.log4a.Log4A.getLogger("LOG");
        sExtraLogger = com.intsig.log4a.Log4A.getExtraLogger("ExtraLog");
    }

    public static void LOGD_debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            LOGD(tag, msg);
        }
    }

    public static void LOGD(String tag, String msg) {
        if (sUnableSendLog) return;
        try {
            if (LOG_DEBUG) {
                if (sLogger != null) {
                    sLogger.debug(tag, msg);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.debug(tag, msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGI_debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            LOGI(tag, msg);
        }
    }

    public static void LOGI(String tag, String msg) {
        LOGD(tag, msg);
    }

    public static void LOGW_debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            LOGW(tag, msg);
        }
    }

    public static void LOGW(String tag, String msg) {
        if (sUnableSendLog) return;
        try {
            if (LOG_WARN) {
                if (sLogger != null) {
                    sLogger.warn(tag, msg);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.warn(tag, msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGW_debug(String tag, Throwable tr) {
        if (BuildConfig.DEBUG) {
            LOGW(tag, tr);
        }
    }

    public static void LOGW(String tag, Throwable tr) {
        if (sUnableSendLog) return;
        try {
            if (LOG_WARN) {
                if (sLogger != null) {
                    sLogger.warn(tag, tr);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.warn(tag, tr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGW_debug(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            LOGW(tag, msg, tr);
        }
    }

    public static void LOGW(String tag, String msg, Throwable tr) {
        if (sUnableSendLog) return;
        try {
            if (LOG_WARN) {
                if (sLogger != null) {
                    sLogger.warn(tag, msg, tr);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.warn(tag, msg, tr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGE_debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            LOGE(tag, msg);
        }
    }

    public static void LOGE(String tag, String msg) {
        if (sUnableSendLog) return;
        try {
            if (LOG_ERROR) {
                if (sLogger != null) {
                    sLogger.error(tag, msg);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.error(tag, msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGE_debug(String tag, Throwable tr) {
        if (BuildConfig.DEBUG) {
            LOGE(tag, tr);
        }
    }

    public static void LOGE(String tag, Throwable tr) {
        if (sUnableSendLog) return;
        try {
            if (LOG_ERROR) {
                if (sLogger != null) {
                    sLogger.error(tag, tr);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.error(tag, tr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LOGE_debug(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            LOGE(tag, msg, tr);
        }
    }

    public static void LOGE(String tag, String msg, Throwable tr) {
        if (sUnableSendLog) return;
        try {
            if (LOG_ERROR) {
                if (sLogger != null) {
                    sLogger.error(tag, msg, tr);
                }
                if (sExtraLogger != null) {
                    sExtraLogger.error(tag, msg, tr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initCheck() {
        String[] issuePhoneModel = new String[]{"vivo", "1818", "V9"};
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            for (String issueModel : issuePhoneModel) {
                if (issueModel.equalsIgnoreCase(manufacturer) || manufacturer.contains(issueModel)) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                        sUnableSendLog = true;
                    break;
                }
            }
        }
    }
}
