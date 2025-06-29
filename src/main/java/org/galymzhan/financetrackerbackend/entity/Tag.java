package org.galymzhan.financetrackerbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.galymzhan.financetrackerbackend.entity.base.BaseEntityAudit;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tags")
public class Tag extends BaseEntityAudit {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 1, max = 50, message = "Tag name must be between 1 and 50 characters")
    private String name;

    @Column(nullable = false)
    @NotBlank
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    private String color;
}
