package voltedge.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ResistorCalculator computes equivalent resistance for multiple resistors
 * arranged in Series or Parallel configurations.
 *
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Collections: ArrayList<Double> for a variable number of resistor values
 * - Method design: separate methods for series and parallel logic
 * - Defensive input validation with descriptive exceptions
 *
 * Formulas:
 *   Series:   R_total = R1 + R2 + ... + Rn
 *   Parallel: 1/R_total = 1/R1 + 1/R2 + ... + 1/Rn
 */
public class ResistorCalculator extends CircuitCalculator {

    public ResistorCalculator() {
        super("Resistor Networks", "Series: Req = ΣRn | Parallel: 1/Req = Σ(1/Rn)");
    }

    /**
     * Validates a list of resistor values before calculation.
     * All values must be positive (> 0). Zero is not a valid resistance.
     *
     * @param resistors List of resistance values in Ohms
     * @throws IllegalArgumentException if list is empty, or any value <= 0
     */
    private void validateResistors(List<Double> resistors) {
        if (resistors == null || resistors.isEmpty()) {
            throw new IllegalArgumentException("Please enter at least one resistor value.");
        }
        for (int i = 0; i < resistors.size(); i++) {
            double r = resistors.get(i);
            if (r <= 0) {
                throw new IllegalArgumentException(
                    "R" + (i + 1) + " = " + r + " Ω is invalid. All resistor values must be greater than zero."
                );
            }
        }
    }

    /**
     * Calculates total series resistance.
     * R_total = R1 + R2 + ... + Rn
     *
     * @param resistors List of resistance values in Ohms (all must be > 0)
     * @return Equivalent series resistance in Ohms
     */
    public double calculateSeries(List<Double> resistors) {
        validateResistors(resistors);
        double total = 0.0;
        for (double r : resistors) {
            total += r;
        }
        return total;
    }

    /**
     * Calculates total parallel resistance.
     * 1/R_total = 1/R1 + 1/R2 + ... + 1/Rn
     *
     * @param resistors List of resistance values in Ohms (all must be > 0)
     * @return Equivalent parallel resistance in Ohms
     */
    public double calculateParallel(List<Double> resistors) {
        validateResistors(resistors);
        double reciprocalSum = 0.0;
        for (double r : resistors) {
            reciprocalSum += 1.0 / r;  // sum of conductances
        }
        // reciprocalSum > 0 is guaranteed because all r > 0
        return 1.0 / reciprocalSum;
    }

    /**
     * Parses a comma-separated string of resistor values into a List<Double>.
     * Trims whitespace from each token. Rejects blank or non-numeric tokens.
     *
     * @param input Raw text from the user, e.g. "10, 20, 30"
     * @return Parsed list of resistance values
     * @throws IllegalArgumentException if any token is non-numeric or blank
     */
    public List<Double> parseResistors(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input is empty. Enter resistor values separated by commas.");
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
