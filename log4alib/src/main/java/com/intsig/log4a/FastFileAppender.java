//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import com.intsig.encryptfile.ISEncryptFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class FastFileAppender extends Appender {
    private MappedByteBuffer mCacheMbb;
    private File mCurrentLogFile;
    private File mCacheFile;

    public FastFileAppender(PropertyConfigure configure, int bufferSize) {
        super(configure, bufferSize);
        this.init(configure);
    }

    public String getCurrentLogFile() {
        if (mCurrentLogFile != null) {
            return mCurrentLogFile.getAbsolutePath();
        } else {
            return null;
        }
    }

    File getCacheLogFile() {
        if (mCacheFile == null) {
            String name = CACHE_FILE_NAME_HEAD + CACHE_FILE_NAME + FILE_NAME_FOOT;
            mCacheFile = new File(mConfigure.getLogDir(), name);
        }
        return mCacheFile;
    }

    private void createNewLogFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String name = FILE_NAME_HEAD + "fast-" + sdf.format(new Date()) + FILE_NAME_FOOT;
        mCurrentLogFile = new File(mConfigure.getLogDir(), name);
    }

    public MappedByteBuffer getMbb() {
        try {
            File cacheLogFile = getCacheLogFile();
            if (!cacheLogFile.exists()) {
                cacheLogFile.createNewFile();
            }
            RandomAccessFile rafi = new RandomAccessFile(cacheLogFile, "rw");
            FileChannel fci = rafi.getChannel();
            return fci.map(FileChannel.MapMode.READ_WRITE, 0, mConfigure.getCacheMaxSize());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized void append(LogEvent event) {
        if (this.enable(event.level)) {
            String msg = event.dumpCRLF(this.mConfigure);
            if (msg.length() > mConfigure.getCacheMaxSize()) {
                //单条log超过缓存上限，需要裁切为多条进行写入
                int subLength = (int) (mConfigure.getCacheMaxSize() / 2);
                int lastLength = msg.length() % subLength;
                int partNum = msg.length() / subLength;
                for (int i = 0; i < partNum; i++) {
                    write(msg.substring(i * subLength, (i + 1) * subLength));
                }
                if (lastLength > 0) {
                    write(msg.substring(partNum * subLength));
                }
            } else {
                write(msg);
            }

        }
    }

    @Override
    public void reopen(PropertyConfigure configure) {
        super.reopen(configure);
        this.init(configure);
    }

    @Override
    public void close() {
        super.close();
        flush();
    }

    void init(PropertyConfigure configure) {
        String dir = configure.getLogDir();

        File log_dir = new File(dir);
        boolean next = true;
        if (!log_dir.exists()) {
            next = log_dir.mkdirs();
        }

        if (!next) {
            return;
        }

        String[] logs = log_dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(FILE_NAME_HEAD) && filename.endsWith(FILE_NAME_FOOT);
            }
        });
        if (logs != null && logs.length >= 1) {
            int num = logs.length;
            Arrays.sort(logs, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            String log = logs[num - 1];
            File f = new File(log_dir, log);
            long s = f.length();
            //加密文件不能续写，需要新建文件
            if (s <= configure.getFileMaxSize() && !ISEncryptFile.FileEncryptedByISCrypter(f.getAbsolutePath())) {
                mCurrentLogFile = f;
            } else {
                for (int tmp = num; tmp >= configure.getFileMaxNum(); --tmp) {
                    File first = new File(log_dir, logs[num - tmp]);
                    first.delete();
                }
                createNewLogFile();
            }
        } else {
            createNewLogFile();
        }
        flush();
    }


    private void write(String msg) {
        try {
            if (msg == null) {
                return;
            }
            if (mCacheMbb == null) {
                mCacheMbb = getMbb();
            }
            // TODO: 2020/12/15 test
            msg = "F" + msg;
            if (mCacheMbb != null) {
                mCacheMbb.put(msg.getBytes());
            }
        } catch (BufferOverflowException e) {
            //缓存区溢出，此时需要把缓存区flush到主日志，清空缓存区
            flush();
            mCacheMbb = getMbb();
            if (mCacheMbb != null) {
                mCacheMbb.put(msg.getBytes());
            }
        }
    }

    private void flush() {
        if (!getCacheLogFile().exists() || getCacheLogFile().length() <= 0) {
            return;
        }
        if (mCurrentLogFile == null) {
            return;
        }
        try {
            if (!mCurrentLogFile.exists()) {
                mCurrentLogFile.getParentFile().mkdirs();
                mCurrentLogFile.createNewFile();
            }
            RandomAccessFile rafI = new RandomAccessFile(mCacheFile, "rw");
            RandomAccessFile rafO = new RandomAccessFile(mCurrentLogFile, "rw");

            FileChannel fcI = rafI.getChannel();
            FileChannel fcO = rafO.getChannel();

            int cacheSize = (int) fcI.size();
            int logSize = (int) fcO.size();

            MappedByteBuffer mbbI = fcI.map(FileChannel.MapMode.READ_WRITE, 0, cacheSize);
            int offset = 0;
            //mmap申请内存会优先填充字节类型的0，所以在flush的时候需要倒序找到非0的位置
            for (int i = cacheSize - 1; i >= 0; i--) {
                if (mbbI.get(i) != 0) {
                    offset = cacheSize - 1 - i;
                    break;
                }
            }
            int byteRealSize = cacheSize - offset;
            MappedByteBuffer mbbO = fcO.map(FileChannel.MapMode.READ_WRITE, logSize, byteRealSize);
            for (int i = 0; i < byteRealSize; i++) {
                mbbO.put(mbbI.get(i));
            }
            //解除内存映射
            unmap(mbbI);
            unmap(mbbO);
            fcI.close();
            fcO.close();
            rafI.close();
            rafO.close();
            //清空缓存文件
            mCacheFile.delete();
            if (mCacheMbb != null) {
                unmap(mCacheMbb);
                mCacheMbb = null;
            }
            if (logSize + byteRealSize > mConfigure.getFileMaxNum()) {
                createNewLogFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除内存与文件的映射
     */
    private void unmap(MappedByteBuffer mbb) {
        if (mbb == null) {
            return;
        }
        try {
            Class<?> clazz = Class.forName("sun.nio.ch.FileChannelImpl");
            Method m = clazz.getDeclaredMethod("unmap", MappedByteBuffer.class);
            m.setAccessible(true);
            m.invoke(null, mbb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
