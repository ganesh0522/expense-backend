# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build project using Maven Wrapper
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

# Run app
CMD ["java", "-jar", "target/*.jar"]