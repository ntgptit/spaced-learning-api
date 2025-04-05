# Sử dụng image OpenJDK 17 trên Alpine để giảm kích thước
FROM eclipse-temurin:17-jdk-alpine as build

# Đặt thư mục làm việc trong container
WORKDIR /workspace/app

# Sao chép file pom.xml và src
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Cấp quyền thực thi cho maven wrapper
RUN chmod +x ./mvnw

# Build ứng dụng với Maven 
RUN ./mvnw clean package -DskipTests

# Sử dụng image JRE cho runtime để giảm kích thước
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="SpacedLearning Team"
LABEL version="1.0"
LABEL description="Spaced Learning API Docker Image"

# Tạo thư mục app
WORKDIR /app

# Sao chép JAR file từ build stage
COPY --from=build /workspace/app/target/*.jar /app/app.jar

# Tạo user non-root để tăng bảo mật
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Thiết lập biến môi trường
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port 8080
EXPOSE 8080

# Thiết lập điểm vào 
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]