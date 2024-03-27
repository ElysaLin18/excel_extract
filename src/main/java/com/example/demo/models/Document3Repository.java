package com.example.demo.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Document3Repository extends JpaRepository<document3,Long> {
    @Query("SELECT new document3(d.id, d.name, d.size) FROM document3 d ORDER BY d.uploadTime DESC")
    List<document3> findAll();
}