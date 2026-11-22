package voltedge.model;

import java.util.ArrayList;
import java.util.List;

/**
 * CapacitorCalculator computes equivalent capacitance for multiple capacitors
 * arranged in Parallel or Series configurations.
 *
 * Key distinction from resistors — the formulas are INVERTED:
 *   Parallel capacitors ADD directly (like series resistors).
 *   Series   capacitors use reciprocal sum (like parallel resistors).
 *
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Collections: ArrayList<Double> for a variable number of capacitor values
 * - Separation of parsing, validation, and calculation into distinct methods
 * - Descriptive exception messages for every failure mode
 *
 * Formulas:
 *   Parallel: C_total = C1 + C2 + ... + Cn
 *   Series:   1/C_total = 1/C1 + 1/C2 + ... + 1/Cn
 */
public class CapacitorCalculator extends CircuitCalculator {

    public CapacitorCalculator() {
        super("Capacitor Networks", "Parallel: Ceq = ΣCn | Series: 1/Ceq = Σ(1/Cn)");
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates that all capacitor values in the list are strictly positive.
     * Zero capacitance is physically invalid; negative capacitance is impossible.
     *
     * @param capacitors List of capacitance values in Farads
     * @throws IllegalArgumentException if list is null/empty or any value <= 0
     */
    private void validateCapacitors(List<Double> capacitors) {
        if (capacitors == null || capacitors.isEmpty()) {
            throw new IllegalArgumentException("Please enter at least one capacitor value.");
        }
        for (int i = 0; i < capacitors.size(); i++) {
            double c = capacitors.get(i);
            if (c <= 0) {
                throw new IllegalArgumentException(
                    "C" + (i + 1) + " = " + c + " F is invalid. All capacitor values must be greater than zero."
                );
            }
        }
    }

    // ── Calculations ──────────────────────────────────────────────────────────

    /**
     * Calculates total PARALLEL capacitance.
     * Capacitors in parallel: C_total = C1 + C2 + ... + Cn
     * (Charge storage adds directly — each capacitor connects across the same voltage.)
     *
     * @param capacitors List of capacitance values in Farads (all must be > 0)
     * @return Equivalent parallel capacitance in Farads
     */
    public double calculateParallel(List<Double> capacitors) {
        validateCapacitors(capacitors);
        double total = 0.0;
        for (double c : capacitors) {
            total += c;
        }
        return total;
    }

    /**
     * Calculates total SERIES capacitance.
     * Capacitors in series: 1/C_total = 1/C1 + 1/C2 + ... + 1/Cn
     * (The equivalent capacitance is always less than the smallest individual value.)
     *
     * @param capacitors List of capacitance values in Farads (all must be > 0)
     * @return Equivalent series capacitance in Farads
     */
    public double calculateSeries(List<Double> capacitors) {
        validateCapacitors(capacitors);
        double reciprocalSum = 0.0;
        for (double c : capacitors) {
            reciprocalSum += 1.0 / c;
        }
        // reciprocalSum > 0 is guaranteed because all c > 0
        return 1.0 / reciprocalSum;
    }

    // ── Input Parsing ─────────────────────────────────────────────────────────

    /**
     * Parses a comma-separated string of capacitor values into a List<Double>.
     * Whitespace around each token is trimmed. Blank tokens and non-numeric
     * entries are rejected with specific error messages.
     *
     * @param input Raw text from the user, e.g. "100e-6, 220e-6, 47e-6"
     * @return Parsed list of capacitance values in Farads
     * @throws IllegalArgumentException for empty input, blank tokens, or non-numeric tokens
     */
    public List<Double> parseCapacitors(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input is empty. Enter capacitor values separated by commas.");
        }
        String[] tokens = input.split(",");
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                throw new IllegalArgumentException(
                    "Entry " + (i + 1) + " is blank. Remove any extra commas."
                );
            }
            try {
                values.add(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "\"" + token + "\" is not a valid number. Enter numeric values only."
                );
            }
        }
        return values;
    }
}
