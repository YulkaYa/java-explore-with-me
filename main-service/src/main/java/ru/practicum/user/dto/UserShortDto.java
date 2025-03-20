package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserShortDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    @Pattern(regexp = ".*\\S+.*", message = "Имя не может состоять из пробелов или быть пустым")
    private String name;
}