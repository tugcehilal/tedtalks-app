package com.tugce.tedtalksapp.tedtalks.common;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DateConversionUtilTest {

    @Test
    void testParseYearMonth_validDate() {
        String validDate = "January 2022";
        YearMonth result = DateConversionUtil.parseYearMonth(validDate);
        assertEquals(YearMonth.of(2022, 1), result, "The parsed YearMonth should match the input date.");
    }

    @Test
    void testParseYearMonth_invalidMonthValidYear() {
        String invalidMonth = "Invalid 2022"; // Invalid month but valid year
        YearMonth result = DateConversionUtil.parseYearMonth(invalidMonth);

        // Expected: current month with the provided year
        YearMonth expected = YearMonth.of(2022, YearMonth.now().getMonthValue());
        assertEquals(expected, result, "For an invalid month but valid year, it should use the current month and the provided year.");
    }


    @Test
    void testParseYearMonth_invalidYearValidMonth() {
        String input = "January InvalidYear"; // Valid month but invalid year
        YearMonth result = DateConversionUtil.parseYearMonth(input);

        // Expected: provided month (January) with the current year
        YearMonth expected = YearMonth.of(YearMonth.now().getYear(), 1); // Current year and January
        assertEquals(expected, result, "For an invalid year but valid month, it should use the current year and the provided month.");
    }


    @Test
    void testParseYearMonth_nullInput() {
        YearMonth result = DateConversionUtil.parseYearMonth(null);
        YearMonth expected = YearMonth.now();
        assertEquals(expected, result, "Null input should default to the current month and year.");
    }

    @Test
    void testParseYearMonth_emptyInput() {
        YearMonth result = DateConversionUtil.parseYearMonth("");
        YearMonth expected = YearMonth.now();
        assertEquals(expected, result, "Empty input should default to the current month and year.");
    }

    @Test
    void testParseYearMonth_partialDate() {
        String partialDate = "January";
        YearMonth result = DateConversionUtil.parseYearMonth(partialDate);
        YearMonth expected = YearMonth.of(YearMonth.now().getYear(), 1); // Defaults to January of the current year
        assertEquals(expected, result, "For a valid month without a year, it should use the current year.");
    }

    @Test
    void testFormatYearMonth_validYearMonth() {
        YearMonth yearMonth = YearMonth.of(2022, 1);
        String result = DateConversionUtil.formatYearMonth(yearMonth);
        assertEquals("January 2022", result, "The formatted string should match 'MMMM yyyy' format.");
    }

    @Test
    void testFormatYearMonth_nullYearMonth() {
        String result = DateConversionUtil.formatYearMonth(null);
        assertEquals("", result, "Null YearMonth should return an empty string.");
    }
}
