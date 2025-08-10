package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("tasks")
public class Task {

    @Id
    private Long id;

    @Nonnull
    private String taskDetails;

    @Nonnull
    private Boolean isCompleted;

    @PersistenceCreator
    public Task(Long id, @Nonnull String taskDetails, @Nonnull Boolean isCompleted) {
        this.id = id;
        this.taskDetails = taskDetails;
        this.isCompleted = isCompleted;
    }

    public Task(@Nonnull String taskDetails, @Nonnull Boolean isCompleted) {
        this.id = null;
        this.taskDetails = taskDetails;
        this.isCompleted = isCompleted;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskDto {
        private Long id;
        @NotBlank(message = "Описание не должно быть пустым")
        @Size(max = 250, message = "Максимальная длинна задачи 250 символов")
        private String taskDetails;
        private Boolean isCompleted;
        @NotNull(message = "Обязательно нужно назначить оператора")
        private Long operatorId;
    }
}