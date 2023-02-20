FROM openjdk:21-jdk

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Make port 8080 available to the world outside this container
EXPOSE 8080

ENTRYPOINT [ "./mvnw" ]
CMD [ "test" ]