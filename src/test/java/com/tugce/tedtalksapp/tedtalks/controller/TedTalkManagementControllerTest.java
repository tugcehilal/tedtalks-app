package com.tugce.tedtalksapp.tedtalks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TedTalkManagementControllerTest {

    private final WebApplicationContext webApplicationContext;
    private final TedTalkRepository repository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public TedTalkManagementControllerTest(WebApplicationContext webApplicationContext,
                                           TedTalkRepository repository) {
        this.webApplicationContext = webApplicationContext;
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        repository.deleteAll(); // Clear the repository before each test
    }

    @Test
    void testCreateTedTalk() throws Exception {
        // Arrange
        TedTalkDTO dto = new TedTalkDTO("Title", "Author", "January 2022", 1000, 500, "link");

        // Act & Assert
        mockMvc.perform(post("/api/tedtalks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.date").value("January 2022"))
                .andExpect(jsonPath("$.views").value(1000))
                .andExpect(jsonPath("$.likes").value(500))
                .andExpect(jsonPath("$.link").value("link"));

        // Verify the data in the repository
        List<TedTalkEntity> entities = repository.findAll();
        assertEquals(1, entities.size());
        TedTalkEntity entity = entities.get(0);
        assertEquals("Title", entity.getTitle());
        assertEquals("Author", entity.getAuthor());
        assertEquals(YearMonth.of(2022, 1), entity.getDate());
        assertEquals(1000, entity.getViews());
        assertEquals(500, entity.getLikes());
        assertEquals("link", entity.getLink());
    }

    @Test
    void testGetAllTedTalks() throws Exception {
        // Arrange
        repository.save(new TedTalkEntity(null, "Title1", "Author1", YearMonth.of(2022, 1), 1000, 500, "link1"));
        repository.save(new TedTalkEntity(null, "Title2", "Author2", YearMonth.of(2023, 2), 2000, 1000, "link2"));

        // Act & Assert
        mockMvc.perform(get("/api/tedtalks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));
    }

    @Test
    void testGetTedTalkById() throws Exception {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Title", "Author", YearMonth.of(2022, 1), 1000, 500, "link"));

        // Act & Assert
        mockMvc.perform(get("/api/tedtalks/" + entity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.date").value("January 2022"));
    }

    @Test
    void testUpdateTedTalk() throws Exception {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Old Title", "Old Author", YearMonth.of(2022, 1), 1000, 500, "oldLink"));
        TedTalkDTO updatedDto = new TedTalkDTO("New Title", "New Author", "February 2023", 2000, 1000, "newLink");

        // Act & Assert
        mockMvc.perform(put("/api/tedtalks/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.author").value("New Author"))
                .andExpect(jsonPath("$.date").value("February 2023"));

        // Verify the data in the repository
        TedTalkEntity updatedEntity = repository.findById(entity.getId()).orElseThrow();
        assertEquals("New Title", updatedEntity.getTitle());
        assertEquals("New Author", updatedEntity.getAuthor());
        assertEquals(YearMonth.of(2023, 2), updatedEntity.getDate());
        assertEquals(2000, updatedEntity.getViews());
        assertEquals(1000, updatedEntity.getLikes());
        assertEquals("newLink", updatedEntity.getLink());
    }

    @Test
    void testDeleteTedTalk() throws Exception {
        // Arrange
        TedTalkEntity entity = repository.save(new TedTalkEntity(null, "Title", "Author", YearMonth.of(2022, 1), 1000, 500, "link"));

        // Act & Assert
        mockMvc.perform(delete("/api/tedtalks/" + entity.getId()))
                .andExpect(status().isNoContent());

        // Verify the data in the repository
        assertEquals(0, repository.findAll().size());
    }
}
