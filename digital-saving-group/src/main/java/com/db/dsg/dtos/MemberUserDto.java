package com.db.dsg.dtos;

import com.db.dsg.model.MemberUser;
import com.db.dsg.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUserDto {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private Long groupId;
    private Set<String> roles;
    private Set<String> permissions;

    public static MemberUserDto from(MemberUser user) {
        return MemberUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getMember() != null ? user.getMember().getName() : null)
                .phone(user.getMember() != null ? user.getMember().getPhone() : null)
                .email(user.getMember() != null ? user.getMember().getEmail() : null)
                .groupId(user.getGroup() != null ? user.getGroup().getId() : null)
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .permissions(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }
}
