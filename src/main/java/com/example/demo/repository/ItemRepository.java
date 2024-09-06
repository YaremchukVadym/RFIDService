package com.example.demo.repository;

import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUserOrderByCreatedDateDesc(User user);

    List<Item> findAllByOrderByCreatedDateDesc();

    Optional<Item> findItemByIdAndUser(Long id, User user);

    Optional<Item> findByRfTag(String rfTag);

}
