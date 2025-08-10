package org.javanamba.javahw12datajdbc.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("phone_usage_history")
@NoArgsConstructor
public class PhoneUsageHistory {

    @Id
    private Long id;

    @Nonnull
    private LocalDateTime usageStart;

    @Nullable
    private LocalDateTime usageEnd;

    @Nonnull
    private Long operatorId;

    @Nonnull
    private Long phoneId;

    @PersistenceCreator
    public PhoneUsageHistory(Long id, @Nonnull LocalDateTime usageStart, @Nullable LocalDateTime usageEnd, @Nonnull Long operatorId, @Nonnull Long phoneId) {
        this.id = id;
        this.usageStart = usageStart;
        this.usageEnd = usageEnd;
        this.operatorId = operatorId;
        this.phoneId = phoneId;
    }

    public PhoneUsageHistory(@Nonnull LocalDateTime usageStart, @Nullable LocalDateTime usageEnd, @Nonnull Long operatorId, @Nonnull Long phoneId) {
        this.id = null;
        this.usageStart = usageStart;
        this.usageEnd = usageEnd;
        this.operatorId = operatorId;
        this.phoneId = phoneId;
    }
}
