package org.javanamba.javahw12datajdbc.services;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Client;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private static final Map<String, SearchFunction> SEARCH_FUNCTIONS = new HashMap<>();

    static {
        SEARCH_FUNCTIONS.put("firstName", ClientRepository::findByFirstNameContaining);
        SEARCH_FUNCTIONS.put("lastName", ClientRepository::findByLastNameContaining);
        SEARCH_FUNCTIONS.put("patronymic", ClientRepository::findByPatronymicContaining);
        SEARCH_FUNCTIONS.put("phone", ClientRepository::findByPhoneNumberContaining);
        SEARCH_FUNCTIONS.put("city", ClientRepository::findByCityContaining);
        SEARCH_FUNCTIONS.put("age", (repo, search, pageable) -> {
            try {
                int age = Integer.parseInt(search);
                return repo.findByAge(age, pageable);
            } catch (NumberFormatException e) {
                return Page.empty();
            }
        });
    }

    public void saveClientDto(Client.@Valid ClientDto clientDto) {
        Client client = clientRepository.findById(clientDto.getId()).orElseThrow(
                ()-> new EntityNotFoundException("Такого клиента с ID:" + clientDto.getId() + " не найдено")
        );
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setPatronymic(clientDto.getPatronymic());
        client.setPhoneNumber(clientDto.getPhoneNumber());
        client.setAge(clientDto.getAge());
        clientRepository.save(client);
    }

    @FunctionalInterface
    interface SearchFunction {
        Page<Client> apply(ClientRepository repository, String searchTerm, Pageable pageable);
    }

    public Page<Client> searchClients(int page, int size, String key, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty() && key != null && !key.isEmpty()) {
            SearchFunction func = SEARCH_FUNCTIONS.get(key);
            if (func != null) {
                try {
                    return func.apply(clientRepository, search, pageable);
                } catch (Exception e) {
                    throw new  EntityNotFoundException("Ошибка поиска");
                }

            }
        }
        return Page.empty();
    }

    public Page<Client> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            return clientRepository.findAll(pageable);
        } catch (Exception e) {
            throw new EntityNotFoundException("Ошибка поиска всех клиентов");
        }
    }

    public void save(@Valid Client client) {
        try {
            clientRepository.save(client);
        } catch (Exception e) {
            throw new EntityNotFoundException("Ошибка сохранения клиента");
        }
    }

    @Nullable
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public List<Client> getAllClientsOnlyNameAndId() {
        return clientRepository.getFirstNameAndLastNameAndPatronymic();
    }
}