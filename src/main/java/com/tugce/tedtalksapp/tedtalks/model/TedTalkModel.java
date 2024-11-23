package com.tugce.tedtalksapp.tedtalks.model;

import java.time.YearMonth;

/**
 * Represents a TED Talk with key attributes such as title, author, date, views, likes, and link.
 */
public record TedTalkModel(
        String title,
        String author,
        YearMonth date,
        long views,
        long likes,
        String link
) {}
