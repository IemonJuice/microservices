package microservices.lab1;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.common.exception.GlobalExceptionHandler;
import microservices.lab1.music.controllers.MusicController;
import microservices.lab1.music.dto.CreateMusicDTO;
import microservices.lab1.music.dto.UpdateMusicDTO;
import microservices.lab1.music.dto.GetMusicDTO;
import microservices.lab1.music.dto.DeleteMusicDTO;
import microservices.lab1.music.services.MusicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MusicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MusicService musicService;

    @InjectMocks
    private MusicController musicController;

    private ObjectMapper objectMapper;

    private CreateMusicDTO createMusicDTO;
    private UpdateMusicDTO updateMusicDTO;
    private GetMusicDTO getMusicDTO;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(musicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createMusicDTO = new CreateMusicDTO();
        createMusicDTO.setTitle("Test Song");
        createMusicDTO.setArtist("Test Artist");
        createMusicDTO.setGenre("Pop");
        createMusicDTO.setUserId(1L);

        updateMusicDTO = new UpdateMusicDTO();
        updateMusicDTO.setTitle("Updated Song");
        updateMusicDTO.setArtist("Updated Artist");
        updateMusicDTO.setGenre("Rock");
        updateMusicDTO.setUserId(1L);

        getMusicDTO = new GetMusicDTO();
        getMusicDTO.setId(1L);
        getMusicDTO.setTitle("Test Song");
        getMusicDTO.setArtist("Test Artist");
        getMusicDTO.setGenre("Pop");
        getMusicDTO.setPath("/media/test.mp3");

        file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "fake mp3 content".getBytes());
    }

    @Test
    void createMusic_Success() throws Exception {
        when(musicService.save(any(CreateMusicDTO.class), any(MockMultipartFile.class))).thenReturn(getMusicDTO);

        MockMultipartFile musicPart = new MockMultipartFile("music", "", "application/json",
                objectMapper.writeValueAsString(createMusicDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/music")
                        .file(file)
                        .file(musicPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Song"))
                .andExpect(jsonPath("$.artist").value("Test Artist"));

        verify(musicService).save(any(CreateMusicDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void createMusic_ValidationError() throws Exception {
        CreateMusicDTO invalidDTO = new CreateMusicDTO();
        invalidDTO.setTitle("");
        invalidDTO.setArtist("Test Artist");
        invalidDTO.setGenre("Pop");
        invalidDTO.setUserId(1L);

        MockMultipartFile musicPart = new MockMultipartFile("music", "", "application/json",
                objectMapper.writeValueAsString(invalidDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/music")
                        .file(file)
                        .file(musicPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value("title: Title must be between 1 and 100 characters"));

        verify(musicService, never()).save(any(CreateMusicDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void createMusic_UserNotFound() throws Exception {
        when(musicService.save(any(CreateMusicDTO.class), any(MockMultipartFile.class)))
                .thenThrow(new CustomException("Користувача з ID 999 не знайдено."));

        createMusicDTO.setUserId(999L);

        MockMultipartFile musicPart = new MockMultipartFile("music", "", "application/json",
                objectMapper.writeValueAsString(createMusicDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/music")
                        .file(file)
                        .file(musicPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Користувача з ID 999 не знайдено."));

        verify(musicService).save(any(CreateMusicDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void getMusicById_Success() throws Exception {
        when(musicService.findById(1L)).thenReturn(getMusicDTO);

        mockMvc.perform(get("/api/music/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Song"));

        verify(musicService).findById(1L);
    }

    @Test
    void getMusicById_NotFound() throws Exception {
        when(musicService.findById(999L)).thenThrow(new CustomException("Музику з ID 999 не знайдено."));

        mockMvc.perform(get("/api/music/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Музику з ID 999 не знайдено."));

        verify(musicService).findById(999L);
    }

    @Test
    void getAllMusic_Success() throws Exception {
        when(musicService.findAll()).thenReturn(List.of(getMusicDTO));

        mockMvc.perform(get("/api/music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Song"));

        verify(musicService).findAll();
    }

    @Test
    void updateMusic_Success() throws Exception {
        when(musicService.update(eq(1L), any(UpdateMusicDTO.class), any())).thenReturn(getMusicDTO);

        MockMultipartFile musicPart = new MockMultipartFile("music", "", "application/json",
                objectMapper.writeValueAsString(updateMusicDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/music/1")
                        .file(file)
                        .file(musicPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Song"));

        verify(musicService).update(eq(1L), any(UpdateMusicDTO.class), any());
    }

    @Test
    void updateMusic_ValidationError() throws Exception {
        UpdateMusicDTO invalidDTO = new UpdateMusicDTO();
        invalidDTO.setTitle("");
        invalidDTO.setArtist("Updated Artist");
        invalidDTO.setGenre("Rock");
        invalidDTO.setUserId(1L);

        MockMultipartFile musicPart = new MockMultipartFile("music", "", "application/json",
                objectMapper.writeValueAsString(invalidDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/music/1")
                        .file(file)
                        .file(musicPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("title: Title must be between 1 and 100 characters"));

        verify(musicService, never()).update(anyLong(), any(UpdateMusicDTO.class), any());
    }

    @Test
    void deleteMusic_Success() throws Exception {
        when(musicService.delete(1L, 1L)).thenReturn(true);

        DeleteMusicDTO deleteDTO = new DeleteMusicDTO();
        deleteDTO.setUserId(1L);

        mockMvc.perform(delete("/api/music/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDTO)))
                .andExpect(status().isNoContent());

        verify(musicService).delete(1L, 1L);
    }

    @Test
    void deleteMusic_AccessDenied() throws Exception {
        when(musicService.delete(1L, 999L))
                .thenThrow(new CustomException("Музика не належить користувачу з ID 999"));

        DeleteMusicDTO deleteDTO = new DeleteMusicDTO();
        deleteDTO.setUserId(999L);

        mockMvc.perform(delete("/api/music/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Музика не належить користувачу з ID 999"))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(musicService).delete(1L, 999L);
    }
}