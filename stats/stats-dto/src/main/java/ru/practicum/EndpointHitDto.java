package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
public class EndpointHitDto {
    //Идентификатор записи
    //readOnly: true
    // example: 1
    //@NotBlank
    private final Long id;
    //Идентификатор сервиса для которого записывается информация
    //example: ewm-main-service
    //@NotBlank
    private String app;
    //URI для которого был осуществлен запрос
    //example: /events/1
    //@NotBlank
    private String uri;
    //IP-адрес пользователя, осуществившего запрос
    //example: 192.163.0.1
    //@NotBlank
    private String ip;
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    //example: 2022-09-06 11:00:23
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

