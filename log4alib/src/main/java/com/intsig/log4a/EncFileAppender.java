//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import com.intsig.encryptfile.ISEncryptFile;
import com.intsig.encryptfile.ISEncryptFile.FileOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EncFileAppender extends FileAppender {
    public EncFileAppender(PropertyConfigure configure, int buffersize) {
        super(configure, buffersize);
    }

    @Override
    public void init(PropertyConfigure configure) {
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
                    return filename.endsWith(FILE_NAME_FOOT);
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
                if (s > configure.getFileMaxSize()) {
                    for (int tmp = num; tmp >= configure.getFileMaxNum(); --tmp) {
                        File first = new File(log_dir, logs[num - tmp]);
                        first.delete();
                    }

                    this.out = this.createNewLogFile(log_dir);
                } else {
                    this.current_log_file = f.getAbsolutePath();
                    if (!ISEncryptFile.FileEncryptedByISCrypter(this.current_log_file)) {
                        ISEncryptFile.EncryptFileToFile(this.current_log_file, this.current_log_file);
                    }

                    this.out = new FileOutputStream(this.current_log_file, true);
                }
            } else {
                this.out = this.createNewLogFile(log_dir);
            }
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        }

        this.flushImmediately = configure.flushImmediately();
    }

    @Override
    public OutputStream createNewLogFile(File dir) throws FileNotFoundException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String name = FILE_NAME_HEAD + sdf.format(new Date()) + FILE_NAME_FOOT;
        File log = new File(dir, name);
        this.current_log_file = log.getAbsolutePath();
        return new FileOutputStream(this.current_log_file, true);
    }
}
