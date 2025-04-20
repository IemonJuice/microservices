package microservices.lab1;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.common.exception.GlobalExceptionHandler;
import microservices.lab1.user.controllers.UserController;
import microservices.lab1.user.dto.CreateUserDTO;
import microservices.lab1.user.dto.UpdateUserDTO;
import microservices.lab1.user.dto.DeleteUserDTO;
import microservices.lab1.user.dto.GetUserDTO;
import microservices.lab1.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    private DeleteUserDTO deleteUserDTO;
    private GetUserDTO getUserDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setPassword("password123");
        createUserDTO.setMusicIds(List.of(1L, 2L));
        createUserDTO.setVideoIds(List.of(3L, 4L));

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setPassword("newpassword123");
        updateUserDTO.setMusicIds(List.of(5L, 6L));
        updateUserDTO.setVideoIds(List.of(7L, 8L));

        deleteUserDTO = new DeleteUserDTO();
        deleteUserDTO.setUsername("testuser");

        getUserDTO = new GetUserDTO();
        getUserDTO.setId(1L);
        getUserDTO.setUsername("testuser");
        getUserDTO.setMusicIds(List.of(1L, 2L));
        getUserDTO.setVideoIds(List.of(3L, 4L));
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.save(any(CreateUserDTO.class))).thenReturn(getUserDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.musicIds").isArray())
                .andExpect(jsonPath("$.musicIds[0]").value(1))
                .andExpect(jsonPath("$.videoIds").isArray())
                .andExpect(jsonPath("$.videoIds[0]").value(3));

        verify(userService).save(any(CreateUserDTO.class));
    }

    @Test
    void createUser_ValidationError() throws Exception {
        CreateUserDTO invalidDTO = new CreateUserDTO();
        invalidDTO.setUsername("");
        invalidDTO.setPassword("short");
        invalidDTO.setMusicIds(List.of());
        invalidDTO.setVideoIds(List.of());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(containsInAnyOrder(
                        "username: Username is required",
                        "password: Password must be between 6 and 100 characters",
                        "musicIds: Music IDs cannot be empty if provided",
                        "videoIds: Video IDs cannot be empty if provided"
                )));

        verify(userService, never()).save(any(CreateUserDTO.class));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(getUserDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.musicIds[0]").value(1))
                .andExpect(jsonPath("$.videoIds[0]").value(3));

        verify(userService).findById(1L);
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).findById(999L);
    }

    @Test
    void getAllUsers_Success() throws Exception {
        when(userService.findAll()).thenReturn(List.of(getUserDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].musicIds[0]").value(1))
                .andExpect(jsonPath("$[0].videoIds[0]").value(3));

        verify(userService).findAll();
    }

    @Test
    void updateUser_Success() throws Exception {
        when(userService.update(any(UpdateUserDTO.class), eq(1L))).thenReturn(getUserDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.musicIds[0]").value(1))
                .andExpect(jsonPath("$.videoIds[0]").value(3));

        verify(userService).update(any(UpdateUserDTO.class), eq(1L));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        when(userService.update(any(UpdateUserDTO.class), eq(999L))).thenReturn(null);

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isNotFound());

        verify(userService).update(any(UpdateUserDTO.class), eq(999L));
    }

    @Test
    void updateUser_ValidationError() throws Exception {
        UpdateUserDTO invalidDTO = new UpdateUserDTO();
        invalidDTO.setUsername("");
        invalidDTO.setPassword("short");
        invalidDTO.setMusicIds(List.of());
        invalidDTO.setVideoIds(List.of());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(containsInAnyOrder(
                        "username: Username is required",
                        "password: Password must be between 6 and 100 characters",
                        "musicIds: Music IDs cannot be empty if provided",
                        "videoIds: Video IDs cannot be empty if provided"
                )));

        verify(userService, never()).update(any(UpdateUserDTO.class), anyLong());
    }

    @Test
    void deleteUser_Success() throws Exception {
        when(userService.delete(1L, "testuser")).thenReturn(true);

        deleteUserDTO.setUsername("testuser");

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteUserDTO)))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L, "testuser");
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        when(userService.delete(999L, "testuser")).thenReturn(false);

        deleteUserDTO.setUsername("testuser");

        mockMvc.perform(delete("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteUserDTO)))
                .andExpect(status().isNotFound());

        verify(userService).delete(999L, "testuser");
    }

    @Test
    void deleteUser_AccessDenied() throws Exception {
        when(userService.delete(1L, "wronguser"))
                .thenThrow(new CustomException("Користувач з username wronguser не може видалити цього користувача"));

        deleteUserDTO.setUsername("wronguser");

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Користувач з username wronguser не може видалити цього користувача"))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(userService).delete(1L, "wronguser");
    }
}