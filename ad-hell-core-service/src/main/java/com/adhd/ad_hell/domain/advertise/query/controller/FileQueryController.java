package com.adhd.ad_hell.domain.advertise.query.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileQueryController {

  @Value("${file.file-dir}")
  private String uploadDir;

  @GetMapping("/{fileName}")
  public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
    try {
      Path path = Paths.get(uploadDir).resolve(fileName).normalize();
      Resource resource = new UrlResource(path.toUri());
      if (!resource.exists()) return ResponseEntity.notFound().build();

      String contentType = Files.probeContentType(path);
      if (contentType == null) contentType = "application/octet-stream";

      return ResponseEntity.ok()
                           .header(HttpHeaders.CONTENT_TYPE, contentType)
                           .body(resource);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
