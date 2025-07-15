package com.db.dsg.dtos;

import com.db.dsg.model.Member;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {
    private String name;
    private String phone;
    private String email;
    private Member.Gender gender;
    private String aadhaar;
    private boolean ekycVerified;
    private LocalDate dateOfBirth;
}
