FROM maven:3-jdk-11
COPY ./ /csar/
RUN cd /csar/src/main/resources && rm application-local.properties && mv application-srv.properties application.properties
RUN cd /csar && mvn -DskipTests=true clean package
EXPOSE 8080
ENTRYPOINT ["java","-jar","/csar/target/csar-0.0.1-SNAPSHOT.jar"]
