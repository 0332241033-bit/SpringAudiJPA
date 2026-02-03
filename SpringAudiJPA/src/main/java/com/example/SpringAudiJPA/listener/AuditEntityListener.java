package com.example.SpringAudiJPA.listener;

import com.example.SpringAudiJPA.entity.history;
import com.example.SpringAudiJPA.entity.product;
import com.example.SpringAudiJPA.repository.historyRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
@Component

public class AuditEntityListener {
    private final historyRepository  historyRepository;




    @PrePersist
    private void prePersist(product product){



        history history = new history();
        history.setName(product.getName());
        history.setDate(LocalDateTime.now());
        history.setOperation("INSERT");

        this.historyRepository.save(history);
    }
    @PreUpdate
    private void preUpdate( product product){

        history history = new history();
        history.setName(product.getName());
        history.setDate(LocalDateTime.now());
        history.setOperation("UPDATE");

        this.historyRepository.save(history);
    }
    @PreRemove
    private void preRemove(product product){

        history history = new history();
        history.setName(product.getName());
        history.setDate(LocalDateTime.now());
        history.setOperation("DELETE");

        this.historyRepository.save(history);
    }



}
