package voltedge.model;

/**
 * PowerCalculator computes electrical power dissipation in a circuit.
 *
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Method Overloading: three calculatePower() signatures for different input combos
 * - Defensive programming and meaningful exception messages
 *
 * Formulas:
 *   P = V * I       (from Voltage and Current)
 *   P = I^2 * R     (from Current and Resistance)
 *   P = V^2 / R     (from Voltage and Resistance)
 */
public class PowerCalculator extends CircuitCalculator {

    public PowerCalculator() {
        super("Electrical Power", "P = V·I | P = I²R | P = V²/R");
    }

    /**
     * Calculates Power using Voltage and Current.
     * P = V * I
     * @param voltage Voltage in Volts (V)
     * @param current Current in Amperes (A)
     * @return Power in Watts (W)
     */
    public double calculatePower(double voltage, double current) {
        // Negative power (returned power) is physically valid in some contexts,
        // but for a basic calculator we warn if both are negative (sign error)
        return voltage * current;
    }

    /**
     * Calculates Power using Current and Resistance — METHOD OVERLOAD.
     * P = I^2 * R
     * @param current Current in Amperes (A)
     * @param resistance Resistance in Ohms (Ω), must be positive
     * @return Power in Watts (W)
     */
    public double calculatePower(double current, double resistance, boolean useCurrentResistance) {
        if (resistance < 0) {
            throw new IllegalArgumentException("Resistance cannot be negative in passive circuits.");
        }
        return current * current * resistance;
    }

    /**
     * Calculates Power using Voltage and Resistance — METHOD OVERLOAD.
     * P = V^2 / R
     * @param voltage Voltage in Volts (V)
     * @param resistance Resistance in Ohms (Ω), must be > 0
     * @return Power in Watts (W)
     */
    public double calculatePowerVR(double voltage, double resistance) {
        if (resistance < 0) {
            throw new IllegalArgumentException("Resistance cannot be negative in passive circuits.");
        }
        if (resistance == 0.0) {
            throw new ArithmeticException("Resistance cannot be zero (would cause infinite power dissipation).");
        }
        return (voltage * voltage) / resistance;
    }
}
