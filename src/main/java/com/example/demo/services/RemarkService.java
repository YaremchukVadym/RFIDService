package com.example.demo.services;

import com.example.demo.dto.RemarkDTO;
import com.example.demo.entity.Remark;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.repository.RemarkRepository;
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
public class RemarkService {
    public static final Logger LOG = LoggerFactory.getLogger(RemarkService.class);
    private final RemarkRepository remarkRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired

    public RemarkService(RemarkRepository remarkRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.remarkRepository = remarkRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Remark saveRemark(Long ItemId, RemarkDTO remarkDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Item item = itemRepository.findById(ItemId)
                .orElseThrow(() -> new ItemNotFoundException("Item cannot be found for username: " + user.getEmail()));

        Remark remark = new Remark();
        remark.setItem(item);
        remark.setUserId(user.getId());
        remark.setUsername(user.getUsername());
        remark.setMessage(remarkDTO.getMessage());

        LOG.info("Saving remark for Item: {}", item.getId());
        return remarkRepository.save(remark);
    }

    public List<Remark> getAllRemarksForItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item cannot be found"));
        List<Remark> remarks = remarkRepository.findAllByItem(item);

        return remarks;
    }

    public void deleteRemark(Long remarkId) {
        Optional<Remark> remark = remarkRepository.findById(remarkId);
        remark.ifPresent(remarkRepository::delete);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }
}

