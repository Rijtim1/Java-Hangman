# syntax=docker/dockerfile:1

# Dockerfile for Java Hangman Game
# This is a simple Java console application that reads from data files

# Use OpenJDK 11 as the base image for building and running
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the source code and data files to the container
COPY src/ ./src/
COPY data/ ./data/

# Compile the Java application
RUN javac -d . src/Final.java

# Create a non-privileged user for security
RUN useradd -r -s /bin/false hangman
RUN chown -R hangman:hangman /app
USER hangman

# Run the application
CMD ["java", "src.Final"]
