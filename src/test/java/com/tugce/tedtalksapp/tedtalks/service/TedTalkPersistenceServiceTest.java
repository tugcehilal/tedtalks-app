package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TedTalkPersistenceServiceTest {

    private final TedTalkRepository repository;
    private final TedTalkPersistenceService persistenceService;

    @Autowired
    public TedTalkPersistenceServiceTest(TedTalkRepository repository, TedTalkPersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Clear repository before each test
    }

    @Test
    void testSaveAll_withValidModels() {
        // Arrange
        List<TedTalkModel> models = List.of(
                new TedTalkModel("Title1", "Author1", YearMonth.of(2022, 1), 1000, 500, "link1"),
                new TedTalkModel("Title2", "Author2", YearMonth.of(2023, 2), 2000, 1000, "link2")
        );

        // Act
        persistenceService.saveAll(models);

        // Assert
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(2, entities.size());

        TedTalkEntity entity1 = entities.get(0);
        assertEquals("Title1", entity1.getTitle());
        assertEquals("Author1", entity1.getAuthor());
        assertEquals(YearMonth.of(2022, 1), entity1.getDate());
        assertEquals(1000, entity1.getViews());
        assertEquals(500, entity1.getLikes());
        assertEquals("link1", entity1.getLink());

        TedTalkEntity entity2 = entities.get(1);
        assertEquals("Title2", entity2.getTitle());
        assertEquals("Author2", entity2.getAuthor());
        assertEquals(YearMonth.of(2023, 2), entity2.getDate());
        assertEquals(2000, entity2.getViews());
        assertEquals(1000, entity2.getLikes());
        assertEquals("link2", entity2.getLink());
    }

    @Test
    void testSaveAll_withEmptyList() {
        // Arrange
        List<TedTalkModel> models = List.of();

        // Act
        persistenceService.saveAll(models);

        // Assert
        List<TedTalkEntity> entities = repository.findAll();
        assertTrue(entities.isEmpty(), "The repository should remain empty when saving an empty list.");
    }

    @Test
    void testSaveAll_withNullFields() {
        // Arrange
        List<TedTalkModel> models = List.of(
                new TedTalkModel(null, null, null, 0, 0, null)
        );

        // Act
        persistenceService.saveAll(models);

        // Assert
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(1, entities.size());

        TedTalkEntity entity = entities.get(0);
        assertNull(entity.getTitle());
        assertNull(entity.getAuthor());
        assertNull(entity.getDate());
        assertEquals(0, entity.getViews());
        assertEquals(0, entity.getLikes());
        assertNull(entity.getLink());
    }
}
