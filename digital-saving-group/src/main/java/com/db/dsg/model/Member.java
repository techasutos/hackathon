package com.db.dsg.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String aadhaar;
    private boolean ekycVerified;
    private LocalDate dateOfBirth;
    private boolean approved = false;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MemberUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Group group;

    @Transient
    public String getDisplayRole() {
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles().iterator().next().getName();
        }
        return "MEMBER";
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
