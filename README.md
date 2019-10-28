# YModemLib <br>
<a href="https://developer.android.com/index.html" rel="nofollow"><img src="https://camo.githubusercontent.com/4e7c3559fec3db6e04cd6d800d00fe6515f75260/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d616e64726f69642d627269676874677265656e2e737667" alt="" data-canonical-src="https://img.shields.io/badge/platform-android-brightgreen.svg" style="max-width:100%;"></a> [![](https://jitpack.io/v/ArdWang/YModemLib.svg)](https://jitpack.io/#ArdWang/YModemLib)
<br>
Ymodem蓝牙通讯协议 使用版本 19+ 这里所指示的只是一个通讯协议 具体操作需要查看Demo版本
版本更新了v2.0.0

### 支持经典蓝牙socket通讯 和 ble

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
	        implementation 'com.github.ArdWang:YModemLib:version'
	}

```

#### Maven使用<br/>

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
