package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.exception.CsvParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")

class CsvImporterServiceTest {

    private CsvImporterService csvImporterService;

    // Define YEAR_MONTH_FORMATTER at the class level
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    @BeforeEach
    void setUp() {
        csvImporterService = new CsvImporterService();
    }

    @Test
    void testParseCsvWithValidData() throws Exception {
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,December 2021,1300000,19000,http://example.com/talk1
                Talk 2,Author 2,February 2022,50000,1000,http://example.com/talk2
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        List<TedTalkModel> tedTalks = csvImporterService.parseCsv(file);

        assertEquals(2, tedTalks.size());
        TedTalkModel talk1 = tedTalks.get(0);
        assertEquals("Talk 1", talk1.getTitle());
        assertEquals("Author 1", talk1.getAuthor());
        assertEquals(YearMonth.parse("December 2021", YEAR_MONTH_FORMATTER), talk1.getDate());
        assertEquals(1300000, talk1.getViews());
        assertEquals(19000, talk1.getLikes());
        assertEquals("http://example.com/talk1", talk1.getLink());
    }

    @Test
    void testParseCsvWithInvalidNumericData() throws Exception {
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,December 2021,abcd,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        List<TedTalkModel> tedTalks = csvImporterService.parseCsv(file);

        assertEquals(1, tedTalks.size());
        TedTalkModel talk = tedTalks.get(0);
        assertEquals(0, talk.getViews()); // Fallback to 0 for invalid views
        assertEquals(19000, talk.getLikes());
    }

    @Test
    void testParseCsvWithInvalidDate() throws Exception {
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,InvalidDate,1300000,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        List<TedTalkModel> tedTalks = csvImporterService.parseCsv(file);

        assertEquals(1, tedTalks.size());
        TedTalkModel talk = tedTalks.get(0);

        // Verify fallback date for invalid date
        assertEquals(YearMonth.now(), talk.getDate()); // Invalid "InvalidDate" falls back to current date
    }

    @Test
    void testParseCsvWithInvalidHeaders() {
        String csvContent = """
                incorrectHeader1,incorrectHeader2,date,views,likes,link
                Talk 1,Author 1,December 2021,1300000,19000,http://example.com/talk1
                """;
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", csvContent.getBytes());

        CsvParseException exception = assertThrows(CsvParseException.class, () -> csvImporterService.parseCsv(file));

        // Validate the exact error message
        String expectedMessage = "Invalid CSV headers. Expected: [title, author, date, views, likes, link]";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testParseCsvWithEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", "".getBytes());

        CsvParseException exception = assertThrows(CsvParseException.class, () -> csvImporterService.parseCsv(file));

        // Validate the exact error message
        String expectedMessage = "CSV file is empty";
        assertEquals(expectedMessage, exception.getMessage());
    }

}
