package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("clients")
public class Client {

    @Id
    private Long id;

    @Nonnull
    private String firstName;

    @Nonnull
    private String lastName;

    @Nonnull
    private String patronymic;

    @Nonnull
    private Integer age;

    @Nonnull
    private String phoneNumber;

    @Nonnull
    private String city;

    @PersistenceCreator
    public Client(Long id, @Nonnull String firstName, @Nonnull String lastName, @Nonnull String patronymic, @Nonnull Integer age, @Nonnull String phoneNumber, @Nonnull String city) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    public Client(@Nonnull String firstName, @Nonnull String lastName, @Nonnull String patronymic, @Nonnull Integer age, @Nonnull String phoneNumber, @Nonnull String city) {
        this.id = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientDto {
        private Long id;

        @NotBlank(message = "Имя обязательно")
        private String firstName;

        @NotBlank(message = "Фамилия обязательно")
        private String lastName;

        private String patronymic;

        @NotBlank(message = "Телефон обязателен")
        @Pattern(regexp = "^(\\+7|8)-?\\d{3}-?\\d{3}-?\\d{2}-?\\d{2}$", message = "Номер телефона недействителен, должно быть от 12 до 16 цифр и начинаться с +7")
        private String phoneNumber;

        private int age;

        @NotBlank(message = "Адрес является обязательным")
        private String city;
    }
}
