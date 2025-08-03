package org.medicine_check.main;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import jakarta.servlet.http.HttpSession;
import org.medicine_check.common.FileManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;

@Controller
public class MainController {

    @Autowired
    private FileManageService fileManageService;

    @GetMapping("/")
    public String main(
            HttpSession session,
            Model model) {
        model.addAttribute("sessionId", session.getId());
        return "chat";
    }

    @GetMapping("/download/{dir}/{fileName}")
    public ResponseEntity<Resource> download(
            Model model,
            @PathVariable String dir,
            @PathVariable String fileName) {

        try {
            Path path = fileManageService.generatePath(dir, fileName);
            Resource resource = fileManageService.getIcsFile(path);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".ics\"")
                    .contentType(MediaType.parseMediaType("text/calendar"))
                    .body(resource);
        }
        catch (Exception e) {
            model.addAttribute("message", "failed due to: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
