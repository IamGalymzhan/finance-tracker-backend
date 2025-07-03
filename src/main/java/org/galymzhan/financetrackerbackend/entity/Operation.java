package org.galymzhan.financetrackerbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.galymzhan.financetrackerbackend.entity.base.BaseEntityAudit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "operations")
public class Operation extends BaseEntityAudit {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 1, max = 200, message = "Operation name must be between 1 and 200 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private OperationType operationType;

    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "account_in")
    private Account accountIn;

    @ManyToOne
    @JoinColumn(name = "account_out")
    private Account accountOut;

    @Column
    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    private String note;

    @ManyToMany
    @JoinTable(
            name = "operation_tags",
            joinColumns = @JoinColumn(name = "operation_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Size(max = 10, message = "Cannot have more than 10 tags per operation")
    private Set<Tag> tags = new HashSet<>();
}
