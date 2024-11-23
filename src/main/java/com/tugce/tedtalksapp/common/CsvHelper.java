package com.tugce.tedtalksapp.common;

import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;

/**
 * A utility class for handling CSV file operations.
 */
public class CsvHelper {

    /**
     * Reads a CSV file and returns its content as a list of string arrays.
     *
     * @param file the uploaded CSV file
     * @return a list of string arrays representing each row
     * @throws Exception if an error occurs while reading the file
     */
    public static List<String[]> readCsvFile(MultipartFile file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            return reader.readAll(); // Reads all lines in the CSV file
        }
    }
}
