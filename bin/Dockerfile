FROM maven:3-jdk-11
COPY ./ /csar/
RUN cd /csar && mvn clean package
EXPOSE 8080
ENTRYPOINT ["java","-jar","/csar/target/csar-0.0.1-SNAPSHOT.jar"]
