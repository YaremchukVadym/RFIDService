package com.example.demo.web;

import com.example.demo.dto.RemarkDTO;
import com.example.demo.entity.Remark;
import com.example.demo.facade.RemarkFacade;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.RemarkService;
import com.example.demo.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/remark")
@CrossOrigin
public class RemarkController {

    @Autowired
    private RemarkService remarkService;
    @Autowired
    private RemarkFacade remarkFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{itemId}/create")
    public ResponseEntity<Object> createRemark(@Valid @RequestBody RemarkDTO remarkDTO,
                                               @PathVariable("itemId") String itemId,
                                               BindingResult bindingResult,
                                               Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Remark remark = remarkService.saveRemark(Long.parseLong(itemId), remarkDTO, principal);
        RemarkDTO createRemark = remarkFacade.remarkToRemarkDTO(remark);

        return new ResponseEntity<>(createRemark, HttpStatus.OK);
    }

    @GetMapping("/{itemId}/all")
    public ResponseEntity<List<RemarkDTO>> getAllRemarksToItem(@PathVariable("itemId") String itemId) {
        List<RemarkDTO> remarkDTOList = remarkService.getAllRemarksForItem(Long.parseLong(itemId))
                .stream()
                .map(remarkFacade::remarkToRemarkDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(remarkDTOList, HttpStatus.OK);
    }


    @PostMapping("/{remarkId}/delete")
    public ResponseEntity<MessageResponse> deleteRemark(@PathVariable("remarkId") String remarkId) {
        remarkService.deleteRemark(Long.parseLong(remarkId));
        return new ResponseEntity<>(new MessageResponse("Remark was deleted"), HttpStatus.OK);

    }

}
