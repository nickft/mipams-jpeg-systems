FROM adoptopenjdk/openjdk11:alpine
RUN addgroup -S mipams && adduser -S mipams -G mipams
USER mipams:mipams
VOLUME /tmp
ARG JAR_FILE=target/jumbf-privsec-0.0.1-SNAPSHOT
ADD ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]