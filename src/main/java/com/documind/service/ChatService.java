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
public class ChatService {

    private static final String MODEL = "llama3.2:3b";

    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public String answer(String question, String mode, String documentText)
            throws IOException, InterruptedException {

        String prompt;

        if ("pdf".equalsIgnoreCase(mode)) {
            if (documentText == null || documentText.isBlank()) {
                throw new IllegalArgumentException(
                        "Please upload a readable PDF first."
                );
            }

            // Select a relevant portion of the uploaded PDF.
            String relevantText = findRelevantText(documentText, question);

            prompt = "You are DocuMind AI, a helpful PDF assistant.\n"
                    + "Answer the user's question using the document "
                    + "provided below.\n"
                    + "If the document does not contain the answer, "
                    + "say so honestly. Do not invent facts.\n\n"
                    + "DOCUMENT EXCERPTS:\n" + relevantText
                    + "\n\nUSER QUESTION:\n" + question;
        } else {
            prompt = "You are DocuMind AI, a friendly and helpful "
                    + "general AI assistant.\n"
                    + "Answer the user's question clearly and accurately. "
                    + "Use simple explanations when useful.\n\n"
                    + "USER QUESTION:\n" + question;
        }

        Map<String, Object> body = Map.of(
                "model", MODEL,
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                        "num_ctx", 8192,
                        "num_predict", 700,
                        "temperature", 0.3
                )
        );

        String requestBody = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Ollama error HTTP " + response.statusCode()
            );
        }

        JsonNode json = mapper.readTree(response.body());
        JsonNode answer = json.get("response");

        if (answer == null || answer.asText().isBlank()) {
            throw new IOException("The AI returned an empty answer.");
        }

        return answer.asText().trim();
    }

    private String findRelevantText(String text, String question) {
        String[] chunks = text.split("(?<=\\.)\\s+|\\n\\s*\\n");
        String[] words = question.toLowerCase()
                .split("[^a-z0-9]+");

        StringBuilder result = new StringBuilder();
        int added = 0;

        for (String chunk : chunks) {
            String lower = chunk.toLowerCase();
            int score = 0;

            for (String word : words) {
                if (word.length() > 2 && lower.contains(word)) {
                    score++;
                }
            }

            if (score > 0) {
                result.append(chunk).append("\n");
                added++;

                if (result.length() >= 12000 || added >= 20) {
                    break;
                }
            }
        }

        if (result.length() == 0) {
            return text.substring(0, Math.min(text.length(), 12000));
        }

        return result.toString();
    }
}