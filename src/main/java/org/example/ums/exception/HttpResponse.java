package org.example.ums.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class HttpResponse <T>{
    private HttpStatus status;
    private T data;


    public static <T> HttpResponse<T> of(HttpStatus status, @Nullable T data) {
        return new HttpResponse<T>(status, data);
    }

    public ResponseEntity<T> responseEntity() {
        return ResponseEntity.status(this.status).body(this.data);
    }

    public static <T> HttpResponse<T> ok(@Nullable T data) {
        return new HttpResponse<>(HttpStatus.valueOf(200), data);
    }
}
