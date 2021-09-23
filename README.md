# YModemLib <br>
<a href="https://developer.android.com/index.html" rel="nofollow"><img src="https://camo.githubusercontent.com/4e7c3559fec3db6e04cd6d800d00fe6515f75260/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d616e64726f69642d627269676874677265656e2e737667" alt="" data-canonical-src="https://img.shields.io/badge/platform-android-brightgreen.svg" style="max-width:100%;"></a> [![](https://jitpack.io/v/ArdWang/YModemLib.svg)](https://jitpack.io/#ArdWang/YModemLib)
<br>
Ymodem Bluetooth communication protocol uses version 19+. What is indicated here is only a communication protocol. For specific operations, you need to check the Demo version. The version has been updated to v2.0.0

Thank you LeonXtp for your help

iOS Version https://github.com/ArdWang/YModemlib_iOS

### Update 2020 8/11

A simplified version of the Ymodem upgrade protocol has been added


### Support classic Bluetooth socket communication and ble

1. This update modified some wrong methods。

2. Increase the data size you can choose to send, as shown in the following code, you can modify the size and format of the data received by your device。

```
 private void startYmodem(){
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
        yModem.start();
    }


```

3. 可以支持 Ble 以及 经典蓝牙。

4. CRC_Check16
```java

public static String CRC16_Check(byte[] pushData, int length){
	int Reg_CRC = 0xffff;
	int temp;
	int i,j;
	for(i=0;i<length;i++){
		temp = pushData[i];
		if(temp<0) temp+=256;
		temp &= 0xff;
		Reg_CRC ^= temp;
		for(j=0; j<8; j++){
			if((Reg_CRC & 0x0001))==0x0001){
				Reg_CRC = (Reg_CRC >> 1)^0xA001;
			}else{
				Reg_CRC >>=1;
			}
		}
	}
	return Integer.toHexString((Reg_CRC&0xffff));
}


 /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }



```





#### Gradle Use <br/>

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


```java
	dependencies {
	        implementation 'com.github.ArdWang:YModemLib:2.0.0'
	}

```

#### Maven Use <br/>

```java

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```java
	<dependency>
	    <groupId>com.github.ArdWang</groupId>
	    <artifactId>YModemLib</artifactId>
	    <version>version</version>
	</dependency>

```

For other operations, please see the operations in the app<br/>
into [YModemBleDemo](https://github.com/ArdWang/YModemBleUpdate "悬停显示")

<br><br>
YModem delegate
```java
/**
 * ========================================================================================
 * THE YMODEM:
 * Send 0x05>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>* 发送0x05
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< C
 * SOH 00 FF "foo.c" "1064'' NUL[118] CRC CRC >>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< C
 * STX 01 FE data[256] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * ACK STX 02 FD data[256] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * ACK STX 03 FC data[256] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ACK
 * STX 04 FB data[256] CRC CRC>>>>>>>>>>>>>>>>>>>>>>>
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
 **/

```
