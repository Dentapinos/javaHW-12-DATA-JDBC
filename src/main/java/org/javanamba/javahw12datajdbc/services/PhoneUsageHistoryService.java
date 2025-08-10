package org.javanamba.javahw12datajdbc.services;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.dtos.PhoneUsageHistoryDTO;
import org.javanamba.javahw12datajdbc.entity.PhoneUsageHistory;
import org.javanamba.javahw12datajdbc.repository.PhoneUsageHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class PhoneUsageHistoryService {

    private final PhoneUsageHistoryRepository phoneUsageHistoryRepository;

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    public Page<PhoneUsageHistoryDTO> searchPhoneUsageHistory(int page, int size, String key, String search) {
        Pageable pageable = createPageable(page, size);
        if (search == null || search.isEmpty()) {
            return Page.empty();
        }

        Map<String, BiFunction<String, Pageable, Page<PhoneUsageHistoryDTO>>> searchMethods = Map.of(
                "operatorFirstName", phoneUsageHistoryRepository::findByOperatorFirstNameContaining,
                "operatorLastName", phoneUsageHistoryRepository::findByOperatorLastNameContaining,
                "phoneSerial", phoneUsageHistoryRepository::findByPhoneSerialContaining,
                "phoneNumber", phoneUsageHistoryRepository::findByPhoneNumberContaining
        );

        return searchMethods.getOrDefault(key, (s, p) -> Page.empty()).apply(search, pageable);
    }

    public Page<PhoneUsageHistoryDTO> findAll(int page, int size) {
        Pageable pageable = createPageable(page, size);
        return phoneUsageHistoryRepository.findAll(pageable);
    }

    public void saveHistory(Long workPhoneId, Long operatorId, Boolean isNew) {
        if (workPhoneId == null || operatorId == null) {
            throw new IllegalArgumentException("workPhoneId and operatorId must not be null");
        }
        if (Boolean.TRUE.equals(isNew)) {
            PhoneUsageHistory newHistory = new PhoneUsageHistory();
            newHistory.setUsageStart(LocalDateTime.now());
            newHistory.setPhoneId(workPhoneId);
            newHistory.setOperatorId(operatorId);
            phoneUsageHistoryRepository.save(newHistory);
        } else {
            PhoneUsageHistory history = phoneUsageHistoryRepository
                    .findHistoryByOperatorIdAndPhoneId(operatorId, workPhoneId)
                    .orElseThrow(() -> new RuntimeException("История не найдена"));
            history.setUsageEnd(LocalDateTime.now());
            phoneUsageHistoryRepository.update(history);
        }
    }
}