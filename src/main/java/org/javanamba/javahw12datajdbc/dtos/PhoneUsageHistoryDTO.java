package org.javanamba.javahw12datajdbc.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneUsageHistoryDTO {

    private Long id;
    private String operatorFirstName;
    private String operatorLastName;
    private String phoneSerial;
    private String phoneNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
