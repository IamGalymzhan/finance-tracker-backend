package org.galymzhan.financetrackerbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.galymzhan.financetrackerbackend.entity.base.BaseEntityAudit;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "name"})
})
public class Account extends BaseEntityAudit {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AccountType accountType;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal balance;

    @Column(nullable = false)
    @NotBlank
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    private String color;

    @Column
    @Size(max = 50, message = "Icon name cannot exceed 50 characters")
    private String icon;
}
