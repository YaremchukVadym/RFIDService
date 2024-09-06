package com.example.demo.services;

import com.example.demo.dto.ItemDTO;
import com.example.demo.entity.ImageModel;
import com.example.demo.entity.Item;
import com.example.demo.entity.OperationCountUser;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.ItemUpdateException;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    public static final Logger LOG = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       ImageRepository imageRepository) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Item createItem(ItemDTO itemDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Item item = new Item();
        item.setUser(user);
        item.setName(itemDTO.getName());
        item.setType(itemDTO.getType());
        item.setDescription(itemDTO.getDescription());
        item.setStatus(itemDTO.getStatus());

        OperationCountUser operationCountUser = new OperationCountUser();
        operationCountUser.setUser(user);
        operationCountUser.setItem(item);
        operationCountUser.setStatus(itemDTO.getStatus());
        item.getOperationCountUsers().add(operationCountUser);


        LOG.info("Saving Item for User: {}", user.getEmail());
        return itemRepository.save(item);
    }


    public Item updateItem(ItemDTO itemDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Item item = getLastCreatedItem(user);

        if (item.getRfTag() != null && !item.getRfTag().isEmpty() && itemDTO.getRfTag() != null) {
            throw new ItemUpdateException("RF Tag is already assigned to the item and cannot be updated.");
        }

        item.setRfTag(itemDTO.getRfTag());
        item.setStatus(itemDTO.getStatus());

        OperationCountUser operationCountUser = new OperationCountUser();
        operationCountUser.setUser(user);
        operationCountUser.setItem(item);
        operationCountUser.setStatus(itemDTO.getStatus());
        item.getOperationCountUsers().add(operationCountUser);

        LOG.info("Updating Item with ID: {}", item.getId());
        return itemRepository.save(item);
    }


    public Item updateItemByRfTag(String rfTag, ItemDTO itemDTO, Principal principal) {
        User currentUser = getUserByPrincipal(principal);
        Item item = itemRepository.findByRfTag(rfTag)
                .orElseThrow(() -> new ItemNotFoundException("No item found with rfTag: " + rfTag));

        item.setStatus(itemDTO.getStatus());

        OperationCountUser operationCountUser = new OperationCountUser();
        operationCountUser.setUser(currentUser);
        operationCountUser.setItem(item);
        operationCountUser.setStatus(itemDTO.getStatus());  // Set status field
        item.getOperationCountUsers().add(operationCountUser);

        LOG.info("Updating Item with rfTag: {}", rfTag);
        return itemRepository.save(item);
    }

    public Item getLastCreatedItem(User user) {
        return itemRepository.findAllByUserOrderByCreatedDateDesc(user)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("No items found for user: " + user.getEmail()));
    }

    public List<Item> getAllItemForUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return itemRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAllByOrderByCreatedDateDesc();
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    public Item getItemById(Long itemId, Principal principal) {
        User user = getUserByPrincipal(principal);
        return itemRepository.findItemByIdAndUser(itemId, user)
                .orElseThrow(() -> new ItemNotFoundException("Item cannot be found for username: " + user.getEmail()));
    }


    public void deleteItem(Long itemId, Principal principal) {
        Item item = getItemById(itemId, principal);
        Optional<ImageModel> imageModel = imageRepository.findByItemId(item.getId());
        itemRepository.delete(item);
        imageModel.ifPresent(imageRepository::delete);

    }


}
