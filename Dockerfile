FROM openjdk:8-jdk
COPY . /app
WORKDIR /app
RUN apt-get update \
 && apt-get install -y maven \
&& rm -rf /var/lib/apt/lists/*
RUN mvn clean install
EXPOSE 8080
CMD ["./docker-entrypoint.sh"]

