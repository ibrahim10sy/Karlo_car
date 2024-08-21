# #Generate build
# FROM 3.9.8-eclipse-temurin-11 as build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -Dmvan.test.skip=true

# #Dockerisation
# # The base image on which we would build our image
# FROM 3.9.8-eclipse-temurin-11


# # Expose port 9000
# EXPOSE 9000

# # Set the working directory
# WORKDIR /app

# # Copy the source code to the working directory
# COPY --from=build app/target/Karlo-0.0.1-SNAPSHOT.jar ./karlo.jar


# # Run the application
# ENTRYPOINT ["java", "-jar", "karlo.jar"]