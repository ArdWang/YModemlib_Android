package com.bw.yml;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread for reading input Stream and encapsulating into a ymodem package
 *
 */

public class FileStreamThread extends Thread {

    private final Context mContext;
    private InputStream inputStream = null;
    private DataRaderListener listener;
    private final String filePath;
    private final AtomicBoolean isDataAcknowledged = new AtomicBoolean(false);
    private boolean isKeepRunning = false;
    private int fileByteSize = 0;

    FileStreamThread(Context mContext, String filePath, DataRaderListener listener) {
        this.mContext = mContext;
        this.filePath = filePath;
        this.listener = listener;
    }

    int getFileByteSize(){
        if (fileByteSize == 0 || inputStream == null) {
            initStream();
        }
        return fileByteSize;
    }

    @Override
    public void run() {
        try {
            prepareData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareData() throws IOException {
        initStream();
        //1024 修改为 n
        byte[] block = new byte[YModem.mSize];
        int dataLength;
        byte blockSequence = 1;//The data package of a file is actually started from 1 文件的数据包实际上是从1开始的。
        isDataAcknowledged.set(true);
        isKeepRunning = true;
        while (isKeepRunning) {

            if (!isDataAcknowledged.get()) {
                try {
                    //We need to sleep for a while as the sending 1024 bytes data from ble would take several seconds
                    //In my circumstances, this can be up to 3 seconds.
                    ////我们需要睡眠一段时间，因为从BLE发送1024字节数据需要几秒钟。
                    //在我的情况下，这可以长达3秒。
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if ((dataLength = inputStream.read(block)) == -1) {
                Lg.f("The file data has all been read...");
                if (listener != null) {
                    onStop();
                    listener.onFinish();
                }
                break;
            }

            byte[] pack = YModemUtil.getDataPackage(block, dataLength, blockSequence);

            if (listener != null) {
                listener.onDataReady(pack);
            }

            blockSequence++;
            isDataAcknowledged.set(false);
        }

    }

    /**
     * When received response from the terminal ,we should keep the thread keep going
     */
    void keepReading() {
        isDataAcknowledged.set(true);
    }

    void release() {
        onStop();
        listener = null;
    }

    private void onStop() {
        isKeepRunning = false;
        isDataAcknowledged.set(false);
        fileByteSize = 0;
        onReadFinished();
    }

    private void initStream() {
        if (inputStream == null) {
            try {
                inputStream = YModemUtil.getInputStream(mContext, filePath);
                fileByteSize = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onReadFinished() {
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface DataRaderListener {
        void onDataReady(byte[] data);
        void onFinish();
    }

}
