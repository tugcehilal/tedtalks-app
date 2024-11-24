package com.tugce.tedtalksapp.tedtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) for TED Talk data.
 */
@Data
@AllArgsConstructor
public class TedTalkDTO {
    private String title;
    private String author;
    private String date; // Keep this as a formatted string like "December 2021"
    private long views;
    private long likes;
    private String link;
}
