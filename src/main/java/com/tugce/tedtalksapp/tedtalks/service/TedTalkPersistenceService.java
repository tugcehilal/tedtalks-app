package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import com.tugce.tedtalksapp.tedtalks.repository.TedTalkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TedTalkPersistenceService {
    private final TedTalkRepository repository;

    public TedTalkPersistenceService(TedTalkRepository repository) {
        this.repository = repository;
    }

    public void saveAll(List<TedTalkModel> models) {
        List<TedTalkEntity> entities = models.stream().map(model -> {
            TedTalkEntity entity = new TedTalkEntity();
            entity.setTitle(model.getTitle());
            entity.setAuthor(model.getAuthor());
            entity.setDate(model.getDate());
            entity.setViews(model.getViews());
            entity.setLikes(model.getLikes());
            entity.setLink(model.getLink());
            return entity;
        }).collect(Collectors.toList());

        repository.saveAll(entities);
    }
}

