FROM openjdk:11
ADD target/hyperboard-0.1.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]