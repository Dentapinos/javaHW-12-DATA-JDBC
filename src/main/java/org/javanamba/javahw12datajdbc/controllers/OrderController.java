package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.OrderDTO;
import org.javanamba.javahw12datajdbc.enums.OrderStatus;
import org.javanamba.javahw12datajdbc.services.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${app.table.size.orders}")
    private int recordsInTable = 4;

    @GetMapping
    public String getOrders(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "keyBy", required = false) String keyBy,
            @RequestParam(value = "pageBasic", defaultValue = "0") int pageBasic,
            @RequestParam(value = "pageSearch", defaultValue = "0") int pageSearch,
            @RequestParam(value = "isUpdate", defaultValue = "false") Boolean isUpdate,
            Model model) {

        // Получение всех заказов
        Page<OrderDTO> orders = orderService.getAllOrders(pageBasic, recordsInTable);
        model.addAttribute("orders", orders);

        // Поиск заказов, если заданы параметры поиска
        if (keyBy != null && search != null && !search.isEmpty()) {
            if (keyBy.equals("op_cl")) {
                keyBy = search;
            }
            Page<OrderDTO> searchedOrders = orderService.getOrdersByKey(keyBy, search, pageSearch, recordsInTable);
            model.addAttribute("searchedOrders", searchedOrders);

            // Проверка, является ли поиск по статусу
            boolean isContains = Arrays.stream(OrderStatus.values()).anyMatch(e -> e.name().equals(search));
            model.addAttribute("isStatus", isContains);

            model.addAttribute("search", search);
            model.addAttribute("keyBy", keyBy);
            model.addAttribute("pageSearch", pageSearch);
        } else {
            model.addAttribute("isStatus", false);
        }

        model.addAttribute("pageBasic", pageBasic);
        model.addAttribute("isUpdate", isUpdate);

        return "orders";
    }
}
