package com.tugce.tedtalksapp.tedtalks.dto;

import java.time.YearMonth;

/**
 * A Data Transfer Object for TED Talks.
 */
public record TedTalkDTO(
        String title,
        String author,
        String date,  // Keeping this as a String to ensure flexibility in formatting
        long views,
        long likes,
        String link
) {}

