package ru.practicum.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    @Pattern(regexp = ".*\\S+.*", message = "Имя не может состоять из пробелов или быть пустым")
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}