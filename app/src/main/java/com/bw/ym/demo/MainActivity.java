package com.bw.ym.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bw.ym.R;
import com.bw.yml.YModem;
import com.bw.yml.YModemListener;

/**
 * 使用介绍
 * 版本 v2.0.0->
 * 如果你想使用请你配合你的蓝牙一起使用
 * 具体操操作 有相对应的Demo
 * 这里只是一个实列 这是代表经典蓝牙
 * 本demo只能实现怎么发送数据的过程 具体经典蓝牙连接socket需要去查看文档
 */

public class MainActivity extends AppCompatActivity implements ConnectThread.OnSendReceiveDataListener {

    private YModem yModem;

    private ConnectThread thread;

    private BluetoothAdapter bluetoothAdapter;

    private final static String TAG = "MainActivity";

    private BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData(){
        initBluetooth();
        if(bluetoothSocket!=null)
            thread = new ConnectThread(bluetoothSocket);
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(this, "当前手机设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }else{
            //手机设备支持蓝牙，判断蓝牙是否已开启
            if(bluetoothAdapter.isEnabled()){
                Toast.makeText(this, "手机蓝牙已开启", Toast.LENGTH_SHORT).show();
            }else{
                //蓝牙没有打开，去打开蓝牙。推荐使用第二种打开蓝牙方式
                //第一种方式：直接打开手机蓝牙，没有任何提示
//                bluetoothAdapter.enable();  //BLUETOOTH_ADMIN权限
                //第二种方式：友好提示用户打开蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        }
    }

    private void searchBtDevice() {
        if(bluetoothAdapter.isDiscovering()){ //当前正在搜索设备...
            return;
        }
        //开始搜索
        bluetoothAdapter.startDiscovery();
    }



    /**
     * 开始Ymodel放在蓝牙点击按钮 启动的时候使用
     */
    private void startYmodem(){
        String customData = "Customized Data";
        yModem = new YModem.Builder()
                .with(this)
                .filePath("你的文件夹路径") //存放到手机的文件路径 stroge/0/.../xx.bin 这种路径
                .fileName("你的文件名字")
                .checkMd5("") //Md5可以写可以不写 看自己的通讯协议
                .sendSize(1024) //可以修改成你需要的大小
                .callback(new YModemListener() {
                    @Override
                    public void onDataReady(byte[] data) {
                        thread.write(data);
                    }

                    @Override
                    public void onProgress(int currentSent, int total) {
                        //进度条处理
                    }

                    @Override
                    public void onSuccess() {
                        //成功的显示
                    }

                    @Override
                    public void onFailed(String reason) {

                    }
                }).build();

        // 默认为空
        yModem.start(null);
        // 有需要的时候再添加
        //yModem.start(customData);
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


    @Override
    public void onSendDataSuccess(byte[] data) {
        //成功数据的打印
    }

    @Override
    public void onSendDataError(byte[] data, String errorMsg) {

    }

    @Override
    public void onReceiveDataSuccess(byte[] data) {
        if(data.length>0)
            yModem.onDataReady(data);
    }

    @Override
    public void onReceiveDataError(String errorMsg) {

    }


}
