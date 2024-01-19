# Biblia
Website đánh giá và giới thiệu sách - Sản phẩm của môn nghiên cứu tốt nghiệp

### 1. Sơ lược thiết kế hệ thống:
#### a) Biểu đồ use case tổng quan:

![biểu đồ use case tổng quan](./images/General%20UseCase.png)

#### b) Biểu đồ quan hệ thực thể:

![biểu đồ quan hệ thực thể](./images/ER%20Diagram.png)

### 2. Cài đặt

#### a) Back-end
* Chạy project bằng IDE hoặc sử dụng lệnh:
    - `mvn spring-boot:run`

#### b) Front-end
* Tạo một storage project trên [Firebase](https://console.firebase.google.com/) và tích hợp vào project bằng cách dán 
nội dung file config từ mục ***Project Settings*** vào file [firebase.js](./biblia-frontend/src/firebase.js)
* Tại thư mục ***biblia-frontend*** lần lượt chạy các lệnh sau:
    - `npm install`
    - `npm start`
    - Mở [http://localhost:3030](http://localhost:3030) để xem trang web trên trình duyệt.
