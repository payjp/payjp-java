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
  <version>0.7.2</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "jp.pay:payjp-java:0.7.2"
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
        Payjp.apiKey = "your_secret_key";
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

### Retry on HTTP Status Code 429

- See [Rate Limit Guideline](https://pay.jp/docs/guideline-rate-limit#2-%E3%83%AA%E3%83%88%E3%83%A9%E3%82%A4)
- When you exceeded rate-limit, you can retry request by setting `Payjp.maxRetry`
  like `Payjp.maxRetry = 3;` .
- The retry interval base value is `Payjp.retryInitialDelay`  
  Adjust the value like `Payjp.retryInitialDelay = 4;`  
  The smaller is shorter.
- The retry interval calcurating is based on "Exponential backoff with equal jitter" algorithm.  
  See https://aws.amazon.com/jp/blogs/architecture/exponential-backoff-and-jitter/

Testing
=======

You must have Maven installed. To run the tests, simply run `mvn test`. You can run particular tests by passing `-D test=Class#method` -- for example, `-D test=PayjpTest#testChargeCreate`.
