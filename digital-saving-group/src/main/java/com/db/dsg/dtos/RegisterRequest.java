package com.db.dsg.dtos;

import com.db.dsg.model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String roleName;
    private String fullName;
    private String phone;
    private String email;
    private Member.Gender gender;
    private String aadhaar;
    private boolean ekycVerified;
    private LocalDate dateOfBirth;
    private Long groupId;
}
