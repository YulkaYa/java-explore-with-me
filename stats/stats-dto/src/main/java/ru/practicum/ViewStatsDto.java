package ru.practicum;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ViewStatsDto {
    //    example: ewm-main-service
    //    Название сервиса
    @NonNull
    private String app;
    //    example: /events/1
    //    URI сервиса
    @NonNull
    private String uri;
    //    example: 6
    //    Количество просмотров
    @NonNull
    private Long hits;
}