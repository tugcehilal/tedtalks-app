package com.tugce.tedtalksapp.tedtalks.service;

import com.tugce.tedtalksapp.common.CsvHelper;
import com.tugce.tedtalksapp.tedtalks.exception.CsvParseException;
import com.tugce.tedtalksapp.tedtalks.model.TedTalkModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for importing and parsing CSV files containing TedTalk data.
 */
@Service
public class CsvImporterService {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH);

    /**
     * Parses the uploaded CSV file and converts it to a list of TedTalkModel objects.
     *
     * @param file the uploaded CSV file
     * @return a list of TedTalkModel objects
     * @throws CsvParseException if an error occurs during parsing
     */
    public List<TedTalkModel> parseCsv(MultipartFile file) throws CsvParseException {
        try {
            System.out.println("Parsing CSV file...");
            List<String[]> rows = CsvHelper.readCsvFile(file);
            List<TedTalkModel> tedTalks = new ArrayList<>();

            if (rows.isEmpty()) {
                throw new CsvParseException("CSV file is empty");
            }

            // Verify headers
            String[] headers = rows.get(0);
            System.out.println("Headers: " + Arrays.toString(headers));
            if (!Arrays.equals(headers, new String[]{"title", "author", "date", "views", "likes", "link"})) {
                throw new CsvParseException("Invalid CSV headers. Expected: [title, author, date, views, likes, link]");
            }

            // Process rows
            for (int i = 1; i < rows.size(); i++) {
                String[] line = rows.get(i);
                try {
                    // Log each row for debugging
                    System.out.println("Processing row: " + Arrays.toString(line));

                    // Handle numeric parsing with fallback to 0
                    long views = parseOrDefault(line[3], 0L);
                    long likes = parseOrDefault(line[4], 0L);

                    // Validate and parse the date
                    YearMonth date = parseYearMonthWithFallback(line[2]);

                    // Parse and create a TedTalkModel object
                    TedTalkModel model = new TedTalkModel(
                            line[0], // title
                            line[1], // author
                            date,    // Parsed or fallback date
                            views,   // Parsed or default views
                            likes,   // Parsed or default likes
                            line[5]  // link
                    );
                    tedTalks.add(model);
                } catch (Exception e) {
                    throw new CsvParseException("Error processing row: " + Arrays.toString(line), e);
                }
            }

            System.out.println("Number of valid rows parsed: " + tedTalks.size());
            return tedTalks;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CsvParseException("Error parsing CSV file", e);
        }
    }

    /**
     * Parses a string to a long value, or returns the default value if parsing fails.
     *
     * @param str the input string
     * @param defaultValue the default value to return if parsing fails
     * @return the parsed long value, or the default value
     */
    private long parseOrDefault(String str, long defaultValue) {
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric value: " + str + ". Defaulting to " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Parses a string to a YearMonth value, falling back to the current month or year if parts are invalid.
     *
     * @param str the input string
     * @return the parsed YearMonth value, or a fallback value with current month/year
     */
    private YearMonth parseYearMonthWithFallback(String str) {
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
}
