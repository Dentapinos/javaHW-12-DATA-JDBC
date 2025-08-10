package org.javanamba.javahw12datajdbc.services;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.CustomOperatorRepository;
import org.javanamba.javahw12datajdbc.repository.OperatorRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final CustomOperatorRepository customOperatorRepository;

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("status").descending().and(Sort.by("id").ascending()));
    }

    public Page<Operator> getAllSortedOperatorsByStatus(int page, int size) {
        Pageable pageable = createPageable(page, size);
        try {
            return operatorRepository.findAll(pageable);
        } catch (Exception e) {
            throw new EntityNotFoundException("Ошибка при получении данных");
        }
    }

    public Page<Operator> getAllActiveSortedOperatorsByStatus(int page, int size) {
        Pageable pageable = createPageable(page, size);
        long total = operatorRepository.countByStatus(true);
        List<Operator> operators = operatorRepository.findAllByStatus(true, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(operators, pageable, total);
    }

    public Page<Operator> getAllWherePresentTask(int page, int size) {
        Pageable pageable = createPageable(page, size);
        long total = operatorRepository.countByTask(true);
        List<Operator> operators = operatorRepository.findAllWhereIsPresentTasks(true, pageable.getPageSize(), pageable.getOffset());
        return new PageImpl<>(operators, pageable, total);
    }

    public Page<Operator> searchOperators(String keyBy, String search, int page, int size, boolean isUpdate) {
        Pageable pageable = createPageable(page, size);
        try {
            return customOperatorRepository.searchOperators(keyBy, search, pageable, isUpdate);
        } catch (Exception e) {
            throw new EntityNotFoundException("Ошибка при поиске операторов");
        }
    }

    public Operator getOperatorById(Long id) {
        return operatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Оператор с ID " + id + " не найден"));
    }

    public List<Operator> getAllWitchIdAndNameAndLastName() {
        return operatorRepository.getAllOperatorsWithIdAndFirstLastName();
    }

    //Транзакционные методы, потому чо происходит изменение данных
    @Transactional
    public Operator saveOperatorDto(Operator.OperatorDto operatorDto) {
        Operator operator = new Operator();
        // Обновление полей
        operator.setFirstName(operatorDto.getFirstName());
        operator.setLastName(operatorDto.getLastName());
        operator.setDateOfBirth(operatorDto.getDateOfBirth());
        operator.setEmail(operatorDto.getEmail());
        operator.setPhoneNumber(operatorDto.getPhone());
        operator.setStatus(true);
        operator.setAddress(operatorDto.getAddress());
        return operatorRepository.save(operator);
    }

    @Transactional
    public void updateOperatorDto(Operator.OperatorDto operatorDto) {
        Operator operator = new Operator();
        // Обновление полей
        operator.setId(operatorDto.getId());
        operator.setFirstName(operatorDto.getFirstName());
        operator.setLastName(operatorDto.getLastName());
        operator.setDateOfBirth(operatorDto.getDateOfBirth());
        operator.setEmail(operatorDto.getEmail());
        operator.setPhoneNumber(operatorDto.getPhone());
        operator.setStatus(true);
        operator.setAddress(operatorDto.getAddress());
        operatorRepository.save(operator);
    }

    @Transactional
    public void saveOperator(Operator operator) {
        operatorRepository.save(operator);
    }

    @Transactional
    public Operator deleteOperatorById(Long id) {
        Operator operator = getOperatorById(id); // выбрасывает исключение если не найден
        // Пометка оператора как неактивного и очистка задач
        operator.setStatus(false);
        operator.setTasks(null); // или new ArrayList<>(), в зависимости от реализации
        return operatorRepository.save(operator);
    }

}