FROM maven:3.9-amazoncorretto-21-alpine

WORKDIR /opt
COPY . .

RUN mvn clean package -DskipTests

RUN mv /opt/target/*.jar /opt/target/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/opt/target/app.jar"]
