package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Client;
import org.javanamba.javahw12datajdbc.services.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Value("${app.table.size.clients}")
    private int recordsInTable;

    @GetMapping
    public ModelAndView getClients(
            @RequestParam Optional<String> searchKey,
            @RequestParam Optional<String> searchValue,
            @RequestParam(defaultValue = "0") int pagePrimaryContent,
            @RequestParam(defaultValue = "0") int pageSearchedContent,
            @RequestParam(name = "isUpdate", defaultValue = "false") Boolean isUpdate) {

        ModelAndView modelAndView = new ModelAndView("clients");

        // Получение всех клиентов
        Page<Client> listAllClients = clientService.findAll(pagePrimaryContent, recordsInTable);
        modelAndView.addObject("listAllClients", listAllClients);

        // Поиск клиентов, если заданы параметры поиска
        searchKey.ifPresent(key -> searchValue.ifPresent(value -> {
            if (!key.isEmpty() && !value.isEmpty()) {
                Page<Client> foundClients = clientService.searchClients(pageSearchedContent, recordsInTable, key, value);
                modelAndView.addObject("foundClients", foundClients);
            }
        }));

        // Добавление параметров поиска и пагинации в модель
        modelAndView.addObject("pageSearchedContent", pageSearchedContent);
        modelAndView.addObject("pagePrimaryContent", pagePrimaryContent);
        modelAndView.addObject("searchKey", searchKey.orElse(null));
        modelAndView.addObject("searchValue", searchValue.orElse(null));
        modelAndView.addObject("isUpdate", isUpdate);

        return modelAndView;
    }
}