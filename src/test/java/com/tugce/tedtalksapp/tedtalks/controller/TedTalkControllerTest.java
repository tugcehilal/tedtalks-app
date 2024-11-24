package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.exception.CsvParseException;
import com.tugce.tedtalksapp.tedtalks.service.TedTalkProcessingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class TedTalkControllerTest {

    @MockBean
    private TedTalkProcessingService processingService; // Mock the processing service

    private MockMvc mockMvc;

    @Autowired
    public void setMockMvc(TedTalkController tedTalkController) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tedTalkController).build();
    }

    @Test
    void testUploadCsvSuccess() throws Exception {
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,December 2021,1300000,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        // Mock the processing service to do nothing (successful execution)
        doNothing().when(processingService).processCsv(file);

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("CSV file processed and saved successfully."));

        // Verify interaction with the processing service
        verify(processingService, times(1)).processCsv(file);
    }

    @Test
    void testUploadCsvInvalidFile() throws Exception {
        String csvContent = """
                incorrectHeader1,incorrectHeader2,date,views,likes,link
                Talk 1,Author 1,December 2021,1300000,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        // Mock the processing service to throw an exception
        doThrow(new CsvParseException("Invalid CSV headers. Expected: [title, author, date, views, likes, link]"))
                .when(processingService).processCsv(file);

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to process and save the CSV file."));

        // Verify interaction with the processing service
        verify(processingService, times(1)).processCsv(file);
    }

    @Test
    void testUploadCsvEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", "".getBytes());

        // Mock the processing service to throw an exception for an empty file
        doThrow(new CsvParseException("CSV file is empty"))
                .when(processingService).processCsv(file);

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to process and save the CSV file."));

        // Verify interaction with the processing service
        verify(processingService, times(1)).processCsv(file);
    }
}
