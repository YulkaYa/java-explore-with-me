package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.UserService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public UserDto addUser(@RequestBody @Valid NewUserRequest user) {
        return userService.addUser(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) Collection<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }
}

