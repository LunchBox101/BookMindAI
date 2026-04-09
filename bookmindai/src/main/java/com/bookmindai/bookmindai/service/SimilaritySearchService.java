package com.bookmindai.bookmindai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SimilaritySearchService {

    public List<String> findTopChunks(List<Double> questionEmbedding,
                                        List<List<Double>> chunckEmbeddings,
                                        List<String> chunks,
                                        int topk) {
        List<double[]> scores = new ArrayList<>();

        for (int i = 0; i < chunckEmbeddings.size(); i++) {
            double score = cosineSimilarity(questionEmbedding, chunckEmbeddings.get(i));
            scores.add(new double[]{score, i});
        }

        scores.sort((a, b) -> Double.compare(b[0], a[0]));

        List<String> topChunks = new ArrayList<>();
        for (int i = 0; i < Math.min(topk, scores.size()); i++) {
            int index = (int) scores.get(i)[1];
            topChunks.add(chunks.get(index));
        }

        return topChunks;
    }
    
    private double cosineSimilarity(List<Double> vecA, List<Double> vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.size(); i++) {
            dotProduct += vecA.get(i) * vecB.get(i);
            normA += Math.pow(vecA.get(i), 2);
            normB += Math.pow(vecB.get(i), 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
