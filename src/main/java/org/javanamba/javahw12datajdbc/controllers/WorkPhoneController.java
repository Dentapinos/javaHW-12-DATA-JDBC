package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.javanamba.javahw12datajdbc.services.WorkPhoneService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/work-phones")
@RequiredArgsConstructor
public class WorkPhoneController {

    private final WorkPhoneService workPhoneService;

    @Value("${app.table.size.phones}")
    private int recordsInTable;

    @GetMapping
    public String getWorkPhones(
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String searchValue,
            @RequestParam(defaultValue = "0") int pagePrimaryContent,
            @RequestParam(defaultValue = "0") int pageSearchedContent,
            @RequestParam(name = "isUpdate", defaultValue = "false") Boolean isUpdate,
            Model model) {

        // Получение всех телефонов
        Page<WorkPhone> allPhones = workPhoneService.findAll(pagePrimaryContent, recordsInTable);
        model.addAttribute("all", allPhones);

        // Поиск телефонов, если заданы параметры поиска
        if (searchKey != null && searchValue != null && !searchKey.isEmpty() && !searchValue.isEmpty()) {
            Page<WorkPhone> searchedPhones = workPhoneService.searchWorkPhones(pageSearchedContent, recordsInTable, searchKey, searchValue);
            model.addAttribute("searchedList", searchedPhones);
        }

        // Добавление параметров поиска и пагинации в модель
        model.addAttribute("pageSearchedContent", pageSearchedContent);
        model.addAttribute("pagePrimaryContent", pagePrimaryContent);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("isUpdate", isUpdate);

        return "work_phones";
    }
}