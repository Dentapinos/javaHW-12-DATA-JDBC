package org.javanamba.javahw12datajdbc.repository;

import org.javanamba.javahw12datajdbc.entity.Operator;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorRepository extends PagingAndSortingRepository<Operator, Long>, ListCrudRepository<Operator, Long> {

    @Query("SELECT id, first_name, last_name FROM operators WHERE status = true ORDER BY first_name ")
    List<Operator> getAllOperatorsWithIdAndFirstLastName();

    @Query("SELECT * FROM operators WHERE status = :status LIMIT :limit OFFSET :offset")
    List<Operator> findAllByStatus(@Param("status") boolean status, @Param("limit") int limit, @Param("offset") long offset);

    @Query("SELECT COUNT(*) FROM operators WHERE status = :status")
    long countByStatus(@Param("status") boolean status);


    @Query("SELECT o.* FROM operators o WHERE EXISTS (SELECT 1 FROM tasks t WHERE t.operator_id = o.id AND status = TRUE) LIMIT :limit OFFSET :offset")
    List<Operator> findAllWhereIsPresentTasks(@Param("status") boolean status, @Param("limit") int limit, @Param("offset") long offset);

    @Query("SELECT COUNT(o.id) FROM operators o WHERE EXISTS (SELECT 1 FROM tasks t WHERE t.operator_id = o.id AND status = TRUE)")
    long countByTask(@Param("status") boolean status);
}
