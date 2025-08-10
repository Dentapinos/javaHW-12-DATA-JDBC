package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin-panel")
@RequiredArgsConstructor
public class AdminController {

    /* admin-panel */
    @GetMapping
    public String adminDashboard() {
        return "admin/admin-panel";
    }
}