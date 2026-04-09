package com.bookmindai.bookmindai.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final String MODEL = "llama3.1";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String ask(String question, List<String> contextChunks) throws Exception {
        String context = String.join("\n\n", contextChunks);

        String prompt = """
            You are a helpful assistant. Answer the question based ONLY on the context below.
            If the answer is not in the context, say "I couldn't find that in the book."
            
            Context:
            %s
            
            Question: %s
            
            Answer:
            """.formatted(context, question);
        
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", MODEL);
        requestMap.put("prompt", prompt);
        requestMap.put("stream", false);

        String body = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OLLAMA_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        var root = objectMapper.readTree(response.body());
        return root.get("response").toString().replace("\"", "");
    }
}
