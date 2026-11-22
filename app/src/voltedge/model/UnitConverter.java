package voltedge.model;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * UnitConverter manages conversion across standard electrical engineering units
 * using base-unit scale factors.
 * 
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Java Enums with fields and methods
 * - Collections: LinkedHashMap for preserving ordered unit sequences
 * - Defensive validation (NaN, Infinity, category-specific negative value rules)
 */
public class UnitConverter extends CircuitCalculator {

    private static final DecimalFormat CONVERTER_DECIMAL_FORMAT = new DecimalFormat("#,##0.########");
    private static final DecimalFormat CONVERTER_SCIENTIFIC_FORMAT = new DecimalFormat("0.########E0");

    public enum Category {
        VOLTAGE("Voltage", true, createUnits(
            "µV", 1e-6,
            "mV", 1e-3,
            "V", 1.0,
            "kV", 1e3
        )),
        CURRENT("Current", true, createUnits(
            "µA", 1e-6,
            "mA", 1e-3,
            "A", 1.0,
            "kA", 1e3
        )),
        RESISTANCE("Resistance", false, createUnits(
            "mΩ", 1e-3,
            "Ω", 1.0,
            "kΩ", 1e3,
            "MΩ", 1e6
        )),
        CAPACITANCE("Capacitance", false, createUnits(
            "pF", 1e-12,
            "nF", 1e-9,
            "µF", 1e-6,
            "mF", 1e-3,
            "F", 1.0
        )),
        POWER("Power", true, createUnits(
            "µW", 1e-6,
            "mW", 1e-3,
            "W", 1.0,
            "kW", 1e3,
            "MW", 1e6
        )),
        FREQUENCY("Frequency", false, createUnits(
            "Hz", 1.0,
            "kHz", 1e3,
            "MHz", 1e6,
            "GHz", 1e9
        ));

        private final String displayName;
        private final boolean allowNegative;
        private final Map<String, Double> units;

        Category(String displayName, boolean allowNegative, Map<String, Double> units) {
            this.displayName = displayName;
            this.allowNegative = allowNegative;
            this.units = Collections.unmodifiableMap(units);
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isNegativeAllowed() {
            return allowNegative;
        }

        public Map<String, Double> getUnits() {
            return units;
        }

        public Set<String> getUnitNames() {
            return units.keySet();
        }

        public static Category fromDisplayName(String name) {
            for (Category cat : values()) {
                if (cat.displayName.equalsIgnoreCase(name)) {
                    return cat;
                }
            }
            throw new IllegalArgumentException("Unknown conversion category: " + name);
        }
    }

    private static Map<String, Double> createUnits(Object... args) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            map.put((String) args[i], (Double) args[i + 1]);
        }
        return map;
    }

    public UnitConverter() {
        super("Unit Converter", "Target = Value × (FromFactor / ToFactor)");
    }

    /**
     * Converts a value from one unit to another within a specified category.
     * 
     * @param category The physical measurement category
     * @param fromUnit The unit symbol of the source value
     * @param toUnit The unit symbol of the destination value
     * @param value The numerical quantity to convert
     * @return The converted value
     */
    public double convert(Category category, String fromUnit, String toUnit, double value) {
        if (category == null) {
            throw new IllegalArgumentException("Conversion category cannot be null.");
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Value cannot be NaN or Infinite.");
        }
        if (!category.isNegativeAllowed() && value < 0) {
            throw new IllegalArgumentException(category.getDisplayName() + " cannot be negative in passive physical domains.");
        }

        Map<String, Double> units = category.getUnits();
        Double fromFactor = units.get(fromUnit);
        Double toFactor = units.get(toUnit);

        if (fromFactor == null) {
            throw new IllegalArgumentException("Unknown unit \"" + fromUnit + "\" in category " + category.getDisplayName());
        }
        if (toFactor == null) {
            throw new IllegalArgumentException("Unknown unit \"" + toUnit + "\" in category " + category.getDisplayName());
        }

        // Shortcut for identical units
        if (fromUnit.equals(toUnit)) {
            return value;
        }

        // Convert input -> base unit -> target unit
        double baseValue = value * fromFactor;
        return baseValue / toFactor;
    }

    /**
     * Returns the human-readable relationship between 1 unit of fromUnit in terms of toUnit.
     * E.g.: "1 kV = 1,000,000 mV"
     */
    public String getConversionRelation(Category category, String fromUnit, String toUnit) {
        double ratio = convert(category, fromUnit, toUnit, 1.0);
        return String.format("1 %s = %s %s", fromUnit, formatConvertedValue(ratio), toUnit);
    }

    /**
     * Formats a converted number without losing reasonable precision.
     */
    public static String formatConvertedValue(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return "Infinity";
        }
        double abs = Math.abs(value);
        if (abs != 0 && (abs >= 100_000_000 || abs < 0.0000001)) {
            return CONVERTER_SCIENTIFIC_FORMAT.format(value);
        }
        return CONVERTER_DECIMAL_FORMAT.format(value);
    }
}
