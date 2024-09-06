package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"userId", "status", "updatedDate"})
public class OperationCountUserDTO {
    private Long userId;
    private String status;
    private LocalDateTime updatedDate;
}
