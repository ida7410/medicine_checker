package org.medicine_check.common;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Component
public class FileManager {

    @Value("${file.upload-path}")
    private String uploadPath;

    private final String BUCKET_NAME = "generated-ics";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public URL saveIcsFile(String sessionId, String title, String icalData) throws IOException {
        String objectName = sessionId + "/" + title + ".ics";

        BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("text/calendar")
                .build();

        // Save the content as a blob in the bucket
        storage.create(blobInfo, icalData.getBytes(StandardCharsets.UTF_8));

        // Generate a signed URL that is valid for 15 minutes.
        // This is a secure way to allow temporary downloads without
        // making the file publicly accessible forever.
        return storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    }

    public URL getFileUrl(String sessionId, String title) {
        String objectName = sessionId + "/" + title + ".ics";

        Blob blob = storage.get(BUCKET_NAME).get(BUCKET_NAME + objectName);

        if (blob == null || !blob.exists()) {
            return null;
        }

        // This is the main change: We now need to build a BlobInfo object
        // to pass to the signUrl method.
        BlobInfo blobInfo = BlobInfo.newBuilder(blob.getBlobId()).build();

        // Generate a signed URL for 15 minutes
        return storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    }
}
