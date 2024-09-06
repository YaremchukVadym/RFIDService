package com.example.demo.facade;

import com.example.demo.dto.RemarkDTO;
import com.example.demo.entity.Remark;
import org.springframework.stereotype.Component;

@Component
public class RemarkFacade {
    public RemarkDTO remarkToRemarkDTO(Remark remark) {
        RemarkDTO remarkDTO = new RemarkDTO();
        remarkDTO.setId(remark.getId());
        remarkDTO.setUsername(remark.getUsername());
        remarkDTO.setMessage(remark.getMessage());

        return remarkDTO;

    }

}
