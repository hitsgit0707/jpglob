package voltedge.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * HistoryRecord represents an immutable log of a successfully completed calculation.
 * 
 * Demonstrates:
 * - Encapsulation: private final fields with public getters
 * - Method Overloading: multiple constructors for creation vs deserialization
 * - Method Overriding: toString(), equals(), hashCode()
 * - Robust parsing for file persistence
 */
public class HistoryRecord {

    public static final String DELIMITER = "\t"; // Tab-separated fields for file storage
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String timestamp;
    private final String moduleName;
    private final String inputDetails;
    private final String result;

    /**
     * Overloaded Constructor 1: Creates a new record with current timestamp.
     * Used when a calculation is performed in the UI.
     */
    public HistoryRecord(String moduleName, String inputDetails, String result) {
        this(LocalDateTime.now().format(FORMATTER), moduleName, inputDetails, result);
    }

    /**
     * Overloaded Constructor 2: Creates a record with an explicit timestamp.
     * Used when deserializing records loaded from a storage file.
     */
    public HistoryRecord(String timestamp, String moduleName, String inputDetails, String result) {
        this.timestamp = (timestamp != null && !timestamp.trim().isEmpty())
                ? timestamp.trim()
                : LocalDateTime.now().format(FORMATTER);
        this.moduleName = moduleName != null ? moduleName.trim() : "Unknown Module";
        this.inputDetails = inputDetails != null ? inputDetails.trim() : "";
        this.result = result != null ? result.trim() : "";
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getInputDetails() {
        return inputDetails;
    }

    public String getResult() {
        return result;
    }

    /**
     * Serializes this record into a single clean line for file storage.
     * Format: timestamp\tmoduleName\tinputDetails\tresult
     */
    public String toFileString() {
        // Sanitize any accidental newlines or delimiters within strings
        String safeInput = inputDetails.replace('\t', ' ').replace('\n', ' ');
        String safeResult = result.replace('\t', ' ').replace('\n', ' ');
        return timestamp + DELIMITER + moduleName + DELIMITER + safeInput + DELIMITER + safeResult;
    }

    /**
     * Parses a line from the storage file into a HistoryRecord.
     * Defensive design: throws IllegalArgumentException if the line is malformed.
     */
    public static HistoryRecord fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot parse empty line.");
        }
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Malformed record: expected 4 fields, found " + parts.length);
        }
        return new HistoryRecord(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s -> %s", timestamp, moduleName, inputDetails, result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryRecord that = (HistoryRecord) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(moduleName, that.moduleName) &&
               Objects.equals(inputDetails, that.inputDetails) &&
               Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, moduleName, inputDetails, result);
    }
}
