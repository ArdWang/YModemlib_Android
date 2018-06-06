# YModemLib <br>
<a href="https://developer.android.com/index.html" rel="nofollow"><img src="https://camo.githubusercontent.com/4e7c3559fec3db6e04cd6d800d00fe6515f75260/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d616e64726f69642d627269676874677265656e2e737667" alt="" data-canonical-src="https://img.shields.io/badge/platform-android-brightgreen.svg" style="max-width:100%;"></a> <a href="https://android-arsenal.com/api?level=19" rel="nofollow"><img src="https://camo.githubusercontent.com/4ded46c2b1687a1778dacbe648c837ba971b8a99/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4150492d31342532422d626c75652e7376673f7374796c653d666c6174" alt="API" data-canonical-src="https://img.shields.io/badge/API-19%2B-blue.svg?style=flat" style="max-width:100%;"></a> <a href="https://github.com/jiangzehui/polygonsview"><img src="https://camo.githubusercontent.com/8c8f5b40e236f9cb7c5b4d9b00e5660f43b9908c/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f477261646c652d332e302e312d627269676874677265656e2e737667" alt="Twitter" data-canonical-src="https://img.shields.io/badge/Gradle-3.1.2-brightgreen.svg" style="max-width:100%;"></a> <img src="https://camo.githubusercontent.com/e606a995d076b54e6460dc18da05efb2fce796fa/68747470733a2f2f6a69747061636b2e696f2f762f766f6e646561722f5278546f6f6c732e737667" alt="YModemLib" data-canonical-src="https://jitpack.io/#ArdWang/YModemLib.svg" style="max-width:100%;">
This is BWYModemLibrary
<br>
Ymodem蓝牙通讯协议 使用版本 19+ 这里所指示的只是一个通讯协议 具体操作需要查看Demo版本

### 使用方法 在项目中添加

#### Gradle使用<br/>

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
	        implementation 'com.github.ArdWang:YModemLib:1.0.0'
	}

```

#### Maven使用<br/>

```java
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
	    <version>1.0.0</version>
	</dependency>

```

其它的操作方法请看app里面的操作<br/>
进入 [YModem蓝牙传输Demo](https://github.com/ArdWang/YModemBleUpdate "悬停显示")

<br><br>
YModem协议
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
