package com.bw.ym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.bw.yml.YModem;
import com.bw.yml.YModemListener;

/**
 * 使用介绍
 * 版本 v1.0.0->
 * 如果你想使用请你配合你的蓝牙一起使用
 * 具体操操作 有相对应的Demo
 *
 * 这里只是一个实列
 */

public class MainActivity extends AppCompatActivity {

    private YModem yModem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * 开始Ymodel放在蓝牙点击按钮 启动的时候使用
     */
    private void startYmodem(){
        yModem = new YModem.Builder()
                .with(this)
                .filePath("你的文件夹路径") //存放到手机的文件路径 stroge/0/.../xx.bin 这种路径
                .fileName("你的文件名字")
                .checkMd5("") //Md5可以写可以不写 看自己的通讯协议
                .callback(new YModemListener() {
                    @Override
                    public void onDataReady(byte[] data) {

                    }

                    @Override
                    public void onProgress(int currentSent, int total) {

                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed(String reason) {

                    }
                }).build();
        yModem.start();
    }

    //用于接受到你蓝牙设备给你反馈的蓝牙信息
    public void onDataReceivedFromBLE(byte[] data) {
        yModem.onReceiveData(data);
    }

    /*stop the transmission*/
    public void onStopClick(View view) {
        yModem.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        yModem.stop();

        //你相应的一系列蓝牙的操作也要停止掉
    }
}
