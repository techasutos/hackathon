package com.db.dsg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // e.g. "loan:create", "meeting:close"

    private String description;

    private String category; // Optional: LOAN, SAVINGS, GOVERNANCE

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private Set<Role> roles;
}
