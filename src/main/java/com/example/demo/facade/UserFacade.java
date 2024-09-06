package com.example.demo.facade;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        userDTO.setUsername(user.getUsername());
        userDTO.setCharacteristic(user.getCharacteristic());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRfReaderIdToken(user.getRfReaderIdToken());
        userDTO.setPosition(user.getPosition());
        userDTO.setResponsibility(user.getResponsibility());
        return userDTO;
    }
}