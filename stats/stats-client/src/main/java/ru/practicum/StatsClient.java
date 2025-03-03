package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.lang.management.ManagementPermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Value("${stats-service.url}")
    private String statsServiceUrl;

    public void saveHit(EndpointHitDto endpointHitDto) {
        restTemplate.postForObject(statsServiceUrl + "/hit", endpointHitDto, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(statsServiceUrl + "/stats", ViewStatsDto[].class, parameters);
        return Arrays.asList(response.getBody());
    }
}



