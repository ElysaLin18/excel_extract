package com.example.demo.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.models.document;
import com.example.demo.models.document2;
import com.example.demo.models.document3;
import com.example.demo.models.DocumentRepository;
import com.example.demo.models.Document2Repository;
import com.example.demo.models.Document3Repository;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class UsersController {  
    
    @Autowired
    private DocumentRepository repo;
    @Autowired
    private Document2Repository repo2;
    @Autowired
    private Document3Repository repo3;
    private DocumentService documentService = new DocumentService(repo, repo2, repo3);

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
    @GetMapping("/preview/{id}")
    public ResponseEntity<String> previewFile(@PathVariable Long id) {
        try {
            String htmlTable = documentService.excelToHtmlTable(repo, id);
            return ResponseEntity.ok().body(htmlTable);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        }
    }
    @GetMapping("/2preview/{id}")
    public ResponseEntity<String> previewFile2(@PathVariable Long id) {
        try {
            String htmlTable = documentService.excelToHtmlTable2(repo2, id);
            return ResponseEntity.ok().body(htmlTable);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        }
    }
    @GetMapping("/3preview/{id}")
    public ResponseEntity<String> previewFile3(@PathVariable Long id) {
        ZipSecureFile.setMinInflateRatio(0.001);
        try {
            String htmlTable = documentService.excelToHtmlTable3(repo3, id);
            return ResponseEntity.ok().body(htmlTable);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        }
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
    
        String fileName3 = "generated_" + doc.getName();
    
        document3 doc3 = new document3();
        doc3.setName(fileName3);
        doc3.setContent(content3.toByteArray());
        doc3.setSize(content3.toByteArray().length);
        doc3.setUploadTime(new Date());
    
        
        
    
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

    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            repo.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The file has been deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error occurred during deletion.");
        }
        return "redirect:/view";
    }

    @GetMapping("/delete3/{id}")
    public String deleteDocument3(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            repo3.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The file has been deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error occurred during deletion.");
        }
        return "redirect:/view3";
    }

    @GetMapping("/delete2/{id}")
    public String deleteDocument2(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            repo2.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The file has been deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error occurred during deletion.");
        }
        return "redirect:/view2";
    }
    
    
    
}
