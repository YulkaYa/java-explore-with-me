package ru.practicum;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    @Autowired
    private final StatsRepository statsRepository;

    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        statsRepository.save(endpointHit);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris != null) {
            if (uris.isEmpty()) {
                uris = null;
            } else {
                uris = uris.stream()
                        .map(uri -> URLDecoder.decode(String.valueOf(uri), StandardCharsets.UTF_8))
                        .map(uri -> uri.replace("[", ""))
                        .map(uri -> uri.replace("]", ""))
                        .toList();
            }
        }

        if (start == null || end == null) {
            throw new ValidationException("Start and end should not be empty");
        } else if (start.isAfter(end)) {
            throw new ValidationException("Start should be before end");
        }

        if (unique) {
            return statsRepository.getUniqueStats(start, end, uris);
        } else {
            return statsRepository.getStats(start, end, uris);
        }
    }
}
