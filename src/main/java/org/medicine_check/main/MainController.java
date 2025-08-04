package org.medicine_check.main;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.medicine_check.common.CookieManager;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpSession;
import org.medicine_check.common.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private FileManager fileManageService;

    @Autowired
    private CookieManager cookieManager;

    @GetMapping("/")
    public String main(
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("sessionId", session.getId());
        Cookie c = cookieManager.getCookieByName(request, "chatList");
        if (c != null) {
            List<String> chatsStrList = cookieManager.getCookieList(c);
            List<Map<String, String>> chatsMapList = new ArrayList<>();
            for (String s : chatsStrList) {
                Map<String, String> chatsMap = new HashMap<>();
                String[] split = s.split("="); // user:prompt & model:response
                chatsMap.put("role", split[0]);
                if (split[0].equals("model")) {
                    String[] split2 = split[1].split(";");
                    chatsMap.put("content", split2[0]);
                    chatsMap.put("downloadIcsUrl", split2[1]);
                }
                else {
                    chatsMap.put("content", split[1]);
                }
                chatsMapList.add(chatsMap);
            }
            model.addAttribute("chatsMapList", chatsMapList);
        }
        else {
            model.addAttribute("chatsMapList", null);
        }
        return "chat";
    }

    @GetMapping("/download/{dir}/{fileName}.{extension}")
    public ResponseEntity<Resource> download(
            Model model,
            @PathVariable String dir,
            @PathVariable String fileName,
            @PathVariable String extension) {

        try {
            Path path = fileManageService.generatePath(dir, fileName, "." + extension);
            Resource resource = fileManageService.getIcsFile(path);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "." + extension)
                    .contentType(MediaType.parseMediaType("text/calendar"))
                    .body(resource);
        }
        catch (Exception e) {
            model.addAttribute("message", "failed due to: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/reset-chat-cookie")
    public String resetChatCookie(HttpServletRequest request) {
        cookieManager.getCookieByName(request, "chatList").setMaxAge(0);
        return "redirect:/";
    }

}
