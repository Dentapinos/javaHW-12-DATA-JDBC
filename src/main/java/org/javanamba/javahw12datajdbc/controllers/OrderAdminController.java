package org.javanamba.javahw12datajdbc.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Client;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.entity.Order;
import org.javanamba.javahw12datajdbc.enums.OrderStatus;
import org.javanamba.javahw12datajdbc.services.ClientService;
import org.javanamba.javahw12datajdbc.services.OperatorService;
import org.javanamba.javahw12datajdbc.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;
    private final ClientService clientService;
    private final OperatorService operatorService;

    @GetMapping("/add")
    public String showAddOrderForm(Model model) {
        model.addAttribute("orderDto", new Order.OrderDto());

        List<Client> clientsNames = clientService.getAllClientsOnlyNameAndId();
        model.addAttribute("clientsNames", clientsNames);

        List<Operator> operatorsNames = operatorService.getAllWitchIdAndNameAndLastName();
        model.addAttribute("operatorsNames", operatorsNames);

        return "admin/orders/add-order";
    }

    @PostMapping("/add")
    public String addOrder(
            @Valid @ModelAttribute("orderDto") Order.OrderDto orderDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/orders/add-order";
        }

        Order order = new Order(
                orderDto.getDescription(),
                LocalDateTime.now(),
                null,
                OrderStatus.SECONDARY_REVIEW.name(),
                orderDto.getClientsComment(),
                orderDto.getOperatorsComment(),
                orderDto.getClientId(),
                orderDto.getOperatorId()
        );

        orderService.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        Order.OrderDto orderDto = orderService.getOrderDtoById(id);
        model.addAttribute("orderDto", orderDto);
        return "admin/orders/edit-order";
    }

    @PostMapping("/edit")
    public String editOrder(
            @Valid @ModelAttribute("orderDto") Order.OrderDto orderDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/orders/edit-order";
        }

        orderService.saveOrderDto(orderDto);
        return "redirect:/orders?isUpdate=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/orders";
    }
}