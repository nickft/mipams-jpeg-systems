FROM adoptopenjdk/openjdk11:alpine

RUN addgroup -S mipams && adduser -S mipams -G mipams

WORKDIR /app
RUN mkdir /app/assets
RUN chown -R mipams:mipams /app/assets

USER mipams:mipams
VOLUME /tmp
ARG JAR_FILE=target/demo-1.0.jar
ADD ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]