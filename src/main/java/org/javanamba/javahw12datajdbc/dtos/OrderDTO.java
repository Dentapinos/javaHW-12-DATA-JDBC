package org.javanamba.javahw12datajdbc.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javanamba.javahw12datajdbc.enums.OrderStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String description;
    private String operatorFirstName;
    private String operatorLastName;
    private Long operatorId;
    private String clientFirstName;
    private String clientLastName;
    private Long clientId;
    private OrderStatus status;
    private LocalDateTime created;
    private LocalDateTime closed;
    private String operatorsComment;
    private String clientsComment;

}
