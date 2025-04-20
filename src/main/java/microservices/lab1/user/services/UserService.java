package microservices.lab1.user.services;

import lombok.RequiredArgsConstructor;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.user.dto.CreateUserDTO;
import microservices.lab1.user.dto.UpdateUserDTO;
import microservices.lab1.user.dto.GetUserDTO;
import microservices.lab1.user.mapper.UserMapper;
import microservices.lab1.user.models.User;
import microservices.lab1.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetUserDTO save(CreateUserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toGetUserDTO(savedUser);
    }

    public GetUserDTO findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("Користувача з ID " + id + " не знайдено."));
        return userMapper.toGetUserDTO(user);
    }

    public List<GetUserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    public GetUserDTO update(UpdateUserDTO userDTO, long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException("Користувача з ID " + id + " не знайдено.");
        }
        User existingUser = userRepository.findById(id).get();
        User updatedUser = userMapper.toEntity(userDTO, existingUser);
        updatedUser = userRepository.save(updatedUser);
        return userMapper.toGetUserDTO(updatedUser);
    }

    public boolean delete(long id, String username) {
        if (!userRepository.existsById(id)) {
            throw new CustomException("Користувача з ID " + id + " не знайдено.");
        }
        User user = userRepository.findById(id).get();
        if (username != null && !username.isBlank() && !user.getUsername().equals(username)) {
            throw new CustomException("Ім'я користувача не співпадає.");
        }
        userRepository.deleteById(id);
        return true;
    }
}