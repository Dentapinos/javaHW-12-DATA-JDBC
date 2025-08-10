package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("orders")
public class Order {

    @Id
    private Long id;

    @Nonnull
    private String description;

    @Nonnull
    private LocalDateTime created;

    @Nullable
    private LocalDateTime closed;

    @Nonnull
    private String status;

    @Nullable
    private String clientsComment;

    @Nullable
    private String operatorsComment;

    @Nonnull
    private Long clientId;

    @Nonnull
    private Long operatorId;

    @PersistenceCreator
    public Order(Long id, @Nonnull String description, @Nonnull LocalDateTime created, @Nullable LocalDateTime closed, @Nonnull String status, @Nullable String clientsComment, @Nullable String operatorsComment, @Nonnull Long clientId, @Nonnull Long operatorId) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.closed = closed;
        this.status = status;
        this.clientsComment = clientsComment;
        this.operatorsComment = operatorsComment;
        this.clientId = clientId;
        this.operatorId = operatorId;
    }

    public Order(@Nonnull String description, @Nonnull LocalDateTime created, @Nullable LocalDateTime closed, @Nonnull String status, @Nullable String clientsComment, @Nullable String operatorsComment, @Nonnull Long clientId, @Nonnull Long operatorId) {
        this.id = null;
        this.description = description;
        this.created = created;
        this.closed = closed;
        this.status = status;
        this.clientsComment = clientsComment;
        this.operatorsComment = operatorsComment;
        this.clientId = clientId;
        this.operatorId = operatorId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDto {
        private Long id;

        @NotBlank(message = "Описание обязательно")
        private String description;

        private String clientsComment, operatorsComment;

        private Long clientId;

        private String clientFirstName, clientLastName;

        private Long operatorId;

        private String operatorFirstName, operatorLastName;
    }
}
