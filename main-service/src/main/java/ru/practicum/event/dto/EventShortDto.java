package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class EventShortDto {
    @NotNull
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(regexp = ".*\\S+.*", message = "description не может состоять из пробелов или быть пустым")
    private String description;
    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "annotation не может состоять из пробелов или быть пустым")
    private String annotation;
    @NotNull
    private CategoryDto category;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Positive
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime eventDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserShortDto initiator;
    @NotNull
    private Boolean paid;
    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Title не может состоять из пробелов или быть пустым")
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long views;
}