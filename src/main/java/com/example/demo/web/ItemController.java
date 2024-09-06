package com.example.demo.web;

import com.example.demo.dto.ItemDTO;
import com.example.demo.entity.Item;
import com.example.demo.facade.ItemFacade;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.ItemService;
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
@RequestMapping("api/item")
@CrossOrigin
public class ItemController {

    @Autowired
    private ItemFacade itemFacade;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDTO itemDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Item item = itemService.createItem(itemDTO, principal);
        ItemDTO createdItem = itemFacade.itemToItemDTO(item);

        return new ResponseEntity<>(createdItem, HttpStatus.OK);
    }


    @PostMapping("/update")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemDTO itemDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Item item = itemService.updateItem(itemDTO, principal);
        ItemDTO updatedItem = itemFacade.itemToItemDTO(item);

        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @PostMapping("/{rfTag}/update")
    public ResponseEntity<Object> updateItemByRfTag(@PathVariable String rfTag,
                                                    @Valid @RequestBody ItemDTO itemDTO,
                                                    BindingResult bindingResult,
                                                    Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Item item = itemService.updateItemByRfTag(rfTag, itemDTO, principal);
        ItemDTO updatedItem = itemFacade.itemToItemDTO(item);

        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> itemDTOList = itemService.getAllItems()
                .stream()
                .map(itemFacade::itemToItemDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(itemDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/items")
    public ResponseEntity<List<ItemDTO>> getAllItemsForUser(Principal principal) {
        List<ItemDTO> itemDTOList = itemService.getAllItemForUser(principal)
                .stream()
                .map(itemFacade::itemToItemDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(itemDTOList, HttpStatus.OK);
    }

    @PostMapping("/{itemId}/delete")
    public ResponseEntity<MessageResponse> deleteItem(@PathVariable("itemId") String itemId, Principal principal) {
        itemService.deleteItem(Long.parseLong(itemId), principal);
        return new ResponseEntity<>(new MessageResponse("Item was deleted"), HttpStatus.OK);
    }
}