package com.bw.ym.demo;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


/*
 *  Copyright © 2018 Radiance Instruments Ltd. All rights reserved.
 *  author ArdWang
 *  email 278161009@qq.com
 *  Created by ArdWang on 10/28/19.
 */

public class ConnectThread extends Thread{
    private static final String TAG = "ConnectedThread";
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    //是否是主动断开
    private boolean isStop = false;
    //发起蓝牙连接的线程
    private ConnectThread connectThread;

    public void terminalClose(ConnectThread connectThread){
        isStop = true;
        this.connectThread = connectThread;
    }

    public ConnectThread(BluetoothSocket socket){
        mmSocket = socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        //使用临时对象获取输入和输出流，因为成员流是静态类型

        //1、获取 InputStream 和 OutputStream
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

        } catch (IOException e) {
            Log.e(TAG,"ConnectedThread-->获取InputStream 和 OutputStream异常!");
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        if(mmInStream != null){
            Log.d(TAG,"ConnectedThread-->已获取InputStream");
        }

        if(mmOutStream != null){
            Log.d(TAG,"ConnectedThread-->已获取OutputStream");
        }

    }

    public void run(){
        //最大缓存区 存放流
        byte[] buffer = new byte[1024 * 2];  //buffer store for the stream
        //从流的read()方法中读取的字节数
        int bytes = 0;  //bytes returned from read()
        //持续监听输入流直到发生异常
        while(!isStop){
            try {
                if(mmInStream == null){
                    Log.e(TAG,"ConnectedThread:run-->输入流mmInStream == null");
                    break;
                }
                //先判断是否有数据，有数据再读取
                if(mmInStream.available() != 0){
                    //2、接收数据
                    bytes = mmInStream.read(buffer);  //从(mmInStream)输入流中(读取内容)读取的一定数量字节数,并将它们存储到缓冲区buffer数组中，bytes为实际读取的字节数
                    byte[] b = Arrays.copyOf(buffer,bytes);  //存放实际读取的数据内容
                    Log.w(TAG,"ConnectedThread:run-->收到消息,长度" + b.length + "->" + bytes2HexString(b, b.length));  //有空格的16进制字符串
                    if(onSendReceiveDataListener != null){
                        onSendReceiveDataListener.onReceiveDataSuccess(b);  //成功收到消息
                    }
                }

            } catch (IOException e) {
                Log.e(TAG,"ConnectedThread:run-->接收消息异常！" + e.getMessage());
                if(onSendReceiveDataListener != null){
                    onSendReceiveDataListener.onReceiveDataError("接收消息异常:" + e.getMessage());  //接收消息异常
                }
                //关闭流和socket
                boolean isClose = cancel();
                if(isClose){
                    Log.e(TAG,"ConnectedThread:run-->接收消息异常,成功断开连接！");
                }
                break;
            }
        }
        //关闭流和socket
        boolean isClose = cancel();
        if(isClose){
            Log.d(TAG,"ConnectedThread:run-->接收消息结束,断开连接！");
        }
    }

    //发送数据
    public boolean write(byte[] bytes){
        try {
            if(mmOutStream == null){
                Log.e(TAG, "mmOutStream == null");
                return false;
            }

            //发送数据
            mmOutStream.write(bytes);
            Log.d(TAG, "写入成功："+ bytes2HexString(bytes, bytes.length));
            if(onSendReceiveDataListener != null){
                onSendReceiveDataListener.onSendDataSuccess(bytes);  //发送数据成功回调
            }
            return true;

        } catch (IOException e) {
            Log.e(TAG, "写入失败："+ bytes2HexString(bytes, bytes.length));
            if(onSendReceiveDataListener != null){
                onSendReceiveDataListener.onSendDataError(bytes,"写入失败");  //发送数据失败回调
            }
            return false;
        }
    }

    /**
     * 释放
     * @return   true 断开成功  false 断开失败
     */
    public boolean cancel(){
        try {
            if(mmInStream != null){
                mmInStream.close();  //关闭输入流
            }
            if(mmOutStream != null){
                mmOutStream.close();  //关闭输出流
            }
            if(mmSocket != null){
                mmSocket.close();   //关闭socket
            }
            if(connectThread != null){
                connectThread.cancel();
            }

            connectThread = null;
            mmInStream = null;
            mmOutStream = null;
            mmSocket = null;

            Log.w(TAG,"ConnectedThread:cancel-->成功断开连接");
            return true;

        } catch (IOException e) {
            // 任何一部分报错，都将强制关闭socket连接
            mmInStream = null;
            mmOutStream = null;
            mmSocket = null;

            Log.e(TAG, "ConnectedThread:cancel-->断开连接异常！" + e.getMessage());
            return false;
        }
    }

    /**
     * 字节数组-->16进制字符串
     * @param b   字节数组
     * @param length  字节数组长度
     * @return 16进制字符串 有空格类似“0A D5 CD 8F BD E5 F8”
     */
    public static String bytes2HexString(byte[] b, int length) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase()).append(" ");
        }
        return result.toString();
    }

    private OnSendReceiveDataListener onSendReceiveDataListener;

    public void setOnSendReceiveDataListener(OnSendReceiveDataListener onSendReceiveDataListener) {
        this.onSendReceiveDataListener = onSendReceiveDataListener;
    }

    //收发数据监听者
    public interface OnSendReceiveDataListener{
        void onSendDataSuccess(byte[] data);  //发送数据结束
        void onSendDataError(byte[] data, String errorMsg); //发送数据出错
        void onReceiveDataSuccess(byte[] buffer);  //接收到数据
        void onReceiveDataError(String errorMsg);   //接收数据出错
    }

}
