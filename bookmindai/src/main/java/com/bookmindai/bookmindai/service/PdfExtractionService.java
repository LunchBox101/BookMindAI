package com.bookmindai.bookmindai.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PdfExtractionService {
    
    private static final Logger log = LoggerFactory.getLogger(PdfExtractionService.class);

    public String extractText(InputStream pdfInputStream) throws IOException {
        try( PDDocument document = Loader.loadPDF(pdfInputStream.readAllBytes())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            log.info("Extracted {} characters from PDF", text.length());
            return text;
        } 
    }
}
