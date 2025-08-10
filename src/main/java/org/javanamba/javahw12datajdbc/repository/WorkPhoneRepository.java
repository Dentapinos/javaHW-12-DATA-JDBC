package org.javanamba.javahw12datajdbc.repository;

import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkPhoneRepository extends PagingAndSortingRepository<WorkPhone, Long>,CrudRepository<WorkPhone, Long> {

    // Поиск по номеру телефона
    Page<WorkPhone> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);

    // Поиск по серийному номеру
    Page<WorkPhone> findBySerialNumberContaining(String serialNumber, Pageable pageable);

    // Телефон, закрепленный за оператором
    @Query("""
           SELECT * FROM work_phones w
           WHERE w.operator_id = :operatorId
           """)
    Optional<WorkPhone> findByOperatorId(@Param("operatorId") Long operatorId);

    // Свободные активные телефоны
    @Query("""
           SELECT * FROM work_phones w
           WHERE w.operator_id IS NULL
           """)
    List<WorkPhone> findFree();
}