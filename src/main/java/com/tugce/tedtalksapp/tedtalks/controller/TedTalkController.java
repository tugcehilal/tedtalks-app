package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.service.CsvImporterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tedtalks")
public class TedTalkController {

    private final CsvImporterService csvImporterService;

    // Constructor injection
    public TedTalkController(CsvImporterService csvImporterService) {
        this.csvImporterService = csvImporterService;
    }

    /**
     * Uploads a CSV file and returns the parsed data as a list of TedTalkDTOs.
     *
     * @param file the uploaded CSV file
     * @return a ResponseEntity containing a list of TedTalkDTOs
     */
    @PostMapping("/upload")
    public ResponseEntity<List<TedTalkDTO>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<TedTalkModel> tedTalkModels = csvImporterService.parseCsv(file);

            // Convert models to DTOs
            List<TedTalkDTO> tedTalkDTOs = tedTalkModels.stream()
                    .map(model -> new TedTalkDTO(
                            model.title(),
                            model.author(),
                            model.date().format(DateTimeFormatter.ofPattern("MMMM yyyy")), // Format YearMonth to "December 2021"
                            model.views(),
                            model.likes(),
                            model.link()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tedTalkDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
