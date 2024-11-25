package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import com.tugce.tedtalksapp.tedtalks.service.TedTalkProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TedTalkFileControllerTest {

    private final WebApplicationContext webApplicationContext;
    private final TedTalkRepository repository;
    private final TedTalkProcessingService processingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @Autowired
    public TedTalkFileControllerTest(WebApplicationContext webApplicationContext,
                                     TedTalkRepository repository,
                                     TedTalkProcessingService processingService) {
        this.webApplicationContext = webApplicationContext;
        this.repository = repository;
        this.processingService = processingService;
    }


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        repository.deleteAll(); // Clear repository before each test
    }

    @Test
    void testUploadCsv() throws Exception {
        // Arrange
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,January 2022,1000,500,http://example.com/talk1
                Talk 2,Author 2,February 2023,2000,1000,http://example.com/talk2
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tedtalks.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/tedtalks/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("CSV file processed and saved successfully."));

        // Verify data is saved in the repository
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void testUploadCsvWithInvalidData() throws Exception {
        // Arrange
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,InvalidDate,1000,500,http://example.com/talk1
                Talk 2,Author 2,February 2023,abcd,1000,http://example.com/talk2
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tedtalks_invalid.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/tedtalks/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("CSV file processed and saved successfully."));

        // Verify fallback data is saved
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void testUploadCsvWithEmptyFile() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/tedtalks/upload").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to process and save the CSV file."));

        // Verify no data is saved
        assertEquals(0, repository.findAll().size());
    }
}
