package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TedTalkManagementService {
    private final TedTalkRepository repository;

    public TedTalkManagementService(TedTalkRepository repository) {
        this.repository = repository;
    }

    public TedTalkModel createTedTalk(TedTalkModel model) {
        TedTalkEntity entity = mapModelToEntity(model);
        TedTalkEntity savedEntity = repository.save(entity);
        return mapEntityToModel(savedEntity);
    }

    public List<TedTalkModel> getAllTedTalks() {
        return repository.findAll().stream().map(this::mapEntityToModel).toList();
    }

    public TedTalkModel getTedTalkById(Long id) {
        TedTalkEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("TedTalk not found"));
        return mapEntityToModel(entity);
    }

    public TedTalkModel updateTedTalk(Long id, TedTalkModel model) {
        TedTalkEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("TedTalk not found"));
        entity.setTitle(model.getTitle());
        entity.setAuthor(model.getAuthor());
        entity.setDate(model.getDate());
        entity.setViews(model.getViews());
        entity.setLikes(model.getLikes());
        entity.setLink(model.getLink());
        TedTalkEntity updatedEntity = repository.save(entity);
        return mapEntityToModel(updatedEntity);
    }

    public void deleteTedTalk(Long id) {
        repository.deleteById(id);
    }

    private TedTalkEntity mapModelToEntity(TedTalkModel model) {
        return new TedTalkEntity(null, model.getTitle(), model.getAuthor(), model.getDate(), model.getViews(), model.getLikes(), model.getLink());
    }

    private TedTalkModel mapEntityToModel(TedTalkEntity entity) {
        return new TedTalkModel(entity.getTitle(), entity.getAuthor(), entity.getDate(), entity.getViews(), entity.getLikes(), entity.getLink());
    }
}
