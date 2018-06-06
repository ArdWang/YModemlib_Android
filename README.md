# YModemLib
This is BWYModemLibrary

### 使用方法

#### Gradle使用<br/>

`
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
`

`
dependencies {
	        implementation 'com.github.ArdWang:YModemLib:v1.0.0'
	}
`

#### Maven使用<br/>

`
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
`

`
<dependency>
	    <groupId>com.github.ArdWang</groupId>
	    <artifactId>YModemLib</artifactId>
	    <version>v1.0.0</version>
	</dependency>
`
