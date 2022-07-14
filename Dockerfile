FROM openjdk:17.0.2-oracle

ADD target/sendToken-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
