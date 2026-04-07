package com.bookmindai.bookmindai.service;

import org.springframework.stereotype.Service;

@Service
public class BookStoreService {

    private String currentBookText;
    private String currentBookName;

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
}
