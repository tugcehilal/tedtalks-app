package com.tugce.tedtalksapp.tedtalks.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.tugce.tedtalksapp.tedtalks.service.TedTalkProcessingService;

@RestController
@RequestMapping("/api/tedtalks")
public class TedTalkController {

    private static final Logger logger = LoggerFactory.getLogger(TedTalkController.class);

    private final TedTalkProcessingService processingService;

    public TedTalkController(TedTalkProcessingService processingService) {
        this.processingService = processingService;
    }

    /**
     * Endpoint to upload and process a CSV file containing TedTalk data.
     *
     * @param file the uploaded CSV file
     * @return ResponseEntity indicating the success or failure of the operation
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Delegate the processing of the CSV to TedTalkProcessingService
            processingService.processCsv(file);

            // Return a success response
            return ResponseEntity.ok("CSV file processed and saved successfully.");
        } catch (Exception e) {
            logger.error("Error processing the CSV file: {}", e.getMessage(), e); // Log at ERROR level
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process and save the CSV file.");
        }
    }
}
