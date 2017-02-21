FROM java:8-alpine
MAINTAINER Sean Schulte <sirsean@gmail.com>

ADD target/uberjar/twitteruption.jar /twitteruption/app.jar

CMD ["java", "-jar", "/twitteruption/app.jar"]
