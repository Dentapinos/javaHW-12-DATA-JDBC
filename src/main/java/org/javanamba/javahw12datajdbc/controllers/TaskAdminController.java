package org.javanamba.javahw12datajdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.javanamba.javahw12datajdbc.entity.Operator;
import org.javanamba.javahw12datajdbc.entity.Task;
import org.javanamba.javahw12datajdbc.services.OperatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
public class TaskAdminController {

    private final OperatorService operatorService;

    //добавление задачи
    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        List<Operator> operators = operatorService.getAllWitchIdAndNameAndLastName();
        model.addAttribute("operators", operators);
        return "admin/tasks/add-task";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam Long operatorId, @RequestParam String taskText) {
        Operator operator = operatorService.getOperatorById(operatorId);
        Task task = new Task();
        task.setTaskDetails(taskText);
        task.setIsCompleted(false);
        operator.getTasks().add(task);
        operatorService.saveOperator(operator);
        return "redirect:/tasks";
    }


    //изменение задачи
    @GetMapping("/edit/{id}")
    public String showEditTaskForm(
            @PathVariable Long id,
            @RequestParam Long operatorId,
            Model model) {
        Operator operator = operatorService.getOperatorById(operatorId);
        operator.getTasks().forEach(task -> {
           if (task.getId().equals(id)) model.addAttribute("task", task);
        });
        model.addAttribute("operator", operator);
        return "admin/tasks/edit-task";
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Long id, @RequestParam Long operatorId, @RequestParam String taskDetails) {
        Operator operator = operatorService.getOperatorById(operatorId);
        operator.getTasks().forEach(task -> {
            if (task.getId().equals(id)) task.setTaskDetails(taskDetails);
        });
        operatorService.saveOperator(operator);
        return "redirect:/tasks?isUpdate=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, @RequestParam Long operatorId) {
        Operator operator = operatorService.getOperatorById(operatorId);
        List<Task> tasks = operator.getTasks();
        List<Task> newTasks = tasks.stream().filter(task -> !task.getId().equals(id)).toList();
        operator.setTasks(newTasks);
        operatorService.saveOperator(operator);
        return "redirect:/tasks?isUpdate=true";
    }


}
