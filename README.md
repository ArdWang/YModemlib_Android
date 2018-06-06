# YModemLib
This is BWYModemLibrary

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
	        implementation 'com.github.ArdWang:YModemLib:v1.0.0'
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
	    <version>v1.0.0</version>
	</dependency>

```

其它的操作方法请看app里面的操作
