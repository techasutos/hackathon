package com.db.dsg.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class CreateRoleRequest {
    private String name;
    private String description;
    private Set<String> permissionNames;
}
