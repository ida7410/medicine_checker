package org.medicine_check.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileManageService {

    @Value("${file.upload-path}")
    private String uploadPath;

    public Path saveIcsFile(String sessionId, String title, String icalData) throws IOException {
        Path filePath = generatePath(sessionId, title, ".ics");

        // Write the iCal data into the file
        Files.write(filePath, icalData.getBytes(StandardCharsets.UTF_8));

        // Return the full path to the created file
        return filePath;
    }

    public Path saveCsvFile(String sessionId, String title, String icalData) throws IOException {
        Path filePath = generatePath(sessionId, title, ".csv");

        // Write the iCal data into the file
        Files.write(filePath, icalData.getBytes(StandardCharsets.UTF_8));

        // Return the full path to the created file
        return filePath;
    }

    public Path generatePath(String sessionId, String title, String extension) throws IOException {
        // Create a unique subdirectory using the current timestamp
        String directoryName = sessionId;

        // Final directory path: uploadPath + / + timestamp
        Path targetDir = Paths.get(uploadPath, directoryName);

        // Create the directory if it doesn't exist
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // file name (with .ics)
        String safeTitle = title;
        if (title.endsWith(".ics") && extension.equals(".ics")) {
            safeTitle = title;
        }
        else if (title.endsWith(".csv") && extension.equals(".csv")) {
            safeTitle = title;
        }
        else {
            safeTitle = title + extension;
        }

        // full path: uploadPath/timestamp/title.ics
        Path filePath = targetDir.resolve(safeTitle);

        return filePath;
    }

    public Resource getIcsFile(Path filePath) throws IOException {
        return new UrlResource(filePath.toUri());
    }

}
