Chuẩn giao tiếp server client

client request:

```json
{
  "route": "GET_RESULT",
  "headers": {
    "key": "value"
  },
  "body": {
    "key": "value"
  }
}
```


server response:
```json
{
  "route": "GET_RESULT",
  "status": 400,
  "headers": {
    "key": "value"
  },
  "body": {
    "key": "value"
  }
}
```
server response exception:
```json
{
  "status": 400,
  "code": "BAD_REQUEST",
  "loc": "headers -> clientMessage",
  "detail": "clientMessage must be number"
}
```

# ĐỀ TÀI 5: XÂY DỰNG CHƯƠNG TRÌNH TRA CỨU KẾT QUẢ HỌC TẬP SINH VIÊN

## *Yêu cầu về chức năng phía client (phải có GUI):*

- Tra cứu điểm theo MSSV:
    - Client gửi một chuỗi dữ liệu là MSSV đến server. Server phản hồi về:
        - Các thông tin cá nhân tương ứng MSSV: họ tên; phái; ngày sinh; nơi sinh; lớp; ngành
        - Điểm tất cả các học phần tương ứng với MSSV nhận được hoặc gửi thông báo lỗi nếu MSSV đó không tồn tại.
    - Thống kê sơ lược về quá trình học đến thời điểm hiện tại: điểm trung bình tích lũy (hệ 4 và hệ 10), số tín chỉ tích lũy, tổng số môn đã học (các lần học cải thiện cũng chỉ tính là 1 môn).

- Theo dõi tiến độ học tập (chỉ áp dụng đối với SV ngành CNTT):
    - Client gửi chuỗi dữ liệu MSSV đến server. Server phản hồi về:
        - Tỉ lệ % hoàn thành chương trình đào tạo.
        - Danh sách các học phần còn phải hoàn tất.
        - Biểu đồ thể hiện điểm tổng kết các học phần trong quá trình đào tạo. Mục đích của biểu đồ giúp người xem thấy được sự thay đổi trong kết quả học tập của người học.

- Xếp hạng sinh viên theo điểm trung bình chung:
    - Client gửi chuỗi dữ liệu MSSV đến server kèm theo tùy chọn muốn biết xếp hạng theo tiêu chí nào: Khóa, Ngành, Khoa hoặc kết hợp các tiêu chí trên.
    - Server trả về thứ hạng của client kèm danh sách 10 người có thứ hạng cao hơn và thấp hơn client. Đồng thời cho phép client xem điểm chi tiết bất cứ người nào trong danh sách trên.

## *Yêu cầu về chức năng phía server (không cần GUI):*

- Nhận MSSV từ client, kết nối đến thongtindaotao.sgu.edu.vn, gửi và phân tích dữ liệu nhận được để cho ra kết quả như yêu cầu.
- Server ngoài sử dụng ngôn ngữ Java được phép sử dụng thêm các ngôn ngữ/công cụ khác (nếu cần) để trích xuất dữ liệu. Gợi ý: sử dụng API hoặc Jsoup, Okhttp

## *Yêu cầu chung:*

- Mã hóa nội dung tin nhắn giữa client – server. Phải sử dụng key khác nhau cho các client.
- Các client phải chạy trên các máy tính khác nhau.