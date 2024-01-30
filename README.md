# Biblia
Website đánh giá và giới thiệu sách - Sản phẩm của môn nghiên cứu tốt nghiệp

### 1. Sơ lược thiết kế hệ thống:
#### a) Biểu đồ use case tổng quan:

![biểu đồ use case tổng quan](./images/General%20UseCase.png)

#### b) Biểu đồ quan hệ thực thể:

![biểu đồ quan hệ thực thể](./images/ER%20Diagram.png)

### 2. Cài đặt

### a) Không sử dụng Docker:
* #### Yêu cầu:
  * JDK 11+
  * Apache Maven 3.0+
  * node 12+

* #### Back-end
  * Chạy project bằng IDE hoặc chạy lệnh sau tại thư mục [biblia-backend](./biblia-backend):
    - `mvn spring-boot:run`
  * Sau khi chạy ứng dụng có thể xem API documentation tại đây: 
    - Swagger UI: http://localhost:8080/swagger-ui-custom.html
    - JSON: http://localhost:8080/api-docs
    - YAML: http://localhost:8080/api-docs.yaml
* #### Front-end
  * Tạo một storage project trên [Firebase](https://console.firebase.google.com/) và tích hợp vào project bằng cách dán 
  nội dung file config từ mục ***Project Settings*** vào file [firebase.js](./biblia-frontend/src/firebase.js)
  * Tại thư mục [biblia-frontend](./biblia-frontend) lần lượt chạy các lệnh sau:
    - `npm install`
    - `npm start`
    - Mở [http://localhost:3030](http://localhost:3030) để xem trang web trên trình duyệt.

### b) Sử dụng Docker:
- Khởi chạy Docker Engine
- Tại thư mục project chạy lệnh `docker compose up` 
