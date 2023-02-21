FROM gradle:8.0-jdk17-focal
RUN mkdir /tmp/source
COPY . /tmp/source
WORKDIR /tmp/source
RUN gradle clean
RUN gradle bootJar
ENTRYPOINT ["java" ,"-jar", "build/libs/woven-videoserver-1.0.0.jar"]
EXPOSE 8080
EXPOSE 1996
