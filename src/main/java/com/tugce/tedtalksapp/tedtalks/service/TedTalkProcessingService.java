package com.tugce.tedtalksapp.tedtalks.service;


import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TedTalkProcessingService {
    private final CsvImporterService csvImporterService;
    private final TedTalkPersistenceService persistenceService;

    public TedTalkProcessingService(CsvImporterService csvImporterService, TedTalkPersistenceService persistenceService) {
        this.csvImporterService = csvImporterService;
        this.persistenceService = persistenceService;
    }

    /**
     * Processes the uploaded CSV file by parsing and saving its content to the database.
     *
     * @param file the uploaded CSV file
     */
    public void processCsv(MultipartFile file) {
        // Parse the CSV into TedTalkModel objects
        List<TedTalkModel> tedTalkModels = csvImporterService.parseCsv(file);

        // Save the parsed data to the database
        persistenceService.saveAll(tedTalkModels);
    }
}
