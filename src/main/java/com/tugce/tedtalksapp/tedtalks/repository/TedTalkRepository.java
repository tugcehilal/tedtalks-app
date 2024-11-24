package com.tugce.tedtalksapp.tedtalks.repository;

import com.tugce.tedtalksapp.tedtalks.entity.TedTalkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TedTalkRepository extends JpaRepository<TedTalkEntity, Long> {
}
