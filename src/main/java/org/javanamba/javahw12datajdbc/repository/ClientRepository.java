package org.javanamba.javahw12datajdbc.repository;

import org.javanamba.javahw12datajdbc.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Long>, CrudRepository<Client, Long> {

    Page<Client> findByFirstNameContaining(String firstName, Pageable pageable);

    Page<Client> findByLastNameContaining(String lastName, Pageable pageable);

    Page<Client> findByPatronymicContaining(String patronymic, Pageable pageable);

    Page<Client> findByPhoneNumberContaining(String phone, Pageable pageable);

    Page<Client> findByCityContaining(String address, Pageable pageable);

    Page<Client> findByAge(int age, Pageable pageable);

    @Query("SELECT c.id, c.first_name, c.last_name, c.patronymic FROM clients c")
    List<Client> getFirstNameAndLastNameAndPatronymic();

}
