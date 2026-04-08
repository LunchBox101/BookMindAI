package com.bookmindai.bookmindai.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BookStoreService {

    private String currentBookText;
    private String currentBookName;
    private List<String> currentChunks;
    private List<List<Double>> currentEmbeddings;

    public void storeBook(String bookName, String bookText) {
        this.currentBookName = bookName;
        this.currentBookText = bookText;
    }

    public String getCurrentBookText() {
        return currentBookText;
    }

    public String getCurrentBookName() {
        return currentBookName;
    }

    public boolean hasBook() {
        return currentBookText != null && !currentBookText.isEmpty();
    }

    public List<String> getCurrentChunks() {
        return currentChunks;
    }

    public void storeChunks(List<String> chunks) {
        this.currentChunks = chunks;
    }

    public List<List<Double>> getCurrentEmbeddings() {
        return currentEmbeddings;
    }

    public void storeEmbeddings(List<List<Double>> embeddings) {
        this.currentEmbeddings = embeddings;
    }
}
