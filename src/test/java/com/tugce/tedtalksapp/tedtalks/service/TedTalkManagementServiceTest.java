package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TedTalkManagementServiceTest {

    private final TedTalkRepository repository;
    private final TedTalkManagementService service;

    @Autowired
    public TedTalkManagementServiceTest(TedTalkRepository repository, TedTalkManagementService service) {
        this.repository = repository;
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Clear repository before each test
    }

    @Test
    void testCreateTedTalk() {
        // Arrange
        TedTalkModel model = new TedTalkModel("Title", "Author", YearMonth.of(2022, 1), 1000, 500, "link");

        // Act
        TedTalkModel result = service.createTedTalk(model);

        // Assert
        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        assertEquals("Author", result.getAuthor());
        assertEquals(YearMonth.of(2022, 1), result.getDate());
        assertEquals(1000, result.getViews());
        assertEquals(500, result.getLikes());
        assertEquals("link", result.getLink());
    }

    @Test
    void testGetAllTedTalks() {
        // Arrange
        repository.save(new TedTalkEntity(null, "Title1", "Author1", YearMonth.of(2022, 1), 1000, 500, "link1"));
        repository.save(new TedTalkEntity(null, "Title2", "Author2", YearMonth.of(2023, 2), 2000, 1000, "link2"));

        // Act
        List<TedTalkModel> results = service.getAllTedTalks();

        // Assert
        assertEquals(2, results.size());
        assertEquals("Title1", results.get(0).getTitle());
        assertEquals("Title2", results.get(1).getTitle());
    }

    @Test
    void testGetTedTalkById() {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Title", "Author", YearMonth.of(2022, 1), 1000, 500, "link"));

        // Act
        TedTalkModel result = service.getTedTalkById(entity.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        assertEquals("Author", result.getAuthor());
    }

    @Test
    void testUpdateTedTalk() {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Old Title", "Old Author", YearMonth.of(2022, 1), 1000, 500, "link"));
        TedTalkModel updatedModel = new TedTalkModel("New Title", "New Author", YearMonth.of(2023, 2), 2000, 1000, "newLink");

        // Act
        TedTalkModel result = service.updateTedTalk(entity.getId(), updatedModel);

        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(YearMonth.of(2023, 2), result.getDate());
    }

    @Test
    void testDeleteTedTalk() {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Title", "Author", YearMonth.of(2022, 1), 1000, 500, "link"));

        // Act
        service.deleteTedTalk(entity.getId());

        // Assert
        assertFalse(repository.findById(entity.getId()).isPresent());
    }
}
