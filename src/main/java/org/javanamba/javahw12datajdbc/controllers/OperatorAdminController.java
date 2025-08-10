package org.javanamba.javahw12datajdbc.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.javanamba.javahw12datajdbc.services.OperatorService;
import org.javanamba.javahw12datajdbc.services.OrderService;
import org.javanamba.javahw12datajdbc.services.PhoneUsageHistoryService;
import org.javanamba.javahw12datajdbc.services.WorkPhoneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/operators")
@RequiredArgsConstructor
public class OperatorAdminController {

    private final OperatorService operatorService;
    private final WorkPhoneService workPhoneService;
    private final PhoneUsageHistoryService phoneUsageHistoryService;
    private final OrderService orderService;

    @GetMapping("/add")
    public String showAddOperatorForm(Model model) {
        model.addAttribute("operatorDto", new Operator.OperatorDto());
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("phones", workPhoneService.getAllFreeWorkPhones());
        return "admin/operators/add-operator";
    }

    @PostMapping("/add")
    public String addOperator(
            @RequestParam(value = "selectPhoneId", defaultValue = "0") Long selectPhoneId,
            @Valid @ModelAttribute("operatorDto") Operator.OperatorDto operatorDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentDate", LocalDate.now());
            model.addAttribute("phones", workPhoneService.getAllFreeWorkPhones());
            return "admin/operators/add-operator";
        }

        Operator operator = operatorService.saveOperatorDto(operatorDto);

        if (selectPhoneId > 0) {
            linkPhoneAndSaveHistory(selectPhoneId, operator.getId());
        }
        return "redirect:/operators";
    }

    @GetMapping("/edit/{id}")
    public String showEditOperatorForm(@PathVariable Long id, Model model) {
        Operator operator = operatorService.getOperatorById(id);
        Operator.OperatorDto operatorDto = mapFromOperator(operator);
        model.addAttribute("operatorDto", operatorDto);

        Optional<WorkPhone> workPhone = workPhoneService.getWorkPhoneByOperatorId(id);
        model.addAttribute("activePhoneId", workPhone.isPresent() ? workPhone.get().getId() : 0);
        model.addAttribute("activePhoneSeries", workPhone.map(WorkPhone::getSerialNumber).orElse(""));

        model.addAttribute("phones", workPhoneService.getAllFreeWorkPhones());
        return "admin/operators/edit-operator";
    }

    @PostMapping("/edit")
    public String editOperator(
            @RequestParam(value = "selectedPhoneId", defaultValue = "0") Long selectedPhoneId,
            @RequestParam(value = "oldPhoneId", defaultValue = "0") Long oldPhoneId,
            @Valid @ModelAttribute("operatorDto") Operator.OperatorDto operatorDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            Optional<WorkPhone> workPhone = workPhoneService.getWorkPhoneByOperatorId(operatorDto.getId());
            model.addAttribute("activePhoneId", workPhone.isPresent() ? workPhone.get().getId() : 0);
            model.addAttribute("activePhoneSeries", workPhone.map(WorkPhone::getSerialNumber).orElse(""));
            model.addAttribute("phones", workPhoneService.getAllFreeWorkPhones());
            return "admin/operators/edit-operator";
        }

        operatorService.updateOperatorDto(operatorDto);

        if (oldPhoneId == 0 && selectedPhoneId > 0) {
            linkPhoneAndSaveHistory(selectedPhoneId, operatorDto.getId());
        } else if (selectedPhoneId == 0 && oldPhoneId > 0) {
            unlinkPhoneAndSaveHistory(oldPhoneId, operatorDto.getId());
        } else if (selectedPhoneId > 0 && oldPhoneId > 0 && !selectedPhoneId.equals(oldPhoneId)) {
            unlinkPhoneAndSaveHistory(oldPhoneId, operatorDto.getId());
            linkPhoneAndSaveHistory(selectedPhoneId, operatorDto.getId());
        }

        return "redirect:/operators";
    }

    @GetMapping("/delete/{id}")
    public String deleteOperator(@PathVariable Long id) {
        Operator operator = operatorService.deleteOperatorById(id);
        Optional<WorkPhone> workPhone = workPhoneService.findWorkPhoneByOperatorId(id);
        workPhone.ifPresent(phone -> unlinkPhoneAndSaveHistory(phone.getId(), operator.getId()));
        orderService.deleteAllOrdersByOperatorId(operator.getId());
        return "redirect:/operators";
    }

    private void unlinkPhoneAndSaveHistory(Long workPhoneId, Long operatorId) {
        workPhoneService.unlinkOperatorFromPhoneById(workPhoneId);
        phoneUsageHistoryService.saveHistory(workPhoneId, operatorId, false);
    }

    private void linkPhoneAndSaveHistory(Long workPhoneId, Long operatorId) {
        workPhoneService.linkOperatorToPhoneById(workPhoneId, operatorId);
        phoneUsageHistoryService.saveHistory(workPhoneId, operatorId, true);
    }

    private Operator.OperatorDto mapFromOperator(Operator operator) {
        return new Operator.OperatorDto(
                operator.getId(),
                operator.getFirstName(),
                operator.getLastName(),
                operator.getDateOfBirth(),
                operator.getEmail(),
                operator.getPhoneNumber(),
                operator.getAddress()
        );
    }
}