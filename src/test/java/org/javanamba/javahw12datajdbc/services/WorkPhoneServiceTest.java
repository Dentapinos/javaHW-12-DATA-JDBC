package org.javanamba.javahw12datajdbc.services;

import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.WorkPhoneRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkPhoneServiceTest {

    @Mock
    private WorkPhoneRepository workPhoneRepository;

    @InjectMocks
    private WorkPhoneService workPhoneService;

    private WorkPhone workPhone;

    @BeforeEach
    void setUp() {
        workPhone = new WorkPhone();
        workPhone.setId(1L);
        workPhone.setPhoneNumber("123456789");
        workPhone.setSerialNumber("SN123");
        workPhone.setOperatorId(10L);
        workPhone.setPhoneType("Mobile");
    }

    @Test
    @DisplayName("Поиск рабочих телефонов по номеру - успешный случай")
    void testSearchWorkPhonesByNumber() {
        Page<WorkPhone> mockPage = new PageImpl<>(List.of(workPhone));
        when(workPhoneRepository.findByPhoneNumberContaining(eq("123"), any()))
                .thenReturn(mockPage);

        Page<WorkPhone> result = workPhoneService.searchWorkPhones(0, 10, "phoneNumber", "123");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(workPhoneRepository).findByPhoneNumberContaining(eq("123"), any());
    }

    @Test
    @DisplayName("Поиск рабочих телефонов по серийному номеру - успешный случай")
    void testSearchWorkPhonesBySerialNumber() {
        Page<WorkPhone> mockPage = new PageImpl<>(List.of(workPhone));
        when(workPhoneRepository.findBySerialNumberContaining(eq("SN"), any()))
                .thenReturn(mockPage);

        Page<WorkPhone> result = workPhoneService.searchWorkPhones(0, 10, "serialNumber", "SN");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(workPhoneRepository).findBySerialNumberContaining(eq("SN"), any());
    }

    @Test
    @DisplayName("Поиск с некорректным ключом возвращает null")
    void testSearchWithInvalidKey() {
        Page<WorkPhone> result = workPhoneService.searchWorkPhones(0, 10, "invalidKey", "test");
        assertNull(result);
    }

    @Test
    @DisplayName("Получение всех активных рабочих телефонов")
    void testFindAllActive() {
        Page<WorkPhone> mockPage = new PageImpl<>(Collections.singletonList(workPhone));
        when(workPhoneRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<WorkPhone> result = workPhoneService.findAll(0, 10);

        assertNotNull(result);
        verify(workPhoneRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Получение всех рабочих телефонов с пагинацией")
    void testGetAllWorkPhonesWithPagination() {
        List<WorkPhone> list = List.of(workPhone);
        Page<WorkPhone> page = new PageImpl<>(list);

        when(workPhoneRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<WorkPhone> result = workPhoneService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(workPhone, result.getContent().get(0));

        verify(workPhoneRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Получение свободных активных рабочих телефонов")
    void testGetAllFreeWorkPhones() {
        List<WorkPhone> list = List.of(workPhone);
        when(workPhoneRepository.findFree()).thenReturn(list);

        List<WorkPhone> result = workPhoneRepository.findFree();

        assertEquals(1, result.size());
        verify(workPhoneRepository).findFree();
    }

    @Test
    @DisplayName("Получение рабочего телефона по ID - успешный случай")
    void testGetWorkPhoneById() {
        when(workPhoneRepository.findById(1L)).thenReturn(Optional.of(workPhone));

        WorkPhone result = workPhoneService.getWorkPhoneById(1L);

        assertNotNull(result);
        assertEquals(workPhone.getId(), result.getId());
        verify(workPhoneRepository).findById(1L);
    }

    @Test
    @DisplayName("Получение рабочего телефона по ID - не найдено")
    void testGetWorkPhoneByIdNotFound() {
        when(workPhoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> workPhoneService.getWorkPhoneById(999L));

        verify(workPhoneRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Создание нового рабочего телефона")
    void testCreateWorkTelephone() {
        when(workPhoneRepository.save(any())).thenReturn(workPhone);

        WorkPhone created = workPhoneService.createWorkPhone(workPhone);

        assertNotNull(created);
        verify(workPhoneRepository).save(any());
    }

    @Test
    @DisplayName("Обновление существующего рабочего телефона")
    void testUpdateWorkTelephone() {
        WorkPhone updatedDetails = new WorkPhone();
        updatedDetails.setSerialNumber("SN999");
        updatedDetails.setPhoneNumber("987654321");
        updatedDetails.setPhoneType("Landline");
        updatedDetails.setOperatorId (20L);

        when(workPhoneRepository.findById(1L)).thenReturn(Optional.of(workPhone));
        when(workPhoneRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        WorkPhone result = workPhoneService.updateWorkPhone(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("SN999", result.getSerialNumber());
        verify(workPhoneRepository).save(any());
    }

    @Test
    @DisplayName("Удаление рабочего телефона по ID")
    void testDeleteWorkTelephone() {
        doNothing().when(workPhoneRepository).deleteById(anyLong());

        workPhoneService.deleteWorkPhone(1L);
        verify(workPhoneRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Сохранение рабочего телефона")
    void testSaveWorkTelephone() {
        when(workPhoneRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        workPhoneService.save(workPhone);
        verify(workPhoneRepository, times(1)).save(any());
    }
}
