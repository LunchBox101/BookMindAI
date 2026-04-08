package com.bookmindai.bookmindai.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    private static final String OLLAMA_URL = "http://localhost:11434/api/embed";
    private static final String MODEL = "nomic-embed-text";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Double> embed(String text) throws Exception {
        Map<String, String> requestMap = new java.util.HashMap<>();
        requestMap.put("model", MODEL);
        requestMap.put("input", text);
        String body = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OLLAMA_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Ollama response: {}", response.body().substring(0, Math.min(100, response.body().length())));
        
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode embeddingNode = root.get("embeddings").get(0);

        List<Double> embedding = new ArrayList<>();
        for (JsonNode value : embeddingNode) {
            embedding.add(value.asDouble());
        }
        return embedding;
    }
}
