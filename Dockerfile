FROM amazoncorretto:21-alpine-jdk
COPY build/libs/TransferBot-0.0.1-SNAPSHOT.jar /transferBot.jar
EXPOSE 8080
CMD ["java", "-jar", "/transferBot.jar"]