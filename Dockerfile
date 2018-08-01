FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/right-site-0.0.1-SNAPSHOT-standalone.jar /right-site/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/right-site/app.jar"]
