package org.override.core.models;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Map;


/**
 * <p>route: tương tự như URL -> http://localhost/get-product.
 * <p>body: dữ liệu cần truyền tải, vd: class ExampleModel.
 * <p>headers: luôn là map <String, String>, nếu cần sài kiểu số thì parse lại sau.
 * <p>status: mã của thông điệp, dự vào mã mà rẽ nhánh xử lý. Tương tự như http status code.
 */
@AllArgsConstructor
public class HyperEntity implements Serializable {
    public String route;
    public String body;
    public Map<String, String> headers;
    public Integer status;

    public HyperEntity(String route, Integer status) {
        this(route, null, null, status);
    }

    public HyperEntity(String route, String body, Integer status) {
        this(route, body, null, status);
    }

    public HyperEntity(String route, Map<String, String> headers, Integer status) {
        this(route, null, headers, status);
    }

    public static HyperEntity request(String route, Map<String, String> headers, HyperBody body) {
        return new HyperEntity(route, body != null ? body.toJson() : null, headers, null);
    }

    public static HyperEntity request(String route, Map<String, String> headers) {
        return new HyperEntity(route, null, headers, null);
    }

    public static HyperEntity ok(HyperBody body) {
        return ok().body(body);
    }

    public static HyperEntity notFound(HyperBody body) {
        return notFound().body(body);
    }

    public static HyperEntity created(HyperBody body) {
        return created().body(body);
    }

    public static HyperEntity badRequest(HyperBody body) {
        return badRequest().body(body);
    }

    public static HyperEntity unauthorized(HyperBody body) {
        return unauthorized().body(body);
    }

    public static HyperEntity unprocessableEntity(HyperBody body) {
        return unprocessableEntity().body(body);
    }

//    public static HyperEntity of(Optional<T> body) {
//        assert body.isPresent() : "Body must not be null";
//        return body.map(HyperEntity::ok).orElseGet(() ->
//                notFound().build()
//        );
//    }

    public static DataEntityBuilder ok() {
        return status(HyperStatus.OK);
    }

    public static DataEntityBuilder created() {
        return status(HyperStatus.CREATED);
    }

    public static DataEntityBuilder notFound() {
        return status(HyperStatus.NOT_FOUND);
    }

    public static DataEntityBuilder badRequest() {
        return status(HyperStatus.BAD_REQUEST);
    }

    public static DataEntityBuilder unauthorized() {
        return status(HyperStatus.UNAUTHORIZED);
    }

    public static DataEntityBuilder unprocessableEntity() {
        return status(HyperStatus.UNPROCESSABLE_ENTITY);
    }

    public static DataEntityBuilder status(Integer status) {
        assert status != null : "HttpStatus must not be null";
        return new DataEntityBuilder(status);
    }

    public static class DataEntityBuilder {
        Integer status;
        Map<String, String> headers;

        public DataEntityBuilder(Integer status) {
            this.status = status;
        }

        public HyperEntity build() {
            return this.body(null);
        }

        public HyperEntity body(HyperBody body) {
            String bodyString = body != null ? body.toJson() : null;
            return new HyperEntity(null, bodyString, this.headers, this.status);
        }
    }
}
