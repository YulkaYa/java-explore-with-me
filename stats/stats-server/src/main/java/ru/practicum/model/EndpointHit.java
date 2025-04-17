package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@SuperBuilder(toBuilder = true)
@Data
@Entity
@RequiredArgsConstructor
@Table(name = "endpoint_hits")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app;
    private String uri;
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}