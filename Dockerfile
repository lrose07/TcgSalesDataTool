FROM openjdk:11
ADD target/tcg-sales-data-tools.jar tcg-sales-data-tools.jar
ENTRYPOINT ["java", "-jar","tcg-sales-data-tools.jar"]
EXPOSE 8080