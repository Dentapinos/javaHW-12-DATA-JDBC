package org.javanamba.javahw12datajdbc.services;

import org.javanamba.javahw12datajdbc.entity.Client;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client sampleClient;

    @BeforeEach
    void setUp() {
        sampleClient = new Client();
        sampleClient.setId(1L);
        sampleClient.setFirstName("John");
        sampleClient.setLastName("Doe");
        sampleClient.setPatronymic("Ivanovich");
        sampleClient.setPhoneNumber("1234567890");
        sampleClient.setAge(30);
    }

    @Test
    @DisplayName("Получение клиента по ID, когда клиент найден")
    void testGetClientById_Found() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));

        Client result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("Получение клиента по ID, когда клиент не найден")
    void testGetClientById_NotFound() {
        when(clientRepository.findById(2L)).thenReturn(Optional.empty());

        Client result = clientService.getClientById(2L);

        assertNull(result);
        verify(clientRepository).findById(2L);
    }

    @Test
    @DisplayName("Обновление клиента через DTO успешно")
    void testSaveClientDto_Success() {
        // Мокаем существующего клиента
        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));

        // Создаем DTO для обновления
        Client.ClientDto dto = new Client.ClientDto();
        dto.setId(1L);
        dto.setFirstName("UpdatedName");
        dto.setLastName("UpdatedLastName");
        dto.setPatronymic("UpdatedPatronymic");
        dto.setPhoneNumber("0987654321");
        dto.setAge(35);

        clientService.saveClientDto(dto);

        verify(clientRepository).save(any(Client.class));

        // Проверяем, что поля обновлены
        assertEquals("UpdatedName", sampleClient.getFirstName());
    }

    @Test
    @DisplayName("Обновление клиента через DTO, когда клиент не найден")
    void testSaveClientDto_ClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        Client.ClientDto dto = new Client.ClientDto();
        dto.setId(99L);

        assertThrows(EntityNotFoundException.class, () -> {
            clientService.saveClientDto(dto);
        });

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Поиск клиентов по имени с использованием фильтрации")
    void testSearchClients_ByFirstName() {
        Page<Client> page = new PageImpl<>(Collections.singletonList(sampleClient));

        when(clientRepository.findByFirstNameContaining(anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Client> result = clientService.searchClients(0, 10, "firstName", "John");

        assertFalse(result.isEmpty());

        verify(clientRepository).findByFirstNameContaining(eq("John"), any(Pageable.class));
    }

    @Test
    @DisplayName("Поиск клиентов по возрасту с валидным числом")
    void testSearchClients_ByAge_ValidNumber() {
        Page<Client> page = new PageImpl<>(Collections.singletonList(sampleClient));

        when(clientRepository.findByAge(eq(30), any(Pageable.class)))
                .thenReturn(page);

        Page<Client> result = clientService.searchClients(0, 10, "age", "30");

        assertFalse(result.isEmpty());
        verify(clientRepository).findByAge(eq(30), any(Pageable.class));
    }

    @Test
    @DisplayName("Поиск клиентов по возрасту с некорректным числом")
    void testSearchClients_ByAge_InvalidNumber() {
        Page<Client> result = clientService.searchClients(0, 10, "age", "notANumber");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение всех активных клиентов")
    void testFindAll_ReturnsClients() {
        Page<Client> page = new PageImpl<>(Collections.singletonList(sampleClient));
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Client> result = clientService.findAll(0, 10);

        assertFalse(result.isEmpty());

        verify(clientRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Обработка исключения при получении всех клиентов")
    void testFindAll_ExceptionThrown() {
        when(clientRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());

        assertThrows(EntityNotFoundException.class, () -> clientService.findAll(0, 10));
    }

    @Test
    @DisplayName("Обработка исключения при сохранении клиента")
    void testSave_ExceptionThrown() {
        doThrow(new RuntimeException()).when(clientRepository).save(any(Client.class));

        assertThrows(EntityNotFoundException.class, () -> clientService.save(sampleClient));
    }
}