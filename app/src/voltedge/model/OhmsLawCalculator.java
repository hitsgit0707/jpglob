package voltedge.model;

/**
 * OhmsLawCalculator encapsulates the core physical formulas of Ohm's Law:
 * V = I * R, I = V / R, R = V / I.
 * 
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Method design and separation of logic from GUI
 * - Defensive programming & Exception throwing
 */
public class OhmsLawCalculator extends CircuitCalculator {

    public OhmsLawCalculator() {
        super("Ohm's Law", "V = I · R");
    }

    /**
     * Calculates Voltage (V) = Current (I) * Resistance (R).
     * @param current Current in Amperes (A)
     * @param resistance Resistance in Ohms (Ω), must be >= 0
     * @return Potential difference in Volts (V)
     */
    public double calculateVoltage(double current, double resistance) {
        if (resistance < 0) {
            throw new IllegalArgumentException("Resistance cannot be negative in passive circuits.");
        }
        return current * resistance;
    }

    /**
     * Calculates Current (I) = Voltage (V) / Resistance (R).
     * @param voltage Voltage in Volts (V)
     * @param resistance Resistance in Ohms (Ω), must be > 0
     * @return Current in Amperes (A)
     */
    public double calculateCurrent(double voltage, double resistance) {
        if (resistance < 0) {
            throw new IllegalArgumentException("Resistance cannot be negative in passive circuits.");
        }
        if (resistance == 0.0) {
            throw new ArithmeticException("Resistance cannot be zero (Short circuit condition causes infinite current).");
        }
        return voltage / resistance;
    }

    /**
     * Calculates Resistance (R) = Voltage (V) / Current (I).
     * @param voltage Voltage in Volts (V)
     * @param current Current in Amperes (A), cannot be 0
     * @return Resistance in Ohms (Ω)
     */
    public double calculateResistance(double voltage, double current) {
        if (current == 0.0) {
            throw new ArithmeticException("Current cannot be zero (Open circuit condition).");
        }
        double r = voltage / current;
        if (r < 0) {
            throw new IllegalArgumentException("Calculated resistance resulted in negative value. Check polarities.");
        }
        return r;
    }
}
