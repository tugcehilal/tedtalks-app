package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TedTalkProcessingServiceTest {

    private final TedTalkRepository repository;
    private final CsvImporterService csvImporterService;
    private final TedTalkPersistenceService persistenceService;
    private final TedTalkProcessingService processingService;

    @Autowired
    public TedTalkProcessingServiceTest(TedTalkRepository repository,
                                        CsvImporterService csvImporterService,
                                        TedTalkPersistenceService persistenceService,
                                        TedTalkProcessingService processingService) {
        this.repository = repository;
        this.csvImporterService = csvImporterService;
        this.persistenceService = persistenceService;
        this.processingService = processingService;
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Clear repository before each test
    }

    @Test
    void testProcessCsv_withValidData() throws Exception {
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

        // Act
        processingService.processCsv(file);

        // Assert
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(2, entities.size());

        TedTalkEntity entity1 = entities.get(0);
        assertEquals("Talk 1", entity1.getTitle());
        assertEquals("Author 1", entity1.getAuthor());
        assertEquals(YearMonth.of(2022, 1), entity1.getDate());
        assertEquals(1000, entity1.getViews());
        assertEquals(500, entity1.getLikes());
        assertEquals("http://example.com/talk1", entity1.getLink());

        TedTalkEntity entity2 = entities.get(1);
        assertEquals("Talk 2", entity2.getTitle());
        assertEquals("Author 2", entity2.getAuthor());
        assertEquals(YearMonth.of(2023, 2), entity2.getDate());
        assertEquals(2000, entity2.getViews());
        assertEquals(1000, entity2.getLikes());
        assertEquals("http://example.com/talk2", entity2.getLink());
    }

    @Test
    void testProcessCsv_withInvalidData() throws Exception {
        // Arrange
        String csvContent = """
                title,author,date,views,likes,link
                Talk 1,Author 1,InvalidDate,abcd,500,http://example.com/talk1
                Talk 2,Author 2,February 2023,2000,abcd,http://example.com/talk2
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid_tedtalks.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act
        processingService.processCsv(file);

        // Assert
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(2, entities.size());

        TedTalkEntity entity1 = entities.get(0);
        assertEquals("Talk 1", entity1.getTitle());
        assertEquals("Author 1", entity1.getAuthor());
        assertEquals(YearMonth.now(), entity1.getDate()); // Defaulted to current date
        assertEquals(0, entity1.getViews()); // Defaulted to 0
        assertEquals(500, entity1.getLikes());
        assertEquals("http://example.com/talk1", entity1.getLink());

        TedTalkEntity entity2 = entities.get(1);
        assertEquals("Talk 2", entity2.getTitle());
        assertEquals("Author 2", entity2.getAuthor());
        assertEquals(YearMonth.of(2023, 2), entity2.getDate());
        assertEquals(2000, entity2.getViews());
        assertEquals(0, entity2.getLikes()); // Defaulted to 0
        assertEquals("http://example.com/talk2", entity2.getLink());
    }

    @Test
    void testProcessCsv_withEmptyFile() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );

        // Act & Assert
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            processingService.processCsv(file);
        });

        assertEquals("CSV file is empty", exception.getMessage());

        // Verify no data is saved
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(0, entities.size());
    }
}

