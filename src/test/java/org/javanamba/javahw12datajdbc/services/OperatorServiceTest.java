package org.javanamba.javahw12datajdbc.services;

import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.CustomOperatorRepository;
import org.javanamba.javahw12datajdbc.repository.OperatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class OperatorServiceTest {

    @InjectMocks
    private OperatorService operatorService;

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private CustomOperatorRepository customOperatorRepository;

    private Operator operator1;
    private Operator operator2;

    @BeforeEach
    void setUp() {
        operator1 = new Operator();
        operator1.setId(1L);
        operator1.setFirstName("Ivan");
        operator1.setLastName("Ivanov");
        operator1.setStatus(true);
        operator1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        operator1.setEmail("ivanov@example.com");
        operator1.setPhoneNumber("1234567890");
        operator1.setAddress("Address 1");

        operator2 = new Operator();
        operator2.setId(2L);
        operator2.setFirstName("Petr");
        operator2.setLastName("Petrov");
        operator2.setStatus(true);
        operator2.setDateOfBirth(LocalDate.of(1985, 5, 20));
        operator2.setEmail("petrov@example.com");
        operator2.setPhoneNumber("0987654321");
        operator2.setAddress("Address 2");
    }

    @Test
    @DisplayName("Получение всех операторов с сортировкой по статусу и ID")
    void testGetAllSortedOperatorsByStatus() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("status").descending().and(Sort.by("id").ascending()));
        Page<Operator> page = new PageImpl<>(Arrays.asList(operator1, operator2), pageable, 2);

        Mockito.when(operatorRepository.findAll(pageable)).thenReturn(page);

        Page<Operator> result = operatorService.getAllSortedOperatorsByStatus(0,10);

        assertThat(result).hasSize(2)
                .containsExactly(operator1, operator2);
        Mockito.verify(operatorRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Получение всех активных операторов с сортировкой по статусу")
    void testGetAllActiveSortedOperatorsByStatus() {
        long total = 1L;
        List<Operator> list = Collections.singletonList(operator1);
        Mockito.when(operatorRepository.countByStatus(true)).thenReturn(total);
        Mockito.when(operatorRepository.findAllByStatus(true, 10, 0)).thenReturn(list);

        Page<Operator> result = operatorService.getAllActiveSortedOperatorsByStatus(0,10);

        assertThat(result).hasSize(1).containsExactly(operator1);
        assertThat(result.getTotalElements()).isEqualTo(total);
    }

    @Test
    @DisplayName("Получение операторов с присутствующими задачами")
    void testGetAllWherePresentTask() {
        long total = 3L;
        List<Operator> list = Collections.singletonList(operator2);
        Mockito.when(operatorRepository.countByTask(true)).thenReturn(total);
        Mockito.when(operatorRepository.findAllWhereIsPresentTasks(true, 10, 0)).thenReturn(list);

        Page<Operator> result = operatorService.getAllWherePresentTask(0,10);

        assertThat(result).hasSize(1)
                .containsExactly(operator2);
    }

    @Test
    @DisplayName("Поиск операторов по ключу (успешный случай)")
    void testSearchOperators_success() {
        String keyBy = "firstName";
        String search = "Ivan";
        Pageable pageable = PageRequest.of(0,10,Sort.by("status").descending().and(Sort.by("id").ascending()));

        Page<Operator> pageResult = new PageImpl<>(Collections.singletonList(operator1));

        Mockito.when(customOperatorRepository.searchOperators(keyBy, search, pageable, true))
                .thenReturn(pageResult);

        Page<Operator> result = operatorService.searchOperators(keyBy, search, 0,10,true);

        assertThat(result).hasSize(1).containsExactly(operator1);

        Mockito.verify(customOperatorRepository).searchOperators(keyBy, search, pageable, true);
    }

    @Test
    @DisplayName("Поиск операторов вызывает исключение при ошибке")
    void testSearchOperators_exception() {
        Mockito.when(customOperatorRepository.searchOperators(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() ->
                operatorService.searchOperators("key", "value", 0,10,true))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ошибка при поиске операторов");
    }

    @Test
    @DisplayName("Получение оператора по существующему ID")
    void testGetOperatorById_found() {
        Long id = 1L;

        Mockito.when(operatorRepository.findById(id)).thenReturn(Optional.of(operator1));

        Operator result = operatorService.getOperatorById(id);

        assertThat(result).isEqualTo(operator1);

        Mockito.verify(operatorRepository).findById(id);
    }

    @Test
    @DisplayName("Обработка отсутствия оператора по ID")
    void testGetOperatorById_notFound() {
        Long id = 99L;

        Mockito.when(operatorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                operatorService.getOperatorById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Оператор с ID " + id + " не найден");
    }

    @Test
    @DisplayName("Получение всех операторов с ID и именами")
    void testGetAllWitchIdAndNameAndLastName() {

        List<Operator> list = Arrays.asList(operator1, operator2);
        Mockito.when(operatorRepository.getAllOperatorsWithIdAndFirstLastName()).thenReturn(list);

        List<Operator> result = operatorService.getAllWitchIdAndNameAndLastName();

        assertThat(result).containsExactlyInAnyOrder(operator1,operator2);
    }

    @Test
    @DisplayName("Сохранение оператора с корректными данными")
    void testSaveOperatorDto_success() {
        Operator.OperatorDto dto = new Operator.OperatorDto();
        dto.setFirstName("IvanUpdated");
        dto.setLastName("IvanovUpdated");
        dto.setDateOfBirth(LocalDate.of(1990, 2, 2));
        dto.setEmail("newemail@example.com");
        dto.setPhone("1112223333");
        dto.setAddress("New Address");

        // Мокаем существующего оператора и сохранение
        Mockito.when(operatorRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Operator updatedOp = (operatorService.saveOperatorDto(dto));

        assertThat(updatedOp.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(updatedOp.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @DisplayName("Обновление оператора с корректными данными")
    void testUpdateOperatorDto_success() {
        Operator.OperatorDto dto = new Operator.OperatorDto();
        dto.setId(2L);
        dto.setFirstName("PetrUpdated");
        dto.setLastName("PetrovUpdated");
        dto.setDateOfBirth(LocalDate.of(1985, 6, 15));
        dto.setEmail("petrnew@example.com");
        dto.setPhone("2223334444");
        dto.setAddress("Updated Address");

        // Мокаем существующего оператора и сохранение
        Mockito.when(operatorRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Operator updatedOp = (operatorService.saveOperatorDto(dto));

        assertThat(updatedOp.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(updatedOp.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @DisplayName("Удаление оператора по существующему ID")
    void testDeleteOperatorById_success() {
        Long id= 1L;

        // Мокаем существующего оператора
        Mockito.when(operatorRepository.findById(id)).thenReturn(Optional.of(operator1));

        // Мокаем сохранение после изменения статуса
        Mockito.when(operatorRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Operator result = operatorService.deleteOperatorById(id);

        assertThat(result.getStatus()).isFalse();

        // Проверка что статус изменен на false
        assertThat(result.getStatus()).isFalse();

        // Проверка что вызван save
        Mockito.verify(operatorRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("Обработка удаления несуществующего оператора по ID")
    void testDeleteOperatorById_notFound() {
        Long id=99L;

        Mockito.when(operatorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                operatorService.deleteOperatorById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Оператор с ID " + id + " не найден");
    }
}