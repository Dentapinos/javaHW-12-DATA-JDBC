package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("work_phones")
public class WorkPhone {

    @Id
    private Long id;

    @Nonnull
    private String phoneNumber;

    @Nonnull
    private String phoneType;

    @Nonnull
    private String serialNumber;

    @Nullable
    private Long operatorId;

    @PersistenceCreator
    public WorkPhone(Long id, @Nonnull String phoneNumber, @Nonnull String phoneType, @Nonnull String serialNumber, @Nullable Long operatorId) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.serialNumber = serialNumber;
        this.operatorId = operatorId;
    }

    public WorkPhone(@Nonnull String phoneNumber, @Nonnull String phoneType, @Nonnull String serialNumber, @Nullable Long operatorId) {
        this.id = null;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
        this.serialNumber = serialNumber;
        this.operatorId = operatorId;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkPhoneDto {

        private Long id;

        @NotBlank(message = "Номер телефона обязателен")
        @Pattern(regexp = "^(\\+7|8)-?\\d{3}-?\\d{3}-?\\d{2}-?\\d{2}$", message = "Номер телефона недействителен: +7-000-000-00-00")
        private String phoneNumber;

        @NotBlank(message = "Тип обязателен")
        @Size(max = 30, message = "Максимальная длинна 30 символов")
        private String phoneType;

        @NotNull(message = "Не может быть пустым")
        @Size(min = 6, message = "Минимум 6 символов")
        private String serialNumber;
    }

}
