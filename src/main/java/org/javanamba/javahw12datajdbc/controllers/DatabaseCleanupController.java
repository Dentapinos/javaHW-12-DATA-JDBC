package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.services.DatabaseCleanupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DatabaseCleanupController {

    private final DatabaseCleanupService databaseCleanupService;

    @GetMapping("/page")
    public String showAdmin(@RequestParam(defaultValue = "") String message,
            @RequestParam(defaultValue = "") String error, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "admin/admin";
    }

    @GetMapping("/clear-database")
    public String clearDatabase(RedirectAttributes redirectAttributes) {
        try {
            databaseCleanupService.clearDatabase();
            redirectAttributes.addAttribute("message", "База данных очищена успешно");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Ошибка очистки базы данных: " + e.getMessage());
        }
        return "redirect:/admin/page";
    }
}
