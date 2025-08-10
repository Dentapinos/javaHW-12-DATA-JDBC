package org.javanamba.javahw12datajdbc.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.OrderDTO;
import org.javanamba.javahw12datajdbc.entity.Order;
import org.javanamba.javahw12datajdbc.repository.CustomOrderRepository;
import org.javanamba.javahw12datajdbc.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomOrderRepository customOrderRepository;


    public Page<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customOrderRepository.findAll(pageable);
    }

    public void deleteAllOrdersByOperatorId(Long operatorId) {
        orderRepository.deleteAllByOperatorId(operatorId);
    }

    public Page<OrderDTO> getOrdersByKey(String keyBy, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customOrderRepository.findOrdersByKey(keyBy, search, pageable);
    }

    public void save(Order orders) {
        orderRepository.save(orders);
    }

    public Order.OrderDto getOrderDtoById(Long id) {
        return orderRepository.findDtoById(id).orElseThrow(() -> new RuntimeException("Invalid order id"));
    }

    public void saveOrderDto(Order.@Valid OrderDto orderDto) {
        Order order = orderRepository.findById(orderDto.getId()).orElseThrow(() -> new RuntimeException("Invalid order id"));
        order.setDescription(orderDto.getDescription());
        order.setCreated(LocalDateTime.now());
        order.setClientsComment(orderDto.getClientsComment());
        order.setOperatorsComment(orderDto.getOperatorsComment());
        orderRepository.save(order);
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}
