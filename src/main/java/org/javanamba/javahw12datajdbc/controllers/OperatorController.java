package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.services.OperatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    @Value("${app.table.size.operators}")
    private int recordsInTable;

    @GetMapping("/operators")
    public String getOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int searchPage,
            @RequestParam(required = false) String keyBy,
            @RequestParam(required = false) String search,
            @RequestParam(name = "isUpdate", defaultValue = "false") Boolean isUpdate,
            Model model) {

        // Получение всех операторов в зависимости от режима администрирование или просмотр
        Page<Operator> pageOperators = isUpdate
                ? operatorService.getAllActiveSortedOperatorsByStatus(page, recordsInTable)
                : operatorService.getAllSortedOperatorsByStatus(page, recordsInTable);
        model.addAttribute("pageOperators", pageOperators);

        // Поиск операторов, если заданы параметры поиска
        if (keyBy != null && search != null && !keyBy.isEmpty() && !search.isEmpty()) {
            Page<Operator> foundOperators = operatorService.searchOperators(keyBy, search, searchPage, recordsInTable, isUpdate);
            model.addAttribute("foundOperators", foundOperators);
            model.addAttribute("keyBy", keyBy);
            model.addAttribute("search", search);
        }

        model.addAttribute("isUpdate", isUpdate);
        return "operators";
    }
}