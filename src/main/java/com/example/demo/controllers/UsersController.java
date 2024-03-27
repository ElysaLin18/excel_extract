package com.example.demo.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import com.example.demo.controllers.genFile;

import com.example.demo.models.document;
import com.example.demo.models.document2;
import com.example.demo.models.document3;
import com.example.demo.models.DocumentRepository;
import com.example.demo.models.Document2Repository;
import com.example.demo.models.Document3Repository;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UsersController {  
    
    @Autowired
    private DocumentRepository repo;
    @Autowired
    private Document2Repository repo2;
    @Autowired
    private Document3Repository repo3;

    @GetMapping("view")
    public String getAllFiles(Model model) {
        List<document> listDocs = repo.findAll();
        model.addAttribute("listDocs", listDocs);
        return "showAll";
    }
    @GetMapping("view2")
    public String getAllFiles2(Model model) {
        System.out.println("pass");
        List<document2> listDocs = repo2.findAll();
        model.addAttribute("listDocs", listDocs);
        return "showAll2";
    }
    @GetMapping("view3")
    public String getAllFiles3(Model model) {
        System.out.println("pass");
        List<document3> listDocs = repo3.findAll();
        model.addAttribute("listDocs", listDocs);
        return "showAll3";
    }
    @GetMapping("chooseView")
    public String goToChoose(Model model) {
        return "choose";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile,
                             @RequestParam("document2") MultipartFile multipartFile2,
                             RedirectAttributes ra) throws IOException {
        
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        document doc = new document();
        doc.setName(fileName);
        doc.setContent(multipartFile.getBytes());
        doc.setSize(multipartFile.getSize());
        doc.setUploadTime(new Date());
    
        String fileName2 = StringUtils.cleanPath(multipartFile2.getOriginalFilename());
        document2 doc2 = new document2();
        doc2.setName(fileName2);
        doc2.setContent(multipartFile2.getBytes());
        doc2.setSize(multipartFile2.getSize());
        doc2.setUploadTime(new Date());
    
        genFile generator = new genFile();
    
        ByteArrayOutputStream content3 = generator.generateFile(multipartFile.getBytes(), multipartFile2.getBytes());
    
        char temp = fileName.charAt(26);
        String fileName3 = "output" + fileName.substring(20, 26);
        if (temp != '-') {
            fileName3 += temp;
        }
        fileName3 += ".xlsx";
    
        document3 doc3 = new document3();
        doc3.setName(fileName3);
        doc3.setContent(content3.toByteArray());
        doc3.setSize(content3.toByteArray().length);
        doc3.setUploadTime(new Date());
    
        repo.save(doc);
        repo2.save(doc2);
        repo3.save(doc3);
    
        ra.addFlashAttribute("message", "The file has been uploaded successfully.");
    
        return "success";
    }
    
    
    @GetMapping("download")
    public void downloadFile(@Param("id") long id, HttpServletResponse response) throws Exception{
        Optional<document> result = repo.findById(id);
        if(!result.isPresent()){
            throw new Exception("Document not found: id="+id);
        }
        document doc = result.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + doc.getName();
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(doc.getContent());
        outputStream.close();
    }
    @GetMapping("download2")
    public void downloadFile2(@Param("id") long id, HttpServletResponse response) throws Exception{
        Optional<document2> result = repo2.findById(id);
        if(!result.isPresent()){
            throw new Exception("Document not found: id="+id);
        }
        document2 doc = result.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + doc.getName();
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(doc.getContent());
        outputStream.close();
    }
    @GetMapping("download3")
    public void downloadFile3(@Param("id") long id, HttpServletResponse response) throws Exception{
        Optional<document3> result = repo3.findById(id);
        if(!result.isPresent()){
            throw new Exception("Document not found: id="+id);
        }
        document3 doc = result.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + doc.getName();
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(doc.getContent());
        outputStream.close();
    }
    
    
    
}
