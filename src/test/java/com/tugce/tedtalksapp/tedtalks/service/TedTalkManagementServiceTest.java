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
import java.util.Map;
import java.util.Optional;


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

    @Test
    void testFindMostInfluentialSpeakers_withWeightedLikes() {
        // Arrange
        repository.save(new TedTalkEntity(null, "Talk 1", "Author 1", YearMonth.of(2022, 1), 1000, 500, "link1"));
        repository.save(new TedTalkEntity(null, "Talk 2", "Author 2", YearMonth.of(2022, 2), 2000, 300, "link2"));
        repository.save(new TedTalkEntity(null, "Talk 3", "Author 1", YearMonth.of(2021, 3), 3000, 200, "link3"));

        // Act
        List<Map.Entry<String, Long>> influentialSpeakers = service.findMostInfluentialSpeakers();

        // Assert
        assertEquals(2, influentialSpeakers.size());

        // Author 1 influence: (1000 + 2*500) + (3000 + 2*200) = 1000 + 1000 + 3000 + 400 = 5400
        assertEquals("Author 1", influentialSpeakers.get(0).getKey());
        assertEquals(5400, influentialSpeakers.get(0).getValue());

        // Author 2 influence: (2000 + 2*300) = 2000 + 600 = 2600
        assertEquals("Author 2", influentialSpeakers.get(1).getKey());
        assertEquals(2600, influentialSpeakers.get(1).getValue());
    }

    @Test
    void testFindMostInfluentialTedTalkPerYear() {
        // Arrange
        repository.save(new TedTalkEntity(null, "Talk 1", "Author 1", YearMonth.of(2022, 1), 1000, 500, "link1")); // Influence = 2000
        repository.save(new TedTalkEntity(null, "Talk 2", "Author 2", YearMonth.of(2022, 2), 3000, 200, "link2")); // Influence = 3400
        repository.save(new TedTalkEntity(null, "Talk 3", "Author 3", YearMonth.of(2021, 3), 2500, 300, "link3")); // Influence = 3100
        repository.save(new TedTalkEntity(null, "Talk 4", "Author 4", YearMonth.of(2021, 4), 2000, 400, "link4")); // Influence = 2800

        // Act
        Map<Integer, Optional<TedTalkEntity>> result = service.findMostInfluentialTedTalkPerYear();

        // Assert
        assertEquals(2, result.size()); // Two years: 2021 and 2022

        // Assert for year 2022
        Optional<TedTalkEntity> mostInfluential2022 = result.get(2022);
        assertTrue(mostInfluential2022.isPresent());
        assertEquals("Talk 2", mostInfluential2022.get().getTitle());
        assertEquals("Author 2", mostInfluential2022.get().getAuthor());
        assertEquals(3400, mostInfluential2022.get().getViews() + 2 * mostInfluential2022.get().getLikes()); // Influence = 3400

        // Assert for year 2021
        Optional<TedTalkEntity> mostInfluential2021 = result.get(2021);
        assertTrue(mostInfluential2021.isPresent());
        assertEquals("Talk 3", mostInfluential2021.get().getTitle());
        assertEquals("Author 3", mostInfluential2021.get().getAuthor());
        assertEquals(3100, mostInfluential2021.get().getViews() + 2 * mostInfluential2021.get().getLikes()); // Influence = 3100
    }
}
