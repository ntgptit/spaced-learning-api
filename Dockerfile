# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw
RUN ./mvnw dependency:resolve dependency:resolve-plugins -B
COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="SpacedLearning Team" \
  version="1.0" \
  description="Spaced Learning API Docker Image"
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs && chown -R spring:spring /app
USER spring:spring
ENV SPRING_PROFILES_ACTIVE=prod \
  JAVA_OPTS=""
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:8080/ || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+UseContainerSupport -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
