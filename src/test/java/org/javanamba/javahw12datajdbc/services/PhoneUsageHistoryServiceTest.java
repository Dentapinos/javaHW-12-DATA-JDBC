package org.javanamba.javahw12datajdbc.services;

import org.javanamba.jhwjdbcdata.dtos.PhoneUsageHistoryDTO;
import org.javanamba.jhwjdbcdata.entity.PhoneUsageHistory;
import org.javanamba.jhwjdbcdata.repository.PhoneUsageHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneUsageHistoryServiceTest {

    @Mock
    private PhoneUsageHistoryRepository repository;

    @InjectMocks
    private PhoneUsageHistoryService service;

    @Test
    @DisplayName("Поиск по оператору по имени - успешный случай")
    void testSearchWithValidKey() {
        // Arrange
        String key = "operatorFirstName";
        String searchTerm = "John";
        Page<PhoneUsageHistoryDTO> mockPage = new PageImpl<>(List.of(
                new PhoneUsageHistoryDTO(1L,"John","Doe","SN123","1234567890",null,null)
        ));
        when(repository.findByOperatorFirstNameContaining(eq(searchTerm), any())).thenReturn(mockPage);

        // Act
        Page<PhoneUsageHistoryDTO> result = service.searchPhoneUsageHistory(0, 10, key, searchTerm);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(repository).findByOperatorFirstNameContaining(eq(searchTerm), any());
    }

    @Test
    @DisplayName("Поиск с некорректным ключом возвращает пустую страницу")
    void testSearchWithInvalidKeyReturnsEmpty() {
        // Arrange
        String key = "invalidKey";

        // Act
        Page<PhoneUsageHistoryDTO> result = service.searchPhoneUsageHistory(0, 10, key, "test");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение всех записей - возвращает данные")
    void testFindAllReturnsData() {
        // Arrange
        Page<PhoneUsageHistoryDTO> mockPage = new PageImpl<>(Collections.emptyList());
        when(repository.findAll(any())).thenReturn(mockPage);

        // Act
        Page<PhoneUsageHistoryDTO> result = service.findAll(0, 10);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Создание новой записи истории использования")
    void testSaveNewEntry() {
        // Arrange
        doNothing().when(repository).save(any());

        // Act
        service.saveHistory(1L, 2L,true);

        // Assert
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Обновление существующей записи истории использования")
    void testUpdateExistingEntry() {
        // Arrange
        PhoneUsageHistory mockHist = new PhoneUsageHistory();
        mockHist.setOperatorId(2L);
        mockHist.setPhoneId(1L);

        when(repository.findHistoryByOperatorIdAndPhoneId(2L, 1L))
                .thenReturn(Optional.of(mockHist));

        doNothing().when(repository).update(any());

        // Act
        service.saveHistory(1L, 2L,false);

        // Assert
        verify(repository).update(any());
        assertNotNull(mockHist.getUsageEnd());
    }
}