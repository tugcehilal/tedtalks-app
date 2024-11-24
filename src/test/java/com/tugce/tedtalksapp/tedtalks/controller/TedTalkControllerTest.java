package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.exception.CsvParseException;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.service.CsvImporterService;
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

import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test") // Ensure the test profile is used with test-specific properties
class TedTalkControllerTest {

    @MockBean
    private CsvImporterService csvImporterService; // Use @MockBean for Spring Boot integration

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

        // Mock the service response
        List<TedTalkModel> tedTalkModels = List.of(new TedTalkModel(
                "Talk 1",
                "Author 1",
                YearMonth.of(2021, 12),
                1300000L,
                19000L,
                "http://example.com/talk1"
        ));
        when(csvImporterService.parseCsv(file)).thenReturn(tedTalkModels);

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Talk 1"))
                .andExpect(jsonPath("$[0].author").value("Author 1"))
                .andExpect(jsonPath("$[0].date").value("December 2021"))
                .andExpect(jsonPath("$[0].views").value(1300000))
                .andExpect(jsonPath("$[0].likes").value(19000))
                .andExpect(jsonPath("$[0].link").value("http://example.com/talk1"));

        // Verify interaction with the service
        verify(csvImporterService, times(1)).parseCsv(file);
    }

    @Test
    void testUploadCsvInvalidFile() throws Exception {
        String csvContent = """
                incorrectHeader1,incorrectHeader2,date,views,likes,link
                Talk 1,Author 1,December 2021,1300000,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        // Mock the service throwing an exception
        when(csvImporterService.parseCsv(file)).thenThrow(
                new CsvParseException("Invalid CSV headers. Expected: [title, author, date, views, likes, link]")
        );

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Verify interaction with the service
        verify(csvImporterService, times(1)).parseCsv(file);
    }

    @Test
    void testUploadCsvEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", "".getBytes());

        // Mock the service throwing an exception
        when(csvImporterService.parseCsv(file)).thenThrow(new CsvParseException("CSV file is empty"));

        // Perform the POST request
        mockMvc.perform(multipart("/api/tedtalks/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Verify interaction with the service
        verify(csvImporterService, times(1)).parseCsv(file);
    }
}
