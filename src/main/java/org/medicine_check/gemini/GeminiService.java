package org.medicine_check.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.system-instruction}")
    private String geminiSystemInstruction;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    public String askGemini(String prompt) {
        try {
            // request body
            Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                    "parts", List.of(
                        Map.of("text", geminiSystemInstruction.replace("{today_date}"
                                , LocalDateTime.now() + ""))
                    )
                ),
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                ),
                "generationConfig", Map.of(
                    "stopSequences", List.of("Title"),
                    "temperature", 0.0,
                    "topP", 0.45
                )
            );

            // api call
            String response = webClient.post()
                    .uri(geminiApiUrl)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;
        } catch (Exception e) {
            e.printStackTrace(); // This will show the actual root cause
            throw new RuntimeException("GeminiService error: " + e.getMessage(), e);
        }
    }

}
