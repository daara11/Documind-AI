package com.documind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Service
public class SummaryService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String summarize(String extractedText)
            throws IOException, InterruptedException {

        if (extractedText == null || extractedText.isBlank()) {
            throw new IllegalArgumentException(
                    "No extracted PDF text was provided."
            );
        }

        String prompt =
                "Summarize the following PDF content in clear, " +
                "easy-to-understand language. Preserve the important " +
                "facts, definitions, and key points. Do not invent " +
                "information. Use headings and bullet points where " +
                "appropriate.\n\nPDF CONTENT:\n" + extractedText;

        String requestBody = objectMapper.writeValueAsString(
                Map.of(
                        "model", "llama3.2:3b",
                        "prompt", prompt,
                        "stream", false
                )
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:11434/api/generate"
                ))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Ollama returned HTTP " + response.statusCode()
            );
        }

        JsonNode json = objectMapper.readTree(response.body());
        JsonNode summary = json.get("response");

        if (summary == null || summary.asText().isBlank()) {
            throw new IOException(
                    "Ollama returned an empty summary."
            );
        }

        return summary.asText().trim();
    }
}