package org.medicine_check.main;

import com.google.cloud.storage.Blob;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpSession;
import org.medicine_check.common.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MainController {

    @Autowired
    private FileManager fileManageService;

    @GetMapping("/")
    public String main(
            HttpSession session,
            Model model) {
        model.addAttribute("sessionId", session.getId());
        return "chat";
    }

    @GetMapping("/download/{sessionId}/{fileName}.ics")
    public ResponseEntity<Resource> download(
            Model model,
            @PathVariable String sessionId,
            @PathVariable String fileName) {

        try {
//            Path path = fileManageService.generatePath(sessionId, fileName, ".cs");
//            Resource resource = fileManageService.getIcsFile(path);
            Blob blob = fileManageService.test(sessionId, fileName);
            if (blob == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] content = blob.getContent();
            Resource resource =  new InputStreamResource(new ByteArrayInputStream(content));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".ics")
                    .contentType(MediaType.parseMediaType("text/calendar"))
                    .body(resource);
        }
        catch (Exception e) {
            model.addAttribute("message", "failed due to: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}