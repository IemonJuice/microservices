package microservices.lab1.user.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservices.lab1.user.dto.CreateUserDTO;
import microservices.lab1.user.dto.UpdateUserDTO;
import microservices.lab1.user.dto.DeleteUserDTO;
import microservices.lab1.user.dto.GetUserDTO;
import microservices.lab1.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<GetUserDTO> createUser(@RequestBody @Valid CreateUserDTO userDTO) {
        GetUserDTO createdUser = userService.save(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> getUserById(@PathVariable long id) {
        GetUserDTO user = userService.findById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAllUsers() {
        List<GetUserDTO> userList = userService.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> updateUser(@PathVariable long id, @RequestBody @Valid UpdateUserDTO userDTO) {
        GetUserDTO updatedUser = userService.update(userDTO, id);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id, @RequestBody DeleteUserDTO deleteDTO) {
        if (userService.delete(id, deleteDTO.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}