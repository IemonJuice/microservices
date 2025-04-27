package microservices.lab1.service;

import microservices.lab1.common.exception.CustomException;
import microservices.lab1.music.dto.CreateMusicDTO;
import microservices.lab1.music.dto.UpdateMusicDTO;
import microservices.lab1.music.dto.GetMusicDTO;
import microservices.lab1.music.mapper.MusicMapper;
import microservices.lab1.music.models.Music;
import microservices.lab1.music.repository.MusicRepository;
import microservices.lab1.music.services.MusicService;
import microservices.lab1.user.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MusicServiceTest {

    @Autowired
    private MusicService underTest;

    @MockitoBean
    private MusicRepository musicRepository;

    @MockitoBean
    private MusicMapper musicMapper;

    private MockMultipartFile mockFile;
    private CreateMusicDTO createMusicDTO;
    private UpdateMusicDTO updateMusicDTO;
    private Music music;
    private GetMusicDTO getMusicDTO;
    private User user;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test data".getBytes()
        );

        createMusicDTO = new CreateMusicDTO();
        createMusicDTO.setTitle("Test Song");
        createMusicDTO.setArtist("Test Artist");
        createMusicDTO.setGenre("Pop");

        updateMusicDTO = new UpdateMusicDTO();
        updateMusicDTO.setTitle("Updated Song");
        updateMusicDTO.setArtist("Updated Artist");
        updateMusicDTO.setGenre("Rock");

        user = new User();
        user.setId(1L);

        music = new Music();
        music.setId(1L);
        music.setTitle("Test Song");
        music.setArtist("Test Artist");
        music.setGenre("Pop");
        music.setFilePath("src/main/resources/media/Test_Song_123456789.mp3");
        music.setUser(user);

        getMusicDTO = new GetMusicDTO();
        getMusicDTO.setId(1L);
        getMusicDTO.setTitle("Test Song");
        getMusicDTO.setArtist("Test Artist");
        getMusicDTO.setGenre("Pop");
        getMusicDTO.setPath("src/main/resources/media/Test_Song_123456789.mp3");
    }

    @AfterEach
    void tearDown() {
        reset(musicRepository, musicMapper);
    }

    // Tests for save method
    @Test
    void whenSaveValidMusicDTOWithFile_ThenReturnGetMusicDTO() throws IOException {
        when(musicMapper.toEntity(any(CreateMusicDTO.class))).thenReturn(music);
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(getMusicDTO);

        GetMusicDTO result = underTest.save(createMusicDTO, mockFile);

        assertNotNull(result);
        assertEquals("Test Song", result.getTitle());
        assertEquals("Test Artist", result.getArtist());
        assertEquals("Pop", result.getGenre());
        assertTrue(result.getPath().contains("Test_Song")); // Змінено з getFilePath на getPath
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void whenSaveWithNullFile_ThenThrowCustomException() {
        assertThrows(CustomException.class, () -> underTest.save(createMusicDTO, null),
                "Файл не передано.");
        verify(musicRepository, never()).save(any(Music.class));
    }

    @Test
    void whenSaveWithEmptyFile_ThenThrowCustomException() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.mp3", "audio/mpeg", new byte[0]);
        assertThrows(CustomException.class, () -> underTest.save(createMusicDTO, emptyFile),
                "Файл не передано.");
        verify(musicRepository, never()).save(any(Music.class));
    }

    @Test
    void whenSaveCreatesMediaDirectory_ThenDirectoryIsCreated() throws IOException {
        when(musicMapper.toEntity(any(CreateMusicDTO.class))).thenReturn(music);
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(getMusicDTO);

        underTest.save(createMusicDTO, mockFile);

        Path mediaPath = Paths.get("media");
        assertTrue(Files.exists(mediaPath));
    }

    // Tests for findById method
    @Test
    void whenFindByIdExists_ThenReturnGetMusicDTO() {
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(getMusicDTO);

        GetMusicDTO result = underTest.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Song", result.getTitle());
        verify(musicRepository).findById(1L);
    }

    @Test
    void whenFindByIdNotExists_ThenThrowCustomException() {
        when(musicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> underTest.findById(1L),
                "Музику з ID 1 не знайдено.");
        verify(musicRepository).findById(1L);
    }

    @Test
    void whenFindByIdNegative_ThenThrowCustomException() {
        when(musicRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> underTest.findById(-1L),
                "Музику з ID -1 не знайдено.");
        verify(musicRepository).findById(-1L);
    }

    // Tests for findAll method
    @Test
    void whenFindAllReturnsList_ThenReturnListOfGetMusicDTO() {
        when(musicRepository.findAll()).thenReturn(Collections.singletonList(music));
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(getMusicDTO);

        List<GetMusicDTO> result = underTest.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        verify(musicRepository).findAll();
    }

    @Test
    void whenFindAllEmpty_ThenReturnEmptyList() {
        when(musicRepository.findAll()).thenReturn(Collections.emptyList());

        List<GetMusicDTO> result = underTest.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(musicRepository).findAll();
    }

    @Test
    void whenFindAllMultipleItems_ThenReturnAllItems() {
        Music music2 = new Music();
        music2.setId(2L);
        music2.setTitle("Song 2");
        GetMusicDTO getMusicDTO2 = new GetMusicDTO();
        getMusicDTO2.setId(2L);
        getMusicDTO2.setTitle("Song 2");

        when(musicRepository.findAll()).thenReturn(List.of(music, music2));
        when(musicMapper.toGetMusicDTO(music)).thenReturn(getMusicDTO);
        when(musicMapper.toGetMusicDTO(music2)).thenReturn(getMusicDTO2);

        List<GetMusicDTO> result = underTest.findAll();

        assertEquals(2, result.size());
        verify(musicRepository).findAll();
    }

    // Tests for update method
    @Test
    void whenUpdateValidMusicDTOWithFile_ThenReturnUpdatedGetMusicDTO() throws IOException {
        GetMusicDTO updatedGetMusicDTO = new GetMusicDTO();
        updatedGetMusicDTO.setId(1L);
        updatedGetMusicDTO.setTitle("Test Song");
        updatedGetMusicDTO.setArtist("Updated Artist");
        updatedGetMusicDTO.setGenre("Rock");
        updatedGetMusicDTO.setPath("src/main/resources/media/Updated_Song_123456789.mp3");

        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(musicMapper.toEntity(any(UpdateMusicDTO.class), any(Music.class))).thenReturn(music);
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(updatedGetMusicDTO);

        GetMusicDTO result = underTest.update(1L, updateMusicDTO, mockFile);

        assertNotNull(result);
        assertEquals("Test Song", result.getTitle());
        assertEquals("Updated Artist", result.getArtist());
        assertEquals("Rock", result.getGenre());
        assertTrue(result.getPath().contains("Updated_Song"));
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void whenUpdateWithoutFile_ThenUpdateWithoutChangingFilePath() throws IOException {
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(musicMapper.toEntity(any(UpdateMusicDTO.class), any(Music.class))).thenReturn(music);
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(musicMapper.toGetMusicDTO(any(Music.class))).thenReturn(getMusicDTO);

        GetMusicDTO result = underTest.update(1L, updateMusicDTO, null);

        assertNotNull(result);
        assertEquals("Test Song", result.getTitle());
        assertEquals("src/main/resources/media/Test_Song_123456789.mp3", result.getPath());
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void whenUpdateNonExistingId_ThenThrowCustomException() {
        when(musicRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomException.class, () -> underTest.update(1L, updateMusicDTO, null),
                "Музику з ID 1 не знайдено.");
        verify(musicRepository, never()).save(any(Music.class));
    }

    @Test
    void whenUpdateWithEmptyFile_ThenThrowCustomException() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.mp3", "audio/mpeg", new byte[0]);
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        assertThrows(CustomException.class, () -> underTest.update(1L, updateMusicDTO, emptyFile),
                "Файл не передано.");
        verify(musicRepository, never()).save(any(Music.class));
    }

    // Tests for delete method
    @Test
    void whenDeleteExistingMusicByOwner_ThenReturnTrue() {
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        boolean result = underTest.delete(1L, 1L);

        assertTrue(result);
        verify(musicRepository).deleteById(1L);
    }

    @Test
    void whenDeleteNonExistingMusic_ThenThrowCustomException() {
        when(musicRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomException.class, () -> underTest.delete(1L, 1L),
                "Музику з ID 1 не знайдено.");
        verify(musicRepository, never()).deleteById(anyLong());
    }

    @Test
    void whenDeleteByNonOwner_ThenThrowCustomException() {
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        assertThrows(CustomException.class, () -> underTest.delete(1L, 2L),
                "Музика не належить користувачу з ID 2");
        verify(musicRepository, never()).deleteById(anyLong());
    }

    @Test
    void whenDeleteWithoutUserId_ThenDeleteSuccessfully() {
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        boolean result = underTest.delete(1L, null);

        assertTrue(result);
        verify(musicRepository).deleteById(1L);
    }

    @Test
    void whenDeleteMusicWithNullUser_ThenDeleteSuccessfully() {
        music.setUser(null);
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        boolean result = underTest.delete(1L, null);

        assertTrue(result);
        verify(musicRepository).deleteById(1L);
    }

    @Test
    void whenDeleteMusicWithNonMatchingUserId_ThenThrowCustomException() {
        when(musicRepository.existsById(1L)).thenReturn(true);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        assertThrows(CustomException.class, () -> underTest.delete(1L, 999L),
                "Музика не належить користувачу з ID 999");
        verify(musicRepository, never()).deleteById(anyLong());
    }
}