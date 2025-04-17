package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.DuplicatedDataException;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = mapper.newDtoToEntity(newUserRequest);
        validateNewEmailUser(null, newUserRequest.getEmail());
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(Collection<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return userRepository.findByIdIn(ids, page)
                .map(mapper::toDto)
                .getContent();
    }

    private void validateNewEmailUser(Long id, String emailInNewUser) {
        Long userIdWithSameEmail = userRepository.findByEmail(emailInNewUser);

        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(id)) {
            throw new DuplicatedDataException("Integrity constraint has been violated.");
        }
    }
}
