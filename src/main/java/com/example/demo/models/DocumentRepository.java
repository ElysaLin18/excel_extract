package com.example.demo.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<document,Long> {
    @Query("SELECT new document(d.id, d.name, d.size) FROM document d ORDER BY d.uploadTime DESC")
    List<document> findAll();
}

