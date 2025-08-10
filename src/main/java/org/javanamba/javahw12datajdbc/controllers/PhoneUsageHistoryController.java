package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.PhoneUsageHistoryDTO;
import org.javanamba.javahw12datajdbc.services.PhoneUsageHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/phone-usage-history")
@RequiredArgsConstructor
public class PhoneUsageHistoryController {

    private final PhoneUsageHistoryService phoneUsageHistoryService;

    @Value("${app.table.size.histories}")
    private int recordsInTable;

    @GetMapping
    public String getPhoneUsageHistory(
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false, defaultValue = "0") int pagePrimaryContent,
            @RequestParam(required = false, defaultValue = "0") int pageSearchedContent,
            Model model) {

        // Получение всей истории использования телефонов
        Page<PhoneUsageHistoryDTO> listAllPhoneUsageHistory = phoneUsageHistoryService.findAll(pagePrimaryContent, recordsInTable);
        model.addAttribute("listAllPhoneUsageHistory", listAllPhoneUsageHistory);

        // Поиск истории использования телефонов, если заданы параметры поиска
        if (searchKey != null && searchValue != null && !searchKey.isEmpty()) {
            Page<PhoneUsageHistoryDTO> foundPhoneUsageHistory = phoneUsageHistoryService.searchPhoneUsageHistory(pageSearchedContent, recordsInTable, searchKey, searchValue);
            model.addAttribute("foundPhoneUsageHistory", foundPhoneUsageHistory);
        }

        model.addAttribute("pageSearchedContent", pageSearchedContent);
        model.addAttribute("pagePrimaryContent", pagePrimaryContent);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("searchValue", searchValue);

        return "phone_usage_history";
    }
}