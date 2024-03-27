package com.example.demo.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Document2Repository extends JpaRepository<document2,Long> {
    
    @Query("SELECT new document2(d.id, d.name, d.size) FROM document2 d ORDER BY d.uploadTime DESC")
    List<document2> findAll();
}
