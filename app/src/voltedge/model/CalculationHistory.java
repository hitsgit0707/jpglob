package voltedge.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CalculationHistory manages an in-memory collection of calculation records
 * and provides persistent local file storage.
 * 
 * Demonstrates:
 * - Collections Framework: ArrayList<HistoryRecord> for storing logs
 * - Standard File I/O: File, FileReader, FileWriter, BufferedReader, BufferedWriter
 * - Defensive Exception Handling: Corrupted records and I/O failures do not crash the app
 * - Method Overloading: addRecord(...) variations
 * - Event Listener callbacks: notifying UI observers on modification
 */
public class CalculationHistory {

    public static final String DEFAULT_FILE_NAME = "voltedge_history.txt";

    private final List<HistoryRecord> records;
    private final List<HistoryChangeListener> listeners;
    private final File storageFile;

    /**
     * Default constructor: uses DEFAULT_FILE_NAME in the current working directory.
     */
    public CalculationHistory() {
        this(new File(DEFAULT_FILE_NAME));
    }

    /**
     * Overloaded constructor: allows custom storage file location (useful for tests).
     */
    public CalculationHistory(File storageFile) {
        this.records = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.storageFile = storageFile;

        // Load existing records on instantiation
        loadFromFile();
    }

    /**
     * Overloaded Method 1: Adds an existing HistoryRecord.
     * Records in-memory and appends to the persistence file.
     */
    public synchronized void addRecord(HistoryRecord record) {
        if (record == null) {
            return;
        }
        records.add(record);
        appendToFile(record);
        notifyListeners();
    }

    /**
     * Overloaded Method 2: Creates and adds a record from individual fields.
     */
    public synchronized void addRecord(String moduleName, String inputDetails, String result) {
        addRecord(new HistoryRecord(moduleName, inputDetails, result));
    }

    /**
     * Returns an unmodifiable view of all recorded history entries.
     */
    public synchronized List<HistoryRecord> getRecords() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }

    /**
     * Returns the number of history records.
     */
    public synchronized int size() {
        return records.size();
    }

    /**
     * Clears all in-memory records and wipes the local persistence file.
     */
    public synchronized void clearHistory() {
        records.clear();
        saveAllToFile();
        notifyListeners();
    }

    /**
     * Registers a listener to be notified whenever history changes.
     */
    public void addChangeListener(HistoryChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a registered listener.
     */
    public void removeChangeListener(HistoryChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (HistoryChangeListener listener : listeners) {
            try {
                listener.onHistoryChanged();
            } catch (Exception ex) {
                System.err.println("Error notifying history listener: " + ex.getMessage());
            }
        }
    }

    // ── FILE PERSISTENCE OPERATIONS ──────────────────────────────────────────

    /**
     * Loads saved records from storageFile.
     * Gracefully handles missing files, and skips malformed or corrupted records.
     */
    public synchronized void loadFromFile() {
        if (storageFile == null || !storageFile.exists()) {
            // Missing file is normal on first run — start with empty records
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue; // Skip blank lines
                }
                try {
                    HistoryRecord record = HistoryRecord.fromFileString(trimmed);
                    records.add(record);
                } catch (IllegalArgumentException ex) {
                    // Malformed line: log warning and continue without crashing
                    System.err.println("Warning: Skipping corrupted history line #" + lineNumber + ": " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("Warning: Unable to read history file: " + ex.getMessage());
        }
    }

    /**
     * Appends a single record to the storage file.
     * Fail-safe: if disk write fails, the calculation continues working.
     */
    private synchronized void appendToFile(HistoryRecord record) {
        if (storageFile == null) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile, true))) {
            writer.write(record.toFileString());
            writer.newLine();
        } catch (IOException ex) {
            System.err.println("Warning: Failed to append to history file: " + ex.getMessage());
        }
    }

    /**
     * Rewrites all current records to the storage file (used during clear or reload).
     */
    private synchronized void saveAllToFile() {
        if (storageFile == null) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile, false))) {
            for (HistoryRecord record : records) {
                writer.write(record.toFileString());
                writer.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Warning: Failed to overwrite history file: " + ex.getMessage());
        }
    }

    public File getStorageFile() {
        return storageFile;
    }
}
