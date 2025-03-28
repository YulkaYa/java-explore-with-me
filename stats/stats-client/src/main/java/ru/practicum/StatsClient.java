package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsClient {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${stats-service.url}")
    private String statsServiceUrl;

    public void saveHit(EndpointHitDto endpointHitDto) {
        restTemplate.postForObject(statsServiceUrl + "/hit", endpointHitDto, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        RestTemplate restTemplate = new RestTemplate();

        // Базовый URL (без параметров)
        String baseUrl = statsServiceUrl + "/stats";

        Map<String, Object > requestParams = Map.of(
                "start", start,
                "end", end ,
                "uris", uris.toString(),
                "unique", String.valueOf(unique)
        );

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String fullUrl = builder.toUriString();
        //todo System.out.println("Request URL: " + fullUrl + " vdf");

        // Делаем GET-запрос (ответ будет в виде массива Post[])
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );

        return Arrays.asList(response.getBody());
    }
}



