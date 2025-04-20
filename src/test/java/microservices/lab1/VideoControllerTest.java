package microservices.lab1;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.lab1.common.exception.CustomException;
import microservices.lab1.common.exception.GlobalExceptionHandler;
import microservices.lab1.video.controllers.VideoController;
import microservices.lab1.video.dto.CreateVideoDTO;
import microservices.lab1.video.dto.UpdateVideoDTO;
import microservices.lab1.video.dto.DeleteVideoDTO;
import microservices.lab1.video.dto.GetVideoDTO;
import microservices.lab1.video.services.VideoService;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    private ObjectMapper objectMapper;

    private CreateVideoDTO createVideoDTO;
    private UpdateVideoDTO updateVideoDTO;
    private DeleteVideoDTO deleteVideoDTO;
    private GetVideoDTO getVideoDTO;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(videoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        createVideoDTO = new CreateVideoDTO();
        createVideoDTO.setTitle("Test Video");
        createVideoDTO.setDirector("Test Director");
        createVideoDTO.setGenre("Action");
        createVideoDTO.setUserId(1L);

        updateVideoDTO = new UpdateVideoDTO();
        updateVideoDTO.setTitle("Updated Video");
        updateVideoDTO.setDirector("Updated Director");
        updateVideoDTO.setGenre("Drama");
        updateVideoDTO.setUserId(1L);

        deleteVideoDTO = new DeleteVideoDTO();
        deleteVideoDTO.setId(1L);
        deleteVideoDTO.setUserId(1L);

        getVideoDTO = new GetVideoDTO();
        getVideoDTO.setId(1L);
        getVideoDTO.setTitle("Test Video");
        getVideoDTO.setDirector("Test Director");
        getVideoDTO.setGenre("Action");
        getVideoDTO.setPath("/media/test.mp4");
        getVideoDTO.setUserId(1L);

        file = new MockMultipartFile("file", "test.mp4", "video/mp4", "fake video content".getBytes());
    }

    @Test
    void createVideo_Success() throws Exception {
        when(videoService.save(any(CreateVideoDTO.class), any(MockMultipartFile.class))).thenReturn(getVideoDTO);

        MockMultipartFile videoPart = new MockMultipartFile("video", "", "application/json",
                objectMapper.writeValueAsString(createVideoDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos")
                        .file(file)
                        .file(videoPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.director").value("Test Director"))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(videoService).save(any(CreateVideoDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void createVideo_UserNotFound() throws Exception {
        when(videoService.save(any(CreateVideoDTO.class), any(MockMultipartFile.class)))
                .thenThrow(new CustomException("Користувача з ID 999 не знайдено."));

        createVideoDTO.setUserId(999L);

        MockMultipartFile videoPart = new MockMultipartFile("video", "", "application/json",
                objectMapper.writeValueAsString(createVideoDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos")
                        .file(file)
                        .file(videoPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Користувача з ID 999 не знайдено."));

        verify(videoService).save(any(CreateVideoDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void getVideoById_Success() throws Exception {
        when(videoService.findById(1L)).thenReturn(getVideoDTO);

        mockMvc.perform(get("/api/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.director").value("Test Director"))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(videoService).findById(1L);
    }

    @Test
    void getVideoById_NotFound() throws Exception {
        when(videoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/videos/999"))
                .andExpect(status().isNotFound());

        verify(videoService).findById(999L);
    }

    @Test
    void getAllVideos_Success() throws Exception {
        when(videoService.findAll()).thenReturn(List.of(getVideoDTO));

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Video"))
                .andExpect(jsonPath("$[0].director").value("Test Director"))
                .andExpect(jsonPath("$[0].genre").value("Action"))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(videoService).findAll();
    }

    @Test
    void updateVideo_Success() throws Exception {
        when(videoService.update(eq(1L), any(UpdateVideoDTO.class), any())).thenReturn(getVideoDTO);

        MockMultipartFile videoPart = new MockMultipartFile("video", "", "application/json",
                objectMapper.writeValueAsString(updateVideoDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos/1")
                        .file(file)
                        .file(videoPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.director").value("Test Director"))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(videoService).update(eq(1L), any(UpdateVideoDTO.class), any());
    }

    @Test
    void updateVideo_WithoutFile() throws Exception {
        when(videoService.update(eq(1L), any(UpdateVideoDTO.class), isNull())).thenReturn(getVideoDTO);

        MockMultipartFile videoPart = new MockMultipartFile("video", "", "application/json",
                objectMapper.writeValueAsString(updateVideoDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos/1")
                        .file(videoPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.director").value("Test Director"))
                .andExpect(jsonPath("$.genre").value("Action"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(videoService).update(eq(1L), any(UpdateVideoDTO.class), isNull());
    }

    @Test
    void updateVideo_NotFound() throws Exception {
        when(videoService.update(eq(999L), any(UpdateVideoDTO.class), any())).thenReturn(null);

        MockMultipartFile videoPart = new MockMultipartFile("video", "", "application/json",
                objectMapper.writeValueAsString(updateVideoDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos/999")
                        .file(file)
                        .file(videoPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());

        verify(videoService).update(eq(999L), any(UpdateVideoDTO.class), any());
    }

    @Test
    void deleteVideo_Success() throws Exception {
        when(videoService.delete(1L, 1L)).thenReturn(true);

        deleteVideoDTO.setId(1L);
        deleteVideoDTO.setUserId(1L);

        mockMvc.perform(delete("/api/videos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteVideoDTO)))
                .andExpect(status().isNoContent());

        verify(videoService).delete(1L, 1L);
    }

    @Test
    void deleteVideo_NotFound() throws Exception {
        when(videoService.delete(999L, 1L)).thenReturn(false);

        deleteVideoDTO.setId(999L);
        deleteVideoDTO.setUserId(1L);

        mockMvc.perform(delete("/api/videos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteVideoDTO)))
                .andExpect(status().isNotFound());

        verify(videoService).delete(999L, 1L);
    }

    @Test
    void deleteVideo_AccessDenied() throws Exception {
        when(videoService.delete(1L, 999L))
                .thenThrow(new CustomException("Відео не належить користувачу з ID 999"));

        deleteVideoDTO.setId(1L);
        deleteVideoDTO.setUserId(999L);

        mockMvc.perform(delete("/api/videos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Відео не належить користувачу з ID 999"))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(videoService).delete(1L, 999L);
    }
}