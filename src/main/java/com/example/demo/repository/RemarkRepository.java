package com.example.demo.repository;

import com.example.demo.entity.Remark;
import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemarkRepository extends JpaRepository<Remark, Long> {
    List<Remark> findAllByUserId(Item item);

    List<Remark> findAllByItem(Item item);
}
