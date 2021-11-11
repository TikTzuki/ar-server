package org.override.models;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
public class DataEntity<T> implements Serializable {
    public T body;
    public Map<String, Object> headers;
    public Status status;

    public DataEntity(Status status) {
        this.status = status;
    }

    public DataEntity(T body, Status status) {
        this(body, null, status);
    }

    public DataEntity(Map<String, Object> headers, Status status) {
        this(null, headers, status);
    }


    public static DataEntityBuilder ok() {
        return status(Status.OK);
    }

    public static <T> DataEntity<T> ok(T body) {
        return ok().body(body);
    }

    public static <T> DataEntity<T> of(Optional<T> body) {
        assert body.isPresent() : "Body must not be null";
        return body.map(DataEntity::ok).orElseGet(() ->
                notFound().build()
        );
    }

    public static DataEntityBuilder notFound() {
        return status(Status.NOT_FOUND);
    }

    public static DataEntityBuilder status(Status status) {
        assert status != null : "HttpStatus must not be null";
        return new DataEntityBuilder(status);
    }

    public static class DataEntityBuilder {
        Status status;
        Map<String, Object> headers;

        public DataEntityBuilder(Status status) {
            this.status = status;
        }

        public <T> DataEntity<T> build() {
            return this.body(null);
        }

        public <T> DataEntity<T> body(T body) {
            return new DataEntity<T>(body, this.headers, this.status);
        }
    }
}
