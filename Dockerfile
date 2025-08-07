# syntax=docker/dockerfile:1

# ====== Phase 1: Build the application using Gradle ======
FROM gradle:8.14.3-jdk24-alpine AS build

# Set the working directory for Gradle
WORKDIR /home/gradle/src

# Copy the Gradle wrapper and configuration files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Install dependencies
RUN gradle dependencies --no-daemon

# Copy the rest of the application source code
COPY . .

# Build the application JAR
RUN gradle bootJar --no-daemon

# ====== Phase 2: Create the final image with the built JAR ======
FROM eclipse-temurin:24-jre-alpine as final

# Set the working directory for the final image
WORKDIR /backend

# Copy the built JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# ====== Development Dockerfile ======
# Use the official Gradle image with JDK 24
FROM gradle:jdk24 as development

# Set the working directory in the container
WORKDIR /backend

# Copy the Gradle wrapper and build files
COPY build.gradle settings.gradle gradlew run-dev.sh ./
COPY gradle ./gradle

# Grant execute permissions to the Gradle wrapper and run script
RUN chmod +x gradlew run-dev.sh

# Run the development script
CMD ["./run-dev.sh"]