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
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static Map<String, Object> getStringObjectMap(String sessionId, String geminiResponse) {
        String answer = geminiResponse.substring(geminiResponse.indexOf("\"text\": \"") + 9, geminiResponse.indexOf("\\u003cics\\u003e")).replace("\\n", "<br>");
        String downloadIcsUrl = "http://localhost:8008/download/" + sessionId + "/" + "title" + ".ics";

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("answer", answer);
        response.put("downloadIcsUrl", downloadIcsUrl); // let frontend call this URL for file
        return response;
    }

}
//"""
//{
//  "candidates": [
//    {
//      "content": {
//        "parts": [
//          {
//            "text": "Medicine name: Vitamin D\nTime to take: Every morning\nRepeated: Every day\nStarting date: Today\nEnding date: 2 weeks from today\n\n\u003ccsv\u003e\nSubject,Start Date,Start Time,End Date,End Time,Description,Location,Private\nVitamin D,2024-07-03,08:00:00,2024-07-17,08:00:00,Take Vitamin D every morning,,TRUE\n\u003c/csv\u003e\n\n\u003cics\u003e\nBEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Example Corp.//CalDAV Client//EN\nBEGIN:VEVENT\nUID:vitamin-d-20240703\nDTSTAMP:20240703T000000Z\nDTSTART:20240703T080000\nDTEND:20240703T080000\nSUMMARY:Vitamin D\nDESCRIPTION:Take Vitamin D every morning\nRRULE:FREQ=DAILY;UNTIL=20240717T070000Z\nEND:VEVENT\nEND:VCALENDAR\n\u003c/ics\u003e\n"
//          }
//        ],
//        "role": "model"
//      },
//      "finishReason": "STOP",
//      "avgLogprobs": -0.032120912725275216
//    }
//  ],
//  "usageMetadata": {
//    "promptTokenCount": 220,
//    "candidatesTokenCount": 275,
//    "totalTokenCount": 495,
//    "promptTokensDetails": [
//      {
//        "modality": "TEXT",
//        "tokenCount": 220
//      }
//    ],
//    "candidatesTokensDetails": [
//      {
//        "modality": "TEXT",
//        "tokenCount": 275
//      }
//    ]
//  },
//  "modelVersion": "gemini-2.0-flash",
//  "responseId": "rI2OaNLuJcyFn9kPlovd-AI"
//}
//
//"""