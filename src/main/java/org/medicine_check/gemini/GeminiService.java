package org.medicine_check.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
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
                    "contents", new Object[] {
                            Map.of("parts", new Object[] {
                                    Map.of("text", geminiSystemInstruction + "\n\n" + prompt)
                            })
                }
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
