package voltedge.model;

import java.text.DecimalFormat;

/**
 * CalculationUtils provides shared mathematical formatting and validation helpers.
 * 
 * Demonstrates:
 * - Static utility class pattern
 * - Number formatting with DecimalFormat
 */
public final class CalculationUtils {

    private static final DecimalFormat STANDARD_FORMAT = new DecimalFormat("#,##0.####");
    private static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.####E0");

    private CalculationUtils() {}

    /**
     * Formats a double value cleanly. Uses scientific notation for very large (>1e6)
     * or very small (<1e-4, non-zero) values, and standard decimal format otherwise.
     */
    public static String formatValue(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return "Infinity";
        }
        double abs = Math.abs(value);
        if (abs != 0 && (abs >= 1_000_000 || abs < 0.0001)) {
            return SCIENTIFIC_FORMAT.format(value);
        }
        return STANDARD_FORMAT.format(value);
    }

    /**
     * Formats a value with its engineering unit symbol.
     */
    public static String formatWithUnit(double value, String unit) {
        return formatValue(value) + " " + unit;
    }
}
