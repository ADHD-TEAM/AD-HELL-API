package com.adhd.ad_hell.domain.advertise.command.application.controller;

import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.service.AdCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

// ... existing code ...

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdCommandController {

    private final AdCommandService adCommandService;

    /* 광고 생성 (JSON 데이터만) */
    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> createAd(@RequestBody AdCreateRequest req) {
        Long adId = adCommandService.createAd(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("adId", adId));
    }

    @PostMapping(value = "/delete", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> deleteAd(@RequestBody AdCreateRequest req) {
        adCommandService.deleteAd(req);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/update", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> updateAd(@RequestBody AdUpdateRequest req) {
        adCommandService.updateAd(req);
        return ResponseEntity.noContent().build();
    }
}
