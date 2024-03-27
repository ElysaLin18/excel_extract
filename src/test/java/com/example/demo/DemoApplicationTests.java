package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import javax.swing.text.Document;

import com.example.demo.models.DocumentRepository;
import com.example.demo.models.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DemoApplicationTests {

	@Autowired
	private DocumentRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@Rollback(false)
	void testInsertDocument() throws IOException {
		File file = new File("/Users/lucialiu/Desktop/cmpt276/assignment1.zip");
		document doc = new document();
		doc.setName(file.getName());
		byte[] bytes = Files.readAllBytes(file.toPath());
		doc.setContent(bytes);
		long fileSize = bytes.length;
		doc.setSize(fileSize);
		doc.setUploadTime(new Date());

		document savedDoc = repo.save(doc);
		document existDoc = entityManager.find(document.class, savedDoc.getId());
		assertEquals(fileSize, existDoc.getSize());

	}

}
