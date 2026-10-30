package com.documind.controller;

import com.documind.service.SummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping("/generate")
    public Map<String, String> generateSummary(
            @RequestBody Map<String, String> request) {

        String extractedText = request.get("extractedText");

        if (extractedText == null || extractedText.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please upload a PDF with readable text first."
            );
        }

        try {
            String summary = summaryService.summarize(extractedText);
            return Map.of("summary", summary);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Summary generation was interrupted."
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to connect to Ollama. Check that Ollama is running."
            );
        }
    }
}
