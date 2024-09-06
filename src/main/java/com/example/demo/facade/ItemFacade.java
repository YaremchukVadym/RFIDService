package com.example.demo.facade;

import com.example.demo.dto.ItemDTO;
import com.example.demo.dto.OperationCountUserDTO;
import com.example.demo.entity.Item;
import com.example.demo.entity.OperationCountUser;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ItemFacade {
    public ItemDTO itemToItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setRfTag(item.getRfTag());
        itemDTO.setName(item.getName());
        itemDTO.setType(item.getType());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setStatus(item.getStatus());
        itemDTO.setUsername(item.getUser().getUsername());
        itemDTO.setOperationCount(item.getOperationCount());
        itemDTO.setOperationCountUsers(
                item.getOperationCountUsers().stream().map(this::operationCountUserToDTO).collect(Collectors.toSet())
        );
        return itemDTO;
    }

    private OperationCountUserDTO operationCountUserToDTO(OperationCountUser operationCountUser) {
        OperationCountUserDTO dto = new OperationCountUserDTO();
        dto.setUserId(operationCountUser.getUser().getId());
        dto.setUpdatedDate(operationCountUser.getUpdatedDate());
        dto.setStatus(operationCountUser.getStatus());
        return dto;
    }


}
