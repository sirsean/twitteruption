FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/twitteruption.jar /twitteruption/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/twitteruption/app.jar"]
