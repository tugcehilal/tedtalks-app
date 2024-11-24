package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.service.CsvImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tedtalks")
public class TedTalkController {

    private static final Logger logger = LoggerFactory.getLogger(TedTalkController.class);

    private final CsvImporterService csvImporterService;

    public TedTalkController(CsvImporterService csvImporterService) {
        this.csvImporterService = csvImporterService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<TedTalkDTO>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<TedTalkModel> tedTalkModels = csvImporterService.parseCsv(file);

            List<TedTalkDTO> tedTalkDTOs = tedTalkModels.stream()
                    .map(model -> new TedTalkDTO(
                            model.getTitle(),
                            model.getAuthor(),
                            model.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            model.getViews(),
                            model.getLikes(),
                            model.getLink()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tedTalkDTOs);
        } catch (Exception e) {
            logger.error("Error processing the CSV file: {}", e.getMessage(), e); // Log at ERROR level
            return ResponseEntity.badRequest().body(null);
        }
    }
}
