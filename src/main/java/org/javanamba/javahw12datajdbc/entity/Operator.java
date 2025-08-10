package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Table("operators")
public class Operator {

    @Id
    private Long id;

    @Nonnull
    private String firstName;

    @Nonnull
    private String lastName;

    @Nonnull
    private LocalDate dateOfBirth;

    @Nonnull
    private String email;

    @Nonnull
    private String phoneNumber;

    @Nonnull
    private String address;

    @Nonnull
    private Boolean status;

    @MappedCollection(idColumn = "operator_id", keyColumn = "list_index")
    private List<Task> tasks = new ArrayList<>();

    @PersistenceCreator
    public Operator(Long id, @Nonnull String firstName, @Nonnull String lastName, @Nonnull LocalDate dateOfBirth, @Nonnull String email, @Nonnull String phoneNumber, @Nonnull String address, @Nonnull Boolean status, List<Task> tasks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.tasks = tasks;
    }

    public Operator(@Nonnull String firstName, @Nonnull String lastName, @Nonnull LocalDate dateOfBirth, @Nonnull String email, @Nonnull String phoneNumber, @Nonnull String address, @Nonnull Boolean status, List<Task> tasks) {
        this.id = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.tasks = tasks;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OperatorDto {

        private Long id;

        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 250, message = "Имя должно содержать от 2 до 250 символов")
        private String firstName;

        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(min = 2, max = 250, message = "Фамилия должна содержать от 2 до 250 символов")
        private String lastName;

        @NotNull(message = "Дата рождения не может быть пустой")
        @Past(message = "Дата рождения должна быть меньше текущей")
        private LocalDate dateOfBirth;

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Email должен быть корректным")
        private String email;

        @NotBlank(message = "Телефон не может быть пустым")
        @Pattern(regexp = "^(\\+7|8)-?\\d{3}-?\\d{3}-?\\d{2}-?\\d{2}$", message = "Номер телефона недействителен, должно быть от 12 до 16 цифр и начинаться с +7")
        private String phone;

        @NotBlank(message = "Адрес не может быть пустым")
        private String address;

    }

}