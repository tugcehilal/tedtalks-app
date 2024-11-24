package com.tugce.tedtalksapp.tedtalks.entity;

import com.tugce.tedtalksapp.tedtalks.converter.YearMonthConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor
public class TedTalkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    @Convert(converter = YearMonthConverter.class)
    private YearMonth date;
    private long views;
    private long likes;
    private String link;
}
