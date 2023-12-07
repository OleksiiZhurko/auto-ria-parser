FROM maven:3.8.5-openjdk-11-slim
COPY pom.xml /usr/local/app/CarPredict/pom.xml
COPY src /usr/local/app/CarPredict/src
RUN mvn -f /usr/local/app/CarPredict clean package -DskipTests
ENTRYPOINT ["sh", "-c", "java -jar /usr/local/app/CarPredict/target/CarPredict.jar"]
