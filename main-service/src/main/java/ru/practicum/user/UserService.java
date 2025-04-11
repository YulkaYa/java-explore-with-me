package ru.practicum.user;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest user);

    void deleteUser(Long id);

    List<UserDto> getUsers(Collection<Long> ids, int from, int size);
}
