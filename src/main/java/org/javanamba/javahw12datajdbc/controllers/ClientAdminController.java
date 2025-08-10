package org.javanamba.javahw12datajdbc.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Client;
import org.javanamba.javahw12datajdbc.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
public class ClientAdminController {

    private final ClientService clientService;

    @GetMapping("/add")
    public String showAddClientForm(Model model) {
        model.addAttribute("clientDto", new Client.ClientDto());
        return "admin/clients/add-client";
    }

    @PostMapping("/add")
    public String addClient(
            @RequestParam String city,
            @Valid @ModelAttribute("clientDto") Client.ClientDto clientDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/clients/add-client";
        }

        clientDto.setCity(city);
        Client client = mapToClient(clientDto);
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String showEditClientForm(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return "redirect:/clients";
        }
        model.addAttribute("clientDto", mapToClientDto(client));
        return "admin/clients/edit-client";
    }

    @PostMapping("/edit")
    public String editClient(
            @RequestParam String city,
            @Valid @ModelAttribute("clientDto") Client.ClientDto clientDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/clients/edit-client";
        }

        clientDto.setCity(city);
        clientService.saveClientDto(clientDto);
        return "redirect:/clients";
    }

    // Маппинг DTO -> Entity
    private Client mapToClient(Client.ClientDto dto) {
        return new Client(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPatronymic(),
                dto.getAge(),
                dto.getPhoneNumber(),
                dto.getCity()
        );
    }

    // Маппинг Entity -> DTO
    private Client.ClientDto mapToClientDto(Client client) {
        return new Client.ClientDto(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getPatronymic(),
                client.getPhoneNumber(),
                client.getAge(),
                client.getCity()
        );
    }
}