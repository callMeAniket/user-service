# Use the official sbt image which includes OpenJDK 11
FROM hseeberger/scala-sbt:11.0.13_1.6.1_2.13.7 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the build.sbt and other necessary files to the working directory

COPY . /app

# Copy the source code and any necessary configuration files

EXPOSE 6001

# Compile the application
RUN sbt clean compile

# Define the entry point for the container to run the sbt command
CMD ["sbt", "run"]