package com.example.demo.models;

import jakarta.persistence.*;
import java. util. Date;

@Entity
@Table(name="document")
public class document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 512, nullable = false, unique = true)
    private String name;
    private long size;
    @Column(name = "upload_time")
    private Date uploadTime;
    
    private byte[] content;

    
    public document() {
    }
    public document(long id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public Date getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
    
}
