package org.medicine_check.gemini;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medicine_check.common.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gemini")
public class GemeniRestController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private FileManager fileManageService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateCalendarFormat(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            @RequestBody Map<String,String> payload) throws IOException, IOException {
        try {
            String question = payload.get("question");
            List<String> previousChats = geminiService.getChatList(request);
            String geminiResponse = geminiService.askGemini(question, previousChats);
            // 17 = len of \u003cics\u003e
            String icsStr = geminiResponse.substring(geminiResponse.indexOf("\\u003cics\\u003e") + 17, geminiResponse.indexOf("\\u003c/ics\\u003e")).replace("\\n", "\r\n");
            fileManageService.saveIcsFile(session.getId(), "title", icsStr);
            Map<String, Object> data = getStringObjectMap(session.getId(), geminiResponse);

            geminiService.setChatList(request, response, Map.of("role", "user", "content", question));
            geminiService.setChatList(request, response, Map.of("role", "model", "content", question));
            return ResponseEntity.ok(data);
        }
        catch(Exception e) {
            e.printStackTrace(); // show detailed error in logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", 500, "message", "Error generating ICS"));
        }
    }

    private static Map<String, Object> getStringObjectMap(String sessionId, String geminiResponse) {
        String answer = geminiResponse.substring(geminiResponse.indexOf("\"text\": \"") + 9, geminiResponse.indexOf("\\u003cics\\u003e")).replace("\\n", "<br>");
        String downloadIcsUrl = "https://storage.googleapis.com/" + sessionId + "/" + "title" + ".ics";

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("answer", answer);
        response.put("downloadIcsUrl", downloadIcsUrl); // let frontend call this URL for file
        return response;
    }

}