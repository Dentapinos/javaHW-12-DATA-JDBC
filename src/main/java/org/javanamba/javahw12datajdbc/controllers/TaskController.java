package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.services.OperatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final OperatorService operatorService;

    @Value("${app.table.size.tasks}")
    private int recordsInTable;

    @GetMapping
    public String getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int searchPage,
            @RequestParam(required = false) String keyBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(defaultValue = "false") Boolean isUpdate,
            Model model){

        System.out.println("11");
        // Получение всех операторов
        Page<Operator> operators;
        if (!isUpdate) {
            operators = operatorService.getAllActiveSortedOperatorsByStatus(page, recordsInTable);
        } else {
            operators = operatorService.getAllWherePresentTask(page, recordsInTable);
        }
        model.addAttribute("operators", operators);

        // Получение списка операторов с ID и именами
        List<Operator> listOperatorWitchIdAndName = operatorService.getAllWitchIdAndNameAndLastName();
        model.addAttribute("listNames", listOperatorWitchIdAndName);

        // Поиск операторов, если заданы параметры поиска
        if (keyBy != null && search != null && !keyBy.isEmpty() && !search.isEmpty()) {
            Page<Operator> foundOperators = operatorService.searchOperators(keyBy, search, searchPage, recordsInTable, isUpdate);
            model.addAttribute("foundOperators", foundOperators);
            model.addAttribute("keyBy", keyBy);
            model.addAttribute("search", search);
        }

        // Поиск оператора по ID, если задан ID
        if (operatorId != null) {
            Operator foundOperator = operatorService.getOperatorById(operatorId);
            model.addAttribute("foundOperator", foundOperator);
            model.addAttribute("operatorId", operatorId);
        }

        model.addAttribute("isUpdate", isUpdate);

        return "tasks";
    }
}