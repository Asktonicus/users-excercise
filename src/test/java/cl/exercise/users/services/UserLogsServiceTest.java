package cl.exercise.users.services;

import cl.exercise.users.dto.log.UserLogDTO;
import cl.exercise.users.mapper.MapperHelper;
import cl.exercise.users.model.UserLogModel;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.repository.UserLogRepository;
import cl.exercise.users.service.impl.UserLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserLogServiceImplTest {

    @Mock
    private UserLogRepository userLogRepository;

    @Mock
    private MapperHelper mapperHelper;

    @InjectMocks
    private UserLogServiceImpl userLogService;

    private UserLogModel userLogModel;
    private UserLogDTO userLogDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        UserModel userModel = new UserModel();
        userModel.setId(userId);

        userLogModel = new UserLogModel();
        userLogModel.setId(UUID.randomUUID());
        userLogModel.setUserModel(userModel);
        userLogModel.setAction("UPDATE");
        userLogModel.setCreationDate(LocalDateTime.now());

        userLogDTO = new UserLogDTO();
        userLogDTO.setIdUser(String.valueOf(userId));
        userLogDTO.setAction("UPDATE");
        userLogDTO.setCreationDate(userLogModel.getCreationDate());
    }

    @Test
    void testGetAllLogWithoutFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserLogModel> page = new PageImpl<>(List.of(userLogModel));

        when(userLogRepository.findAll(pageable)).thenReturn(page);
        when(mapperHelper.mapLogToDTO(userLogModel)).thenReturn(userLogDTO);

        Page<UserLogDTO> result = userLogService.getAllLog(1, 10, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("UPDATE", result.getContent().get(0).getAction());
        verify(userLogRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllLogWithUserIdFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserLogModel> page = new PageImpl<>(List.of(userLogModel));

        when(userLogRepository.findByUserModel_Id(userId, pageable)).thenReturn(page);
        when(mapperHelper.mapLogToDTO(userLogModel)).thenReturn(userLogDTO);

        Page<UserLogDTO> result = userLogService.getAllLog(1, 10, userId);

        assertEquals(1, result.getTotalElements());
        assertEquals(String.valueOf(userId), String.valueOf(result.getContent().get(0).getIdUser()));
        verify(userLogRepository, times(1)).findByUserModel_Id(userId, pageable);
    }

    @Test
    void getAllLog_whenPageIsNull_shouldDefaultToPageZero() {
        when(userLogRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<UserLogDTO> result = userLogService.getAllLog(null, 10, null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userLogRepository).findAll(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
    }

    @Test
    void getAllLog_whenPageIsLessThanOne_shouldDefaultToPageZero() {
        when(userLogRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<UserLogDTO> result = userLogService.getAllLog(0, 10, null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userLogRepository).findAll(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
    }

    @Test
    void getAllLog_whenSizeIsNull_shouldDefaultToTen() {
        when(userLogRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        userLogService.getAllLog(1, null,null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userLogRepository).findAll(captor.capture());
        assertEquals(10, captor.getValue().getPageSize());
    }


    @Test
    void getAllLog_whenSizeIsLessThanOne_shouldDefaultToTen() {
        when(userLogRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        userLogService.getAllLog(1, 0,null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userLogRepository).findAll(captor.capture());
        assertEquals(10, captor.getValue().getPageSize());
    }

}

