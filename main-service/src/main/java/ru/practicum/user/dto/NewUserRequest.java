package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    @Pattern(regexp = ".*\\S+.*", message = "Имя не может состоять из пробелов или быть пустым")
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}