# PAY.JP for Java

[![Build Status](https://github.com/payjp/payjp-java/actions/workflows/main.yml/badge.svg)](https://github.com/payjp/payjp-java/actions)

Requirements
============

Java 1.6 and later.

Installation
============

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>jp.pay</groupId>
  <artifactId>payjp-java</artifactId>
  <version>0.4.4</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "jp.pay:payjp-java:0.4.4"
```

### Others

You'll need to manually install the following JARs:

* The Payjp JAR from https://github.com/payjp/payjp-java/releases/latest
* [Google Gson](http://code.google.com/p/google-gson/) from <http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip>.
* [javax.mail](http://www.oracle.com/technetwork/java/javamail/index.html) from <http://central.maven.org/maven2/javax/mail/mail/1.4.7/mail-1.4.7.jar>

### [ProGuard](http://proguard.sourceforge.net/)

If you're planning on using ProGuard, make sure that you exclude the Payjp bindings. You can do this by adding the following to your `proguard.cfg` file:

    -keep class jp.pay.** { *; }

Usage
=====

In advance, you need to get token by [Checkout](https://pay.jp/docs/checkout).

PayjpExample.java

```java
import java.util.HashMap;
import java.util.Map;

import jp.pay.Payjp;
import jp.pay.model.Charge;
import jp.pay.net.RequestOptions;

public class PayjpExample {

    public static void main(String[] args) {
        Payjp.apiKey = "sk_test_c62fade9d045b54cd76d7036";
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", 3500);
        chargeMap.put("currency", "jpy");
        chargeMap.put("card", "<your_token_id>");
        try {
            Charge charge = Charge.create(chargeMap);
            System.out.println(charge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Testing
=======

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=PayjpTest#testChargeCreate`.
