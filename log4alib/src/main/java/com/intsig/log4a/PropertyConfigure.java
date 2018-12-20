//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyConfigure {
    public static final String VAL_LEVEL_OFF = "off";
    public static final String VAL_LEVEL_ERROR = "error";
    public static final String VAL_LEVEL_WARN = "warn";
    public static final String VAL_LEVEL_INFO = "info";
    public static final String VAL_LEVEL_DEBUG = "debug";
    public static final String VAL_APPENDER_FILE = "file";
    public static final String VAL_APPENDER_ENC_FILE = "enc_file";
    public static final String VAL_APPENDER_CONSOLE = "console";
    public static final String VAL_APPENDER_LOGCAT = "logcat";
    public static final String VAL_THREAD_NAME = "name";
    public static final String VAL_THREAD_ID = "id";
    public static final String VAL_THREAD_NAME_ID = "name/id";
    public static final String PROP_LEVEL = "log4a.level";
    public static final String PROP_APPENDER = "log4a.appender";
    public static final String PROP_APPENDER_FILE_DIR = "log4a.appender.file.dir";
    public static final String PROP_APPENDER_FILE_NAME = "log4a.appender.file.name";
    public static final String PROP_APPENDER_FILE_ZIP = "log4a.appender.file.zip";
    public static final String PROP_APPENDER_FILE_MAXSIZE = "log4a.appender.file.maxsize";
    public static final String PROP_APPENDER_FILE_NUMBERS = "log4a.appender.file.maxnumbers";
    public static final String PROP_APPENDER_FILE_FLUSH_IMMEDIATELY = "log4a.appender.file.flush.immediately";
    public static final String PROP_TIME_FORMAT = "log4a.time.format";
    public static final String PROP_THREAD_NAME = "log4a.thread";
    public static final String PROP_FORMAT = "log4a.format";
    Properties properties;
    Level level;
    String logDir;
    long fileMaxSize;
    int fileMaxNUm;
    Layout mLayout;

    public PropertyConfigure(String name) {
        this((Properties) null, name);
    }

    public PropertyConfigure(Properties prop) {
        this(prop, (String) null);
    }

    public PropertyConfigure(Properties prop, String propPath) {
        this.properties = new Properties();
        this.level = Level.ERROR;
        this.logDir = null;
        this.fileMaxSize = -1L;
        this.fileMaxNUm = -1;
        if (prop != null) {
            Enumeration enume = prop.propertyNames();

            while (enume.hasMoreElements()) {
                Object key = enume.nextElement();
                this.properties.put(key, prop.get(key));
            }
        }

        if (propPath != null) {
            File f = new File(propPath);
            if (f.exists()) {
                try {
                    InputStream in = new FileInputStream(f);
                    this.properties.load(in);
                } catch (IOException var5) {
                    var5.printStackTrace();
                }
            }
        }

        this.init();
    }

    public Appender getAppender() {
        String appenderName = this.properties.getProperty("log4a.appender");
        if ("logcat".equals(appenderName)) {
            return new LogcatAppender(this);
        } else if ("file".equals(appenderName)) {
            return new FileAppender(this, 20);
        } else if ("console".equals(appenderName)) {
            return new ConsoleAppender(this);
        } else {
            return (Appender) ("enc_file".equals(appenderName) ? new EncFileAppender(this, 20) : new FileAppender(this, 20));
        }
    }

    void init() {
        String levelStr = this.properties.getProperty("log4a.level");
        if ("off".equals(levelStr)) {
            this.level = Level.OFF;
        } else if ("error".equals(levelStr)) {
            this.level = Level.ERROR;
        } else if ("warn".equals(levelStr)) {
            this.level = Level.WARN;
        } else if ("info".equals(levelStr)) {
            this.level = Level.INFO;
        } else if ("debug".equals(levelStr)) {
            this.level = Level.DEBUG;
        }

        String seq = this.properties.getProperty("log4a.format", "%l\t%d %t\t%g\t%m");
        String date_format = this.properties.getProperty("log4a.time.format", "MM-dd HH:mm:ss.SSS");
        String thread = this.properties.getProperty("log4a.thread", "name");
        this.mLayout = new Layout(seq, date_format, thread);
    }

    public Level getLevel() {
        return this.level;
    }

    public String layout(LogEvent e) {
        return this.mLayout.layout(e);
    }

    public String getLogDir() {
        if (this.logDir == null) {
            String path = this.properties.getProperty("log4a.appender.file.dir", (String) null);
            this.logDir = path;
        }

        return this.logDir;
    }

    public long getFileMaxSize() {
        if (this.fileMaxSize < 0L) {
            String size = this.properties.getProperty("log4a.appender.file.maxsize", "1M");

            try {
                size = size.trim().toUpperCase();
                char c = size.charAt(size.length() - 1);
                if (c == 'M' || c == 'K') {
                    size = size.substring(0, size.length() - 1);
                }

                long l = Long.parseLong(size);
                if (c == 'M') {
                    l *= 1048576L;
                } else if (c == 'K') {
                    l *= 1024L;
                }

                this.fileMaxSize = l;
            } catch (Exception var5) {
                var5.printStackTrace();
                this.fileMaxSize = 1048576L;
            }
        }

        return this.fileMaxSize;
    }

    public int getFileMaxNum() {
        if (this.fileMaxNUm < 0) {
            String num = this.properties.getProperty("log4a.appender.file.maxnumbers", "1");

            try {
                this.fileMaxNUm = Integer.parseInt(num);
            } catch (NumberFormatException var3) {
                var3.printStackTrace();
                this.fileMaxNUm = 1;
            }
        }

        return this.fileMaxNUm;
    }

    boolean isCompressed() {
        String zip = this.properties.getProperty("log4a.appender.file.zip", "false");

        try {
            return Boolean.parseBoolean(zip);
        } catch (Exception var3) {
            var3.printStackTrace();
            return true;
        }
    }

    boolean flushImmediately() {
        String flush = this.properties.getProperty("log4a.appender.file.flush.immediately", "false");

        try {
            return Boolean.parseBoolean(flush);
        } catch (Exception var3) {
            var3.printStackTrace();
            return false;
        }
    }

    class Layout {
        SimpleDateFormat dateFormat;
        int thread_format = 0;
        final int THREAD_NAME = 1;
        final int THREAD_ID = 2;
        final int THREAD_NAME_ID = 3;
        byte[] sequence;

        public Layout(String seq, String date_format, String thread) {
            this.dateFormat = new SimpleDateFormat(date_format);
            if ("name/id".equals(thread)) {
                this.thread_format = 3;
            } else if ("name".equals(thread)) {
                this.thread_format = 1;
            } else if ("id".equals(thread)) {
                this.thread_format = 2;
            }

            String[] elements = seq.split("%");
            this.sequence = new byte[elements.length];

            for (int i = 0; i < elements.length; ++i) {
                String s = elements[i].trim();
                if (s.length() > 0) {
                    this.sequence[i] = elements[i].getBytes()[0];
                }
            }

        }

        String layout(LogEvent event) {
            StringBuilder str = new StringBuilder();
            byte[] var6 = this.sequence;
            int var5 = this.sequence.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                byte e = var6[var4];
                switch (e) {
                    case 100:
                        str.append(this.dateFormat.format(new Date()));
                        break;
                    case 103:
                        str.append(event.name);
                        break;
                    case 108:
                        str.append(event.level.levelStr);
                        break;
                    case 109:
                        str.append(event.message);
                        str.append(event.stackTrace());
                        break;
                    case 116:
                        Thread t = Thread.currentThread();
                        if (this.thread_format == 1) {
                            str.append(t.getName());
                        } else if (this.thread_format == 2) {
                            str.append(t.getId());
                        } else if (this.thread_format == 3) {
                            str.append(t.getName() + "/" + t.getId());
                        }
                        break;
                    default:
                        continue;
                }

                str.append(" \t");
            }

            return str.toString();
        }
    }
}
