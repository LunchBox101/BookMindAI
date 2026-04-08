package com.bookmindai.bookmindai.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

    private static final Logger log = LoggerFactory.getLogger(ChunkingService.class);
    private static final int CHUNK_SIZE = 1000; // characters per chunk
    private static final int OVERLAP = 200; // characters of overlap between chunks

    public List<String> chunk(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());
            chunks.add(text.substring(start, end));
            start += CHUNK_SIZE - OVERLAP; // move forward with overlap
        }
        log.info("Text chunked into {} chunks", chunks.size());
        return chunks;
    }
}
