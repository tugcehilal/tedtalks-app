package com.tugce.tedtalksapp.tedtalks.controller;

import com.tugce.tedtalksapp.tedtalks.common.DateConversionUtil;
import com.tugce.tedtalksapp.tedtalks.dto.TedTalkDTO;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.service.TedTalkManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tedtalks")
public class TedTalkManagementController {
    private final TedTalkManagementService managementService;

    public TedTalkManagementController(TedTalkManagementService managementService) {
        this.managementService = managementService;
    }

    @PostMapping
    public ResponseEntity<TedTalkDTO> createTedTalk(@RequestBody TedTalkDTO tedTalkDTO) {
        TedTalkModel model = managementService.createTedTalk(mapDtoToModel(tedTalkDTO));
        return new ResponseEntity<>(mapModelToDto(model), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TedTalkDTO>> getAllTedTalks() {
        List<TedTalkModel> models = managementService.getAllTedTalks();
        return ResponseEntity.ok(models.stream().map(this::mapModelToDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TedTalkDTO> getTedTalkById(@PathVariable Long id) {
        TedTalkModel model = managementService.getTedTalkById(id);
        return ResponseEntity.ok(mapModelToDto(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TedTalkDTO> updateTedTalk(@PathVariable Long id, @RequestBody TedTalkDTO tedTalkDTO) {
        TedTalkModel model = managementService.updateTedTalk(id, mapDtoToModel(tedTalkDTO));
        return ResponseEntity.ok(mapModelToDto(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTedTalk(@PathVariable Long id) {
        managementService.deleteTedTalk(id);
        return ResponseEntity.noContent().build();
    }

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

}
