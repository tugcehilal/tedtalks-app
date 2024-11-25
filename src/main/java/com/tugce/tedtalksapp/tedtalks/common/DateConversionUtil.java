package com.tugce.tedtalksapp.tedtalks.common;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateConversionUtil {
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    /**
     * Parses a string to YearMonth, with fallback to the current month and year for invalid input.
     *
     * @param str the input string
     * @return a valid YearMonth object
     */
    public static YearMonth parseYearMonth(String str) {
        if (str == null || str.isEmpty()) {
            return YearMonth.now();
        }
        String[] parts = str.split(" ");
        String month = parts.length > 0 ? parts[0] : "";
        String year = parts.length > 1 ? parts[1] : "";

        int fallbackYear = YearMonth.now().getYear();
        int fallbackMonth = YearMonth.now().getMonthValue();

        try {
            if (!month.isEmpty()) {
                fallbackMonth = YearMonth.parse(month + " " + fallbackYear, YEAR_MONTH_FORMATTER).getMonthValue();
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid month value: " + month + ". Defaulting to current month.");
        }

        try {
            if (!year.isEmpty()) {
                fallbackYear = Integer.parseInt(year);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid year value: " + year + ". Defaulting to current year.");
        }

        return YearMonth.of(fallbackYear, fallbackMonth);
    }

    /**
     * Formats a YearMonth to a string in "MMMM yyyy" format.
     *
     * @param yearMonth the YearMonth object
     * @return a formatted string
     */
    public static String formatYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) {
            return "";
        }
        return yearMonth.format(YEAR_MONTH_FORMATTER);
    }
}
