package org.medicine_check.main;

import jakarta.servlet.http.HttpSession;
import org.medicine_check.common.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URL;

@Controller
public class MainController {

    @Autowired
    private FileManager fileManager;

    @GetMapping("/")
    public String main(
            HttpSession session,
            Model model) {
        model.addAttribute("sessionId", session.getId());
        return "chat";
    }

    @GetMapping("/download/{sessionId}/{fileName}.ics")
    public ResponseEntity<String> download(
            Model model,
            @PathVariable String sessionId,
            @PathVariable String fileName) {

        try {
            URL signedUrl = fileManager.getFileUrl(sessionId, fileName);

            return ResponseEntity.ok(signedUrl.toString());
        }
        catch (Exception e) {
            model.addAttribute("message", "failed due to: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File Not Found");
        }
    }

}
