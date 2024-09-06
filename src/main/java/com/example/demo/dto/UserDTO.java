package com.example.demo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String characteristic;
    private String phoneNumber;
    private String rfReaderIdToken;
    private String position;
    private String responsibility;
}