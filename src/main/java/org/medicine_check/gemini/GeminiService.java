package org.medicine_check.gemini;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medicine_check.common.CookieManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private CookieManager cookieManager;

    @Value("${gemini.system-instruction}")
    private String geminiSystemInstruction;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    public String askGemini(String prompt, List<String> previousChats) {
        try {
            // request body
            Map<String, Object> requestBody = new java.util.HashMap<>(Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(
                                    Map.of("text", geminiSystemInstruction.replace("{today_date}"
                                            , LocalDateTime.now() + ""))
                            )
                    )
            ));

            // add previous chats to contents
            Object[] contents = new Object[previousChats.size() + 1];
            for (int i = 0; i < previousChats.size(); i++) {
                String[] prevChat = previousChats.get(i).split(":");
                contents[i] = Map.of(
                    "role", prevChat[0],
                    "parts", List.of(Map.of("text", prevChat[1])));
            }
            contents[previousChats.size()] = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt)));
            // put in the request body
            requestBody.put("contents", contents);

            // add configuration
            requestBody.put(
                "generationConfig", Map.of(
                    "stopSequences", List.of("Title"),
                    "temperature", 0.0,
                    "topP", 0.45
            ));

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

    public List<String> getChatList(HttpServletRequest request) {
        Cookie cookie = cookieManager.getCookieByName(request, "chatList");
        List<String> chatList = cookie == null ? new ArrayList<>() : cookieManager.getCookieList(cookie);
        return  chatList;
    }

    public void setChatList(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, String> chat
    ) {

        List<String> chatList = getChatList(request);

        chatList.add(chat.get("role") + ":" + chat.get("content"));

        Cookie c = new Cookie("chatList", URLEncoder.encode(String.join(",", chatList)
                , StandardCharsets.UTF_8));
        c.setMaxAge(60);
        c.setPath("/");
        response.addCookie(c);
    }

}
