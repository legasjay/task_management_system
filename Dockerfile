FROM alpine:3.20
RUN apk add openjdk21
COPY build/libs/tms.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]