package com.db.dsg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue
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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private MemberUser user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Optional derived role name for display only
    @Transient
    public String getDisplayRole() {
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles().iterator().next().getName(); // or prioritize by logic
        }
        return "MEMBER";
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
