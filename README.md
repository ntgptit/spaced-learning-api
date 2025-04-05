# Spaced Learning API

Ứng dụng REST API quản lý học tập theo phương pháp lặp lại ngắt quãng (spaced repetition) để tăng hiệu quả ghi nhớ.

## Yêu cầu hệ thống

- Docker và Docker Compose
- Java 17 (chỉ cần khi phát triển cục bộ)
- Maven (chỉ cần khi phát triển cục bộ)

## Hướng dẫn chạy ứng dụng

### 1. Chạy sử dụng Docker Compose (khuyến nghị)

Đây là cách đơn giản nhất để chạy ứng dụng với đầy đủ môi trường (API + PostgreSQL).

1. **Clone repository và điều hướng tới thư mục dự án**

```bash
git clone <repository-url>
cd spaced-learning-api
```

2. **Chạy ứng dụng với Docker Compose**

```bash
docker-compose up -d
```

Lệnh này sẽ:
- Tạo và chạy PostgreSQL database
- Tạo schema và dữ liệu ban đầu từ tệp SQL
- Build và chạy ứng dụng Spring Boot API

3. **Kiểm tra ứng dụng**

API sẽ khả dụng tại: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html

4. **Dừng ứng dụng**

```bash
docker-compose down
```

Để xóa cả dữ liệu PostgreSQL, sử dụng:

```bash
docker-compose down -v
```

### 2. Chạy trong môi trường phát triển (không sử dụng Docker)

Nếu bạn muốn phát triển và chạy ứng dụng trực tiếp trên máy tính:

1. **Cài đặt PostgreSQL**

- Cài đặt PostgreSQL 13+ trên máy
- Tạo database tên "spaced_learning"
- Tạo schema "spaced_learning" trong database
- Chạy tệp SQL init-scripts/01-init-schema.sql

2. **Cấu hình ứng dụng**

Sửa file `src/main/resources/application-dev.properties` nếu cần:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spaced_learning?currentSchema=spaced_learning
spring.datasource.username=postgres
spring.datasource.password=abcd1234
```

3. **Chạy ứng dụng**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Endpoints API

- **Auth:**
  - POST /api/v1/auth/register - Đăng ký người dùng mới
  - POST /api/v1/auth/login - Đăng nhập
  - POST /api/v1/auth/refresh-token - Làm mới token
  - GET /api/v1/auth/validate - Kiểm tra token

- **Users:**
  - GET /api/v1/users/me - Lấy thông tin người dùng hiện tại
  - GET /api/v1/users - Lấy danh sách người dùng (admin)
  - GET /api/v1/users/{id} - Lấy người dùng theo ID

- **Books:**
  - GET /api/v1/books - Lấy danh sách sách
  - GET /api/v1/books/{id} - Lấy chi tiết sách
  - POST /api/v1/books - Tạo sách mới (admin)
  - GET /api/v1/books/filter - Lọc sách
  - GET /api/v1/books/categories - Lấy danh sách thể loại
  - GET /api/v1/books/search - Tìm kiếm sách

- **Modules:**
  - GET /api/v1/modules - Lấy danh sách module
  - GET /api/v1/modules/{id} - Lấy chi tiết module
  - GET /api/v1/modules/book/{bookId} - Lấy danh sách module của sách

- **Progress:**
  - POST /api/v1/progress - Tạo tiến trình học
  - GET /api/v1/progress/{id} - Lấy chi tiết tiến trình
  - GET /api/v1/progress/user/{userId}/due - Lấy tiến trình cần học

- **Repetitions:**
  - POST /api/v1/repetitions - Tạo lịch lặp lại
  - PUT /api/v1/repetitions/{id} - Cập nhật lịch lặp lại
  - GET /api/v1/repetitions/user/{userId}/due - Lấy lịch lặp lại đến hạn

- **Statistics:**
  - GET /api/v1/stats/dashboard - Lấy thống kê tổng quan
  - GET /api/v1/stats/insights - Lấy thông tin phân tích học tập

## Cấu trúc dự án

```
spaced-learning-api/
├── src/                    # Mã nguồn
│   ├── main/
│   │   ├── java/           # Mã nguồn Java
│   │   └── resources/      # Tài nguyên và cấu hình
│   └── test/               # Unit tests
├── init-scripts/           # Script tạo database
├── .mvn/                   # Maven wrapper
├── Dockerfile              # Docker build file
├── docker-compose.yml      # Docker Compose configuration
├── mvnw                    # Maven wrapper script
├── pom.xml                 # Maven dependencies
└── README.md               # Documentation
```

## Quản lý phiên bản

- Spring Boot v3.4.4
- Java 17
- PostgreSQL 15+

## Bảo mật

API sử dụng JWT (JSON Web Token) để xác thực người dùng. Dữ liệu truyền tải qua HTTP được bảo mật với TLS trong môi trường production.

## Tài liệu API

Tài liệu API đầy đủ (Swagger) có thể truy cập tại: http://localhost:8080/swagger-ui/index.html khi ứng dụng đang chạy.
