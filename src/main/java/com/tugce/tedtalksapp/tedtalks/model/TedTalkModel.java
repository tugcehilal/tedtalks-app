package com.tugce.tedtalksapp.tedtalks.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;

/**
 * Represents a TED Talk with key attributes such as title, author, date, views, likes, and link.
 */
@Data
@AllArgsConstructor
public class TedTalkModel {
    private String title;
    private String author;
    private YearMonth date;
    private long views;
    private long likes;
    private String link;
}
