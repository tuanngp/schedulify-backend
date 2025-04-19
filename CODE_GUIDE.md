# Schedulify Backend

## Tổng quan
Schedulify là một hệ thống quản lý lịch trình với kiến trúc backend được xây dựng trên Spring Boot. Project tuân thủ các design pattern và best practices để đảm bảo tính mở rộng và bảo trì.

## Cấu trúc Base Classes

### BaseEntity
Là lớp cơ sở cho tất cả các entity trong hệ thống, cung cấp các tính năng:
- Tự động sinh ID
- Auditing (createdAt, updatedAt, createdBy, updatedBy)
- Soft Delete với trường isDeleted

```java
public class YourEntity extends BaseEntity {
    // Thêm các trường riêng của entity
}
```

### BaseService
Interface định nghĩa các thao tác CRUD chuẩn:
- Tìm kiếm: findAll(), findById()
- Thêm/Sửa: save(), saveAll(), update()
- Xóa mềm: delete(), deleteById()
- Phân trang: findAll(Pageable)

```java
@Service
public class YourService extends BaseServiceImpl<YourEntity, Long> {
    public YourService(YourRepository repository) {
        super(repository);
    }
    
    // Thêm các method riêng của service
}
```

### BaseRepository
Extend JpaRepository và thêm các tính năng tùy chỉnh:
- Tự động lọc các entity đã bị xóa mềm
- Hỗ trợ các query method tùy chỉnh

```java
@Repository
public interface YourRepository extends BaseRepository<YourEntity, Long> {
    // Thêm các query method
}
```

## Logging System
Hệ thống logging được phân tách theo mức độ:

1. **Error Logs** (error.log)
- Các lỗi nghiêm trọng
- Được lưu trữ 30 ngày

2. **Application Logs** (application.log)
- Log mức INFO và WARN
- Thông tin hoạt động của hệ thống
- Được lưu trữ 30 ngày

3. **Debug Logs** (debug.log)
- Chi tiết debug
- Chỉ lưu trữ 7 ngày

Cấu hình logging:
```yaml
# Trong application.yml
logging:
  level:
    com.schedulify.backend: DEBUG
    org.springframework: INFO
```

## Quy ước phát triển

### 1. Entity mới
- Extend BaseEntity
- Thêm các annotation JPA cần thiết
- Implement equals() và hashCode() nếu cần

### 2. Repository mới
- Extend BaseRepository
- Thêm các query method tùy chỉnh
- Sử dụng @Query cho các truy vấn phức tạp

### 3. Service mới
- Extend BaseServiceImpl
- Override các method cần tùy chỉnh
- Thêm logic nghiệp vụ riêng

### 4. Logging
- Sử dụng @Slf4j trên class
- Log theo mức độ phù hợp:
  ```java
  log.error("Lỗi nghiêm trọng");
  log.warn("Cảnh báo");
  log.info("Thông tin hoạt động");
  log.debug("Chi tiết debug");
  ```

## Bảo mật
- Sử dụng Spring Security
- Tích hợp JWT
- Auditing tự động cho mọi thao tác

## Hướng dẫn chạy project

1. Yêu cầu:
- Java 17+
- PostgreSQL
- Gradle

2. Cấu hình:
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/schedulify
    username: your_username
    password: your_password
```

3. Chạy project:
```bash
./gradlew bootRun
```

## API Documentation
Truy cập Swagger UI: `http://localhost:8080/api/swagger-ui.html`