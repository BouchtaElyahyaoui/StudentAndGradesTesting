FROM maven as build
WORKDIR /app
COPY . .
RUN mvn install


FROM openjdk:11.0
WORKDIR /app
COPY --from=build /app/target/spring-boot-mvc-testing-demo-1.0.0.jar /app/
EXPOSE 8080
CMD ["java","-jar","spring-boot-mvc-testing-demo-1.0.0.jar"]
