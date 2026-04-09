package com.bookmindai.bookmindai.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bookmindai.bookmindai.service.BookStoreService;
import com.bookmindai.bookmindai.service.ChunkingService;
import com.bookmindai.bookmindai.service.EmbeddingService;
import com.bookmindai.bookmindai.service.OllamaService;
import com.bookmindai.bookmindai.service.PdfExtractionService;
import com.bookmindai.bookmindai.service.SimilaritySearchService;

@RestController
@RequestMapping("/api")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final PdfExtractionService pdfService;
    private final BookStoreService bookStore;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final SimilaritySearchService similaritySearchService;
    private final OllamaService ollamaService;
    
    public BookController(PdfExtractionService pdfService, BookStoreService bookStore, 
            ChunkingService chunkingService, EmbeddingService embeddingService,
            SimilaritySearchService similaritySearchService, OllamaService ollamaService) {
        this.pdfService = pdfService;
        this.bookStore = bookStore;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
        this.similaritySearchService = similaritySearchService;
        this.ollamaService = ollamaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadBook(@RequestParam("file") MultipartFile file) {
        try {
            String text = pdfService.extractText(file.getInputStream());
            bookStore.storeBook(file.getOriginalFilename() != null ? file.getOriginalFilename() : "Unknown", text);
            List<String> chunks = chunkingService.chunk(text);
            bookStore.storeChunks(chunks);
            log.info("Embedding {} chunks...", chunks.size());
            List<List<Double>> embeddings = new ArrayList<>();
            for (String chunk : chunks) {
                embeddings.add(embeddingService.embed(chunk));
            }
            bookStore.storeEmbeddings(embeddings);
            log.info("Embedding completed for all chunks");
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", file.getOriginalFilename(),
                "character", text.length(),
                "embeddings", embeddings.size()
            ));
        } catch (Exception e) {
            log.error("Failed to process PDF: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ask")
    public ResponseEntity<Map<String, Object>> askQuestion(@RequestParam("q") String question) {
        try {
            if(!bookStore.hasBook()) {
               Map<String, Object> error = new HashMap<>();
                error.put("error", "No book uploaded yet");
                return ResponseEntity.badRequest().body(error);
            }

            List<Double> questionEmbedding = embeddingService.embed(question);
            List<String> topChunks = similaritySearchService.findTopChunks(questionEmbedding,
                    bookStore.getCurrentEmbeddings(), bookStore.getCurrentChunks(), 3);

            String answer = ollamaService.ask(question, topChunks);

            Map<String, Object> response = new HashMap<>();
            response.put("question", question);
            response.put("answer", answer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to answer question: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
}
