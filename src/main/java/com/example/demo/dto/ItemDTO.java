package com.example.demo.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ItemDTO {
    private Long id;
    private String rfTag;
    private String name;
    private String type;
    private String description;
    private String status;
    private String username;
    private Integer operationCount;
    private Set<OperationCountUserDTO> operationCountUsers;
}
