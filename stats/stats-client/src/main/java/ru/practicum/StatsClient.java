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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsClient {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
/* todo   public StatsClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }*/

    @Value("${stats-service.url}")
    private String statsServiceUrl;

    public void saveHit(EndpointHitDto endpointHitDto) {
        restTemplate().postForObject(statsServiceUrl + "/hit", endpointHitDto, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        RestTemplate restTemplate = new RestTemplate();

        // Базовый URL (без параметров)
        String baseUrl = statsServiceUrl + "/stats";

        Map<String, String > requestParams = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris.toString(),
                "unique", String.valueOf(unique)
        );

        // Собираем URL с параметрами
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        String fullUrl = builder.toUriString();

        System.out.println("Request URL: " + fullUrl + " vdf");

        // Делаем GET-запрос (ответ будет в виде массива Post[])
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );

        return Arrays.asList(response.getBody());
    }
}



