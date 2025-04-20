package microservices.lab1.media.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/media")
public class MediaController {

    private static final String MEDIA_DIR = "src/main/resources/media";

    @RequestMapping("/stream/{title}")
    public ResponseEntity<Resource> streamMedia(@PathVariable("title") String title) {
        try {
            File file = findMediaFile(title);

            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            String mimeType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private File findMediaFile(String title) {
        String sanitizedTitle = StringUtils.cleanPath(title);

        File directory = new File(MEDIA_DIR);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().startsWith(sanitizedTitle)) {
                    return file;
                }
            }
        }
        return null;
    }
}
