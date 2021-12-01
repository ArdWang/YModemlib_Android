package com.bw.yml;

import android.content.Context;
import java.io.IOException;

/**
 * ========================================================================================
 * THE YMODEM:
 * Send 0x05>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>* 发送0x05
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< C
 * SOH 00 FF "foo.c" "1064'' NUL[118] CRC CRC >>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< C
 * STX 01 FE data[n] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * ACK STX 02 FD data[n] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * ACK STX 03 FC data[n] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * STX 04 FB data[n] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * SOH 05 FA data[100] 1A[28] CRC CRC>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * EOT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< NAK
 * EOT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< C
 * SOH 00 FF NUL[128] CRC CRC >>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * ===========================================================================================
 *
 * 传输协议 编辑 ArdWang
 * 于 2018/6/5 15:49完成
 *
 * version v2.0.0
 * version v2.0.1
 *
 */

public class YModem implements FileStreamThread.DataRaderListener {

    private static final int STEP_HELLO = 0x00;
    private static final int STEP_FILE_NAME = 0x01;
    private static final int STEP_FILE_BODY = 0x02;
    private static final int STEP_EOT = 0x03;
    private static final int STEP_END = 0x04;
    private static int CURR_STEP = STEP_HELLO;

    private static final byte ACK = 0x06; /* ACKnowlege */
    private static final byte NAK = 0x15; /* Negative AcKnowlege */
    private static final byte CAN = 0x18; /* CANcel character */
    private static final byte ST_C = 'C';
    private static final String MD5_OK = "MD5_OK";
    private static final String MD5_ERR = "MD5_ERR";

    private final Context mContext;
    private final String filePath;
    private final String fileNameString;
    private final String fileMd5String;
    private final YModemListener listener;

    private final TimeOutHelper timerHelper = new TimeOutHelper();
    private FileStreamThread streamThread;

    //bytes has been sent of this transmission
    private int bytesSent = 0;
    //package data of current sending, used for int case of fail
    private byte[] currSending = null;
    private int packageErrorTimes = 0;
    private static final int MAX_PACKAGE_SEND_ERROR_TIMES = 6;
    //the timeout interval for a single package
    private static final int PACKAGE_TIME_OUT = 6000;
    static Integer mSize = 1024;

    /**
     * Construct of the YModemBLE,you may don't need the fileMD5 checking,remove it
     * YMODESMLE的构造，您可能不需要FLIMD5检查，删除它
     *
     * @param filePath       absolute path of the file
     * @param fileNameString file name for sending to the terminal
     * @param fileMd5String  md5 for terminal checking after transmission finished 传输结束后的终端检查MD5
     */
    private YModem(Context context, String filePath,
                  String fileNameString, String fileMd5String,Integer size,
                  YModemListener listener) {
        this.filePath = filePath;
        this.fileNameString = fileNameString;
        this.fileMd5String = fileMd5String;
        if(size==0)
            mSize = 1024;
        mSize = size;
        this.mContext = context;
        this.listener = listener;
    }

    /**
     * Start the transmission
     */
    public void start() {
        sendData();
    }

    /**
     * Stop the transmission when you don't need it or shut it down in an accident
     * 停止传输当你不需要它或关闭它在一次事故
     */
    public void stop() {
        bytesSent = 0;
        currSending = null;
        packageErrorTimes = 0;
        if (streamThread != null) {
            streamThread.release();
        }
        timerHelper.stopTimer();
        timerHelper.unRegisterListener();
    }

    /**
     * Method for the outer caller when received data from the terminal
     * 接收来自终端的数据时外部呼叫者的方法
     */
    public void onReceiveData(byte[] respData) {
        //Stop the package timer
        timerHelper.stopTimer();
        if (respData != null && respData.length > 0) {
            Lg.f("YModem received " + respData.length + " bytes.");
            switch (CURR_STEP) {
                case STEP_HELLO:
                    handleData(respData);
                    break;
                case STEP_FILE_NAME:
                    handleFileName(respData);
                    break;
                case STEP_FILE_BODY:
                    handleFileBody(respData);
                    break;
                case STEP_EOT:
                    handleEOT(respData);
                    break;
                case STEP_END:
                    handleEnd(respData);
                    break;
                default:
                    break;
            }
        } else {
            Lg.f("The terminal do responsed something, but received nothing??");
        }
    }

    /**
     * ==============================================================================
     * Methods for sending data begin
     * ==============================================================================
     */
    private void sendData() {
        streamThread = new FileStreamThread(mContext, filePath, this);
        CURR_STEP = STEP_HELLO;
        Lg.f("StartData!!!");
        byte[] hello = YModemUtil.getYModelData();
        sendPackageData(hello);
    }

    private void sendFileName() {
        CURR_STEP = STEP_FILE_NAME;
        Lg.f("sendFileName");
        try {
            int fileByteSize = streamThread.getFileByteSize();
            byte[] fileNamePackage = YModemUtil.getFileNamePackage(fileNameString, fileByteSize
                    , fileMd5String);
            sendPackageData(fileNamePackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSendFileData() {
        CURR_STEP = STEP_FILE_BODY;
        Lg.f("startSendFileData");
        streamThread.start();
    }

    //Callback from the data reading thread when a data package is ready
    @Override
    public void onDataReady(byte[] data) {
        sendPackageData(data);
    }

    private void sendEOT() {
        CURR_STEP = STEP_EOT;
        Lg.f("sendEOT");
        if (listener != null) {
            listener.onDataReady(YModemUtil.getEOT());
        }
    }

    private void sendEND() {
        CURR_STEP = STEP_END;
        Lg.f("sendEND");
        if (listener != null) {
            try {
                listener.onDataReady(YModemUtil.getEnd());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPackageData(byte[] packageData) {
        if (listener != null && packageData != null) {
            currSending = packageData;
            //Start the timer, it will be cancelled when reponse received,
            // or trigger the timeout and resend the current package data
            //启动计时器，当收到回复时将被取消，
            //或触发超时并重新发送当前包数据
            timerHelper.startTimer(timeoutListener, PACKAGE_TIME_OUT);
            listener.onDataReady(packageData);
        }
    }

    /**
     * ==============================================================================
     * Method for handling the response of a package
     * ==============================================================================
     */
    private void handleData(byte[] value) {
        int character = value[0];
        if (character == ST_C) {//Receive "C" for "HELLO"
            Lg.f("Received 'C'");
            packageErrorTimes = 0;
            sendFileName();
        } else {
            handleOthers(character);
        }
    }

    //The file name package was responsed
    private void handleFileName(byte[] value) {
        if (value.length == 2 && value[0] == ACK && value[1] == ST_C) {//Receive 'ACK C' for file name
            Lg.f("Received 'ACK C'");
            packageErrorTimes = 0;
            startSendFileData();
        } else if (value[0] == ST_C) {//Receive 'C' for file name, this package should be resent
            Lg.f("Received 'C'");
            handlePackageFail("Received 'C' without 'ACK' after sent file name");
        } else {
            handleOthers(value[0]);
        }
    }

    private void handleFileBody(byte[] value) {
        if (value.length == 1 && value[0] == ACK) {//Receive ACK for file data
            Lg.f("Received 'ACK'");
            packageErrorTimes = 0;
            bytesSent += currSending.length;
            try {
                if (listener != null) {
                    listener.onProgress(bytesSent, streamThread.getFileByteSize());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            streamThread.keepReading();

        } else if (value.length == 1 && value[0] == ST_C) {
            Lg.f("Received 'C'");
            //Receive C for file data, the ymodem cannot handle this circumstance, transmission failed...
            handlePackageFail("Received 'C' after sent file data");
        } else {
            handleOthers(value[0]);
        }
    }

    private void handleEOT(byte[] value) {
        if (value[0] == ACK) {
            Lg.f("Received 'ACK'");
            packageErrorTimes = 0;
            sendEND();
        } else if (value[0] == ST_C) {//As we haven't received ACK, we should resend EOT
            handlePackageFail("Received 'C' after sent EOT");
        } else if(value[0]==NAK){ //如果是NAK的话 再次发送一次EOT数据
            sendEOT();
        }else{
            handleOthers(value[0]);
        }
    }

    private void handleEnd(byte[] character) {
        if (character[0] == ACK) {//The last ACK represents that the transmission has been finished, but we should validate the file
            Lg.f("Received 'ACK'");
            packageErrorTimes = 0;
            //发送已经成功，完全结束
            if (listener != null) {
                listener.onSuccess();
            }
        } else if ((new String(character)).equals(MD5_OK)) {//The file data has been checked,Well Done!
            Lg.f("Received 'MD5_OK'");
            stop();
            if (listener != null) {
                listener.onSuccess();
            }
        } else if ((new String(character)).equals(MD5_ERR)) {//Oops...Transmission Failed...
            Lg.f("Received 'MD5_ERR'");
            stop();
            if (listener != null) {
                listener.onFailed("MD5 check failed!!!");
            }
        } else {
            handleOthers(character[0]);
        }
    }

    private void handleOthers(int character) {
        if (character == NAK) {//We need to resend this package as the terminal failed when checking the crc
            Lg.f("Received 'NAK'");
            handlePackageFail("Received NAK");
        } else if (character == CAN) {//Some big problem occurred, transmission failed...
            Lg.f("Received 'CAN'");
            if (listener != null) {
                listener.onFailed("Received CAN");
            }
            stop();
        }
    }

    //Handle a failed package data ,resend it up to MAX_PACKAGE_SEND_ERROR_TIMES times.
    //处理失败的包数据
    //If still failed, then the transmission failed.
    private void handlePackageFail(String reason) {
        packageErrorTimes++;
        Lg.f("Fail:" + reason + " for " + packageErrorTimes + " times");
        if (packageErrorTimes < MAX_PACKAGE_SEND_ERROR_TIMES) {
            sendPackageData(currSending);
        } else {
            //Still, we stop the transmission, release the resources
            stop();
            if (listener != null) {
                listener.onFailed(reason);
            }
        }
    }

    /* The InputStream data reading thread was done */
    @Override
    public void onFinish() {
        sendEOT();
    }

    //The timeout listener
    private final TimeOutHelper.ITimeOut timeoutListener = new TimeOutHelper.ITimeOut() {
        @Override
        public void onTimeOut() {
            Lg.f("------ time out ------");
            if (currSending != null) {
                handlePackageFail("package timeout...");
            }
        }
    };

    public static class Builder {
        private Context context;
        private String filePath;
        private String fileNameString;
        private String fileMd5String;
        private Integer size;
        private YModemListener listener;

        public Builder with(Context context) {
            this.context = context;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileNameString = fileName;
            return this;
        }

        public Builder sendSize(Integer size){
            this.size = size;
            return this;
        }

        public Builder checkMd5(String fileMd5String) {
            this.fileMd5String = fileMd5String;
            return this;
        }

        public Builder callback(YModemListener listener) {
            this.listener = listener;
            return this;
        }

        public YModem build() {
            return new YModem(context, filePath, fileNameString, fileMd5String, size, listener);
        }

    }

}
