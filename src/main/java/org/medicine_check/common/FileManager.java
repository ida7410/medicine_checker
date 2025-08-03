package org.medicine_check.common;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class FileManager {

    @Value("${gcs.bucket-name}")
    private String bucketName;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public Path saveIcsFile(String sessionId, String title, String data) throws IOException {
        String objectName = sessionId + "/" + title + ".ics";

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("text/plain")
                .build();

        storage.create(blobInfo, data.getBytes(StandardCharsets.UTF_8));
        return Path.of(objectName);
    }

    public Path generatePath(String sessionId, String title, String extension) {
        return Path.of(sessionId, title + extension);
    }

    public Resource getIcsFile(Path filePath) throws IOException {
        String objectName = filePath.toString().replace("\\", "/"); // normalize for Windows paths
        Blob blob = storage.get(bucketName, objectName);
        if (blob == null || !blob.exists()) {
            return null;
        }

        byte[] content = blob.getContent();
        return new InputStreamResource(new ByteArrayInputStream(content));
    }

    public String generateSignedUrl(String objectName, long durationSeconds) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
        URL url = storage.signUrl(blobInfo, durationSeconds, TimeUnit.SECONDS, Storage.SignUrlOption.withV4Signature());
        return url.toString();
    }

    public Blob test(String sessionId, String title) {
        return storage.get(BlobId.of(bucketName, sessionId + "/" + title + ".ics"));
    }

}