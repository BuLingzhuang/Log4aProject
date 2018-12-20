//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class FileAppender extends Appender {
    OutputStream out = null;
    boolean flushImmediately = false;
    String current_log_file;
    byte[] CRLF = new byte[]{13, 10};

    public FileAppender(PropertyConfigure configure, int buffersize) {
        super(configure, buffersize);
        this.init(configure);
    }

    public String getCurrentLogFile() {
        return this.current_log_file;
    }

    OutputStream createNewLogFile(File dir) throws FileNotFoundException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String name = "log-" + sdf.format(new Date()) + ".log";
        File log = new File(dir, name);
        this.current_log_file = log.getAbsolutePath();
        return new FileOutputStream(log);
    }

    public synchronized void append(LogEvent event) {
        if (this.enable(event.level)) {
            try {
                this.out.write(event.dump(this.mConfigure).getBytes());
                this.out.write(this.CRLF);
                if (this.flushImmediately) {
                    this.out.flush();
                }
            } catch (IOException var3) {
                var3.printStackTrace();
            } catch (NullPointerException var4) {
                var4.printStackTrace();
            }
        }

    }

    void init(PropertyConfigure configure) {
        String dir = configure.getLogDir();

        try {
            File log_dir = new File(dir);
            boolean next = true;
            if (!log_dir.exists()) {
                next = log_dir.mkdirs();
            }

            if (!next) {
                return;
            }

            String[] logs = log_dir.list(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".log");
                }
            });
            if (logs != null && logs.length >= 1) {
                int num = logs.length;
                Arrays.sort(logs, new Comparator<String>() {
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                });
                String log = logs[num - 1];
                File f = new File(log_dir, log);
                long s = f.length();
                if (s <= configure.getFileMaxSize()) {
                    this.out = new FileOutputStream(f, true);
                    this.current_log_file = f.getAbsolutePath();
                } else {
                    for(int tmp = num; tmp >= configure.getFileMaxNum(); --tmp) {
                        File first = new File(log_dir, logs[num - tmp]);
                        first.delete();
                    }

                    this.out = this.createNewLogFile(log_dir);
                }
            } else {
                this.out = this.createNewLogFile(log_dir);
            }

            if (configure.isCompressed()) {
                this.out = new GZIPOutputStream(this.out);
            }
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            var14.printStackTrace();
        }

        this.flushImmediately = configure.flushImmediately();
    }

    public void reopen(PropertyConfigure configure) {
        super.reopen(configure);
        this.init(configure);
    }

    public void close() {
        super.close();

        try {
            if (this.out != null) {
                this.out.flush();
                this.out.close();
            }

            this.out = null;
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
