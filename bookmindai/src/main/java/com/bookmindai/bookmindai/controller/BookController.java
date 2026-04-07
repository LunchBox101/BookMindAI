package com.bookmindai.bookmindai.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bookmindai.bookmindai.service.BookStoreService;
import com.bookmindai.bookmindai.service.PdfExtractionService;

@RestController
@RequestMapping("/api")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final PdfExtractionService pdfService;
    private final BookStoreService bookStore;

    public BookController(PdfExtractionService pdfService, BookStoreService bookStore) {
        this.pdfService = pdfService;
        this.bookStore = bookStore;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadBook(@RequestParam("file") MultipartFile file) {
        try {
            String text = pdfService.extractText(file.getInputStream());
            bookStore.storeBook(file.getOriginalFilename() != null ? file.getOriginalFilename() : "Unknown", text);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", file.getOriginalFilename(),
                "character", text.length()
            ));
        } catch (Exception e) {
            log.error("Failed to process PDF: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
