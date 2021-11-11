package org.override.models;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;


/**
 *  <p>route: tương tự như URL -> http://localhost/get-product.
 *  <p>body: dữ liệu cần truyền tải, vd: class ExampleModel.
 *  <p>headers: luôn là map <String, String>, nếu cần sài kiểu số thì parse lại sau.
 *  <p>status: mã của thông điệp, dự vào mã mà rẽ nhánh xử lý. Tương tự như http status code.
 */
@AllArgsConstructor
public class HyperEntity<T> implements Serializable {
    public String route;
    public T body;
    public Map<String, String> headers;
    public Integer status;

    public HyperEntity(String route, Integer status) {
        this.status = status;
    }

    public HyperEntity(String route, T body, Integer status) {
        this(route, body, null, status);
    }

    public HyperEntity(String route, Map<String, String> headers, Integer status) {
        this(route, null, headers, status);
    }


    public static <T> HyperEntity<T> ok(T body) {
        return ok().body(body);
    }

    public static <T> HyperEntity<T> notFound(T body) {
        return notFound().body(body);
    }

    public static <T> HyperEntity<T> created(T body) {
        return created().body(body);
    }

    public static <T> HyperEntity<T> badRequest(T body) {
        return badRequest().body(body);
    }

    public static <T> HyperEntity<T> unauthorized(T body) {
        return unauthorized().body(body);
    }

    public static <T> HyperEntity<T> unprocessableEntity(T body) {
        return unprocessableEntity().body(body);
    }

    public static <T> HyperEntity<T> of(Optional<T> body) {
        assert body.isPresent() : "Body must not be null";
        return body.map(HyperEntity::ok).orElseGet(() ->
                notFound().build()
        );
    }

    public static HyperEntity.DataEntityBuilder ok() {
        return status(HyperStatus.OK);
    }

    public static HyperEntity.DataEntityBuilder created() {
        return status(HyperStatus.CREATED);
    }

    public static HyperEntity.DataEntityBuilder notFound() {
        return status(HyperStatus.NOT_FOUND);
    }

    public static HyperEntity.DataEntityBuilder badRequest() {
        return status(HyperStatus.BAD_REQUEST);
    }

    public static HyperEntity.DataEntityBuilder unauthorized() {
        return status(HyperStatus.UNAUTHORIZED);
    }

    public static HyperEntity.DataEntityBuilder unprocessableEntity() {
        return status(HyperStatus.UNPROCESSABLE_ENTITY);
    }

    public static HyperEntity.DataEntityBuilder status(Integer status) {
        assert status != null : "HttpStatus must not be null";
        return new HyperEntity.DataEntityBuilder(status);
    }

    public static class DataEntityBuilder {
        Integer status;
        Map<String, String> headers;

        public DataEntityBuilder(Integer status) {
            this.status = status;
        }

        public <T> HyperEntity<T> build() {
            return this.body(null);
        }

        public <T> HyperEntity<T> body(T body) {
            return new HyperEntity<T>(null, body, this.headers, this.status);
        }
    }
}
