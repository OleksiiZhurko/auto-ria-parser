FROM gradle:8.2-jdk11-alpine as build
COPY --chown=gradle:gradle . /usr/local/app/Wrapper
WORKDIR /usr/local/app/Wrapper
RUN gradle clean build -x test

FROM openjdk:11-jdk-oracle
COPY --from=build /usr/local/app/Wrapper/build/libs/Wrapper.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/Wrapper.jar"]