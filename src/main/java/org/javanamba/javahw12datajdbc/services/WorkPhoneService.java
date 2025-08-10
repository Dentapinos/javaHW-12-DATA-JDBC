package org.javanamba.javahw12datajdbc.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.javanamba.javahw12datajdbc.exception.EntityNotFoundException;
import org.javanamba.javahw12datajdbc.repository.WorkPhoneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkPhoneService {

    private final WorkPhoneRepository workPhoneRepository;

    public Page<WorkPhone> searchWorkPhones(int page, int size,String key, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            if (key.equals("phoneNumber")){
                return workPhoneRepository.findByPhoneNumberContaining(search, pageable);
            } else if (key.equals("serialNumber")) {
                return workPhoneRepository.findBySerialNumberContaining(search, pageable);
            }
        }
        return null;
    }

    public Page<WorkPhone> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return workPhoneRepository.findAll(pageable);
    }

    public List<WorkPhone> getAllFreeWorkPhones() {
        return workPhoneRepository.findFree();
    }

    public WorkPhone getWorkPhoneById(Long id) {
        return workPhoneRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("WorkPhone not found"));
    }

    public WorkPhone createWorkPhone(WorkPhone workPhone) {
        return workPhoneRepository.save(workPhone);
    }

    public WorkPhone updateWorkPhone(Long id, WorkPhone workPhoneDetails) {
        WorkPhone workPhone = workPhoneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("WorkPhone not found"));
        workPhone.setPhoneNumber(workPhoneDetails.getPhoneNumber());
        workPhone.setPhoneType(workPhoneDetails.getPhoneType());
        workPhone.setSerialNumber(workPhoneDetails.getSerialNumber());
        workPhone.setOperatorId(workPhoneDetails.getOperatorId());
        return workPhoneRepository.save(workPhone);
    }

    public void deleteWorkPhone(Long id) {
        workPhoneRepository.deleteById(id);
    }

    public void save(WorkPhone workPhone) {
        workPhoneRepository.save(workPhone);
    }

    public Optional<WorkPhone> findWorkPhoneByOperatorId(Long operatorId) {
        return workPhoneRepository.findByOperatorId(operatorId);
    }

    public void unlinkOperatorFromPhoneById(Long workPhoneId) {
        WorkPhone workPhone = workPhoneRepository.findById(workPhoneId).orElseThrow(() -> new EntityNotFoundException("Не удалось найти телефон по ID:" + workPhoneId));
        workPhone.setOperatorId(null);
        workPhoneRepository.save(workPhone);
    }

    public void linkOperatorToPhoneById(Long workPhoneId, Long operatorId) {
        WorkPhone workPhone = workPhoneRepository.findById(workPhoneId).orElseThrow(() -> new EntityNotFoundException("Не удалось найти телефон по ID:" + workPhoneId));
        workPhone.setOperatorId(operatorId);
        workPhoneRepository.save(workPhone);
    }

    public Optional<WorkPhone> getWorkPhoneByOperatorId(Long operatorId) {
        return workPhoneRepository.findByOperatorId(operatorId);
    }

    public void saveWorkPhoneDto(WorkPhone.@Valid WorkPhoneDto workPhoneDto) {
        WorkPhone workPhone = workPhoneRepository.findById(workPhoneDto.getId()).orElseThrow(() -> new EntityNotFoundException("Не удалось найти телефон по ID:" + workPhoneDto.getId()));
        workPhone.setPhoneNumber(workPhoneDto.getPhoneNumber());
        workPhone.setPhoneType(workPhoneDto.getPhoneType());
        workPhone.setSerialNumber(workPhoneDto.getSerialNumber());
        workPhoneRepository.save(workPhone);
    }
}