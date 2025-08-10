package org.javanamba.javahw12datajdbc.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.WorkPhone;
import org.javanamba.javahw12datajdbc.services.WorkPhoneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/work-phones")
@RequiredArgsConstructor
public class WorkPhoneAdminController {

    private final WorkPhoneService workPhoneService;

    @GetMapping("/add")
    public String showAddWorkPhoneForm(Model model) {
        model.addAttribute("workPhoneDto", new WorkPhone.WorkPhoneDto());
        return "admin/phones/add-work-phone";
    }

    @PostMapping("/add")
    public String addWorkPhone(
            @Valid @ModelAttribute("workPhoneDto") WorkPhone.WorkPhoneDto workPhoneDto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/phones/add-work-phone";
        }

        WorkPhone workPhone = mapToWorkPhone(workPhoneDto);
        workPhoneService.save(workPhone);
        return "redirect:/work-phones?isUpdate=true";
    }

    private WorkPhone mapToWorkPhone(WorkPhone.WorkPhoneDto workPhoneDto) {
        return new WorkPhone(
                workPhoneDto.getPhoneNumber(),
                workPhoneDto.getPhoneType(),
                workPhoneDto.getSerialNumber(),
                null
        );
    }

    @GetMapping("/edit/{id}")
    public String showEditWorkPhoneForm(@PathVariable Long id, Model model) {
        WorkPhone workPhone = workPhoneService.getWorkPhoneById(id);
        WorkPhone.WorkPhoneDto workPhoneDto = mapToWorkPhoneDto(workPhone);

        model.addAttribute("workPhoneDto", workPhoneDto);
        return "admin/phones/edit-work-phone";
    }

    private WorkPhone.WorkPhoneDto mapToWorkPhoneDto(WorkPhone workPhone) {
        return new WorkPhone.WorkPhoneDto(
                workPhone.getId(),
                workPhone.getPhoneNumber(),
                workPhone.getPhoneType(),
                workPhone.getSerialNumber()
        );
    }

    @PostMapping("/edit")
    public String editWorkPhone(
            @Valid @ModelAttribute("workPhoneDto") WorkPhone.WorkPhoneDto workPhoneDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/phones/edit-work-phone";
        }

        workPhoneService.saveWorkPhoneDto(workPhoneDto);
        return "redirect:/work-phones?isUpdate=true";
    }
}