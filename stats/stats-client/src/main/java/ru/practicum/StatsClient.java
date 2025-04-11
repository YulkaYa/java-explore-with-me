package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        // Базовый URL (без параметров)
        String baseUrl = statsServiceUrl + "/stats";

        Map<String, Object> requestParams = Map.of(
                "start", start,
                "end", end,
                "uris", uris.toString(),
                "unique", String.valueOf(unique)
        );

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );
        return Arrays.asList(response.getBody());
    }
}



