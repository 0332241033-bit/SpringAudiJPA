package com.example.SpringAudiJPA.repository;

import com.example.SpringAudiJPA.entity.history;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface historyRepository extends CrudRepository<history,Integer> {
}
