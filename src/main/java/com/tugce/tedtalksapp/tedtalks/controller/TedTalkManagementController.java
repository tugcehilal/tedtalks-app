package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.common.DateConversionUtil;
import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.service.TedTalkManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tedtalks")
public class TedTalkManagementController {
    private final TedTalkManagementService managementService;

    public TedTalkManagementController(TedTalkManagementService managementService) {
        this.managementService = managementService;
    }

    /**
     * Creates a new TedTalk entry.
     *
     * @param tedTalkDTO the TedTalk details to be created
     * @return the created TedTalk with a 201 Created status
     */
    @PostMapping
    public ResponseEntity<TedTalkDTO> createTedTalk(@RequestBody TedTalkDTO tedTalkDTO) {
        TedTalkModel model = managementService.createTedTalk(mapDtoToModel(tedTalkDTO));
        return new ResponseEntity<>(mapModelToDto(model), HttpStatus.CREATED);
    }

    /**
     * Retrieves all TedTalk entries.
     *
     * @return a list of all TedTalks
     */
    @GetMapping
    public ResponseEntity<List<TedTalkDTO>> getAllTedTalks() {
        List<TedTalkModel> models = managementService.getAllTedTalks();
        return ResponseEntity.ok(models.stream().map(this::mapModelToDto).toList());
    }

    /**
     * Retrieves a specific TedTalk entry by its ID.
     *
     * @param id the ID of the TedTalk
     * @return the TedTalk details if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TedTalkDTO> getTedTalkById(@PathVariable Long id) {
        TedTalkModel model = managementService.getTedTalkById(id);
        return ResponseEntity.ok(mapModelToDto(model));
    }

    /**
     * Updates a specific TedTalk entry by its ID.
     *
     * @param id the ID of the TedTalk to be updated
     * @param tedTalkDTO the updated TedTalk details
     * @return the updated TedTalk
     */
    @PutMapping("/{id}")
    public ResponseEntity<TedTalkDTO> updateTedTalk(@PathVariable Long id, @RequestBody TedTalkDTO tedTalkDTO) {
        TedTalkModel model = managementService.updateTedTalk(id, mapDtoToModel(tedTalkDTO));
        return ResponseEntity.ok(mapModelToDto(model));
    }

    /**
     * Deletes a specific TedTalk entry by its ID.
     *
     * @param id the ID of the TedTalk to be deleted
     * @return a 204 No Content status if deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTedTalk(@PathVariable Long id) {
        managementService.deleteTedTalk(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a TedTalkDTO object to a TedTalkModel object.
     *
     * @param dto the DTO object to be converted
     * @return the converted Model object
     */
    private TedTalkModel mapDtoToModel(TedTalkDTO dto) {
        return new TedTalkModel(
                dto.getTitle(),
                dto.getAuthor(),
                DateConversionUtil.parseYearMonth(dto.getDate()),
                dto.getViews(),
                dto.getLikes(),
                dto.getLink()
        );
    }

    /**
     * Converts a TedTalkModel object to a TedTalkDTO object.
     *
     * @param model the Model object to be converted
     * @return the converted DTO object
     */
    private TedTalkDTO mapModelToDto(TedTalkModel model) {
        return new TedTalkDTO(
                model.getTitle(),
                model.getAuthor(),
                DateConversionUtil.formatYearMonth(model.getDate()),
                model.getViews(),
                model.getLikes(),
                model.getLink()
        );
    }

    /**
     * Retrieves the most influential TedTalk speakers based on their influence score.
     * Influence is calculated based on views and likes.
     *
     * @return a list of influential speakers and their scores
     */
    @GetMapping("/influential-speakers")
    public List<Map.Entry<String, Long>> getMostInfluentialSpeakers() {
        return managementService.findMostInfluentialSpeakers();
    }


    /**
     * Retrieves the most influential TedTalks for each year based on their influence score.
     * Influence is calculated based on views and likes.
     *
     * @return a map of years to the most influential TedTalks
     */
    @GetMapping("/most-influential-tedtalks-per-year")
    public Map<Integer, Optional<TedTalkDTO>> getMostInfluentialTedTalksPerYear() {
        return managementService.findMostInfluentialTedTalkPerYear().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Year as the key
                        entry -> entry.getValue().map(this::mapEntityToDto) // Map entity to DTO if present
                ));
    }

    /**
     * Helper method to map a TedTalkEntity to TedTalkDTO.
     *
     * @param entity The TedTalkEntity to map.
     * @return The mapped TedTalkDTO.
     */
    private TedTalkDTO mapEntityToDto(TedTalkEntity entity) {
        return new TedTalkDTO(
                entity.getTitle(),
                entity.getAuthor(),
                DateConversionUtil.formatYearMonth(entity.getDate()),
                entity.getViews(),
                entity.getLikes(),
                entity.getLink()
        );
    }



}
