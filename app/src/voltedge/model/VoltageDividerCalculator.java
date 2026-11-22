package voltedge.model;

/**
 * VoltageDividerCalculator computes the output voltage and additional
 * derived quantities for a standard two-resistor voltage divider network.
 *
 * Circuit topology (unloaded):
 *
 *   Vin ──┬── R1 ──┬── Vout
 *         │        │
 *        GND      R2
 *                  │
 *                 GND
 *
 * Demonstrates:
 * - Inheritance (extends CircuitCalculator)
 * - Encapsulation: a result is returned as a plain inner class (VoltageDividerResult)
 *   so the UI receives all derived values in one structured object
 * - Defensive validation with descriptive exceptions
 *
 * Formulas:
 *   Vout    = Vin * R2 / (R1 + R2)
 *   VR1     = Vin - Vout              (voltage drop across R1)
 *   VR2     = Vout                    (voltage drop across R2 == Vout)
 *   Current = Vin / (R1 + R2)         (same current through both resistors)
 */
public class VoltageDividerCalculator extends CircuitCalculator {

    public VoltageDividerCalculator() {
        super("Voltage Divider", "Vout = Vin × R2 / (R1 + R2)");
    }

    // ── Inner result class ────────────────────────────────────────────────────

    /**
     * Holds all derived quantities from a single voltage divider calculation.
     * Using a result object avoids multiple method calls for the same input set
     * and keeps the UI code concise.
     */
    public static class VoltageDividerResult {
        public final double vOut;       // Output voltage across R2 (V)
        public final double vDropR1;    // Voltage drop across R1 (V)
        public final double vDropR2;    // Voltage drop across R2 == vOut (V)
        public final double current;    // Series current through both resistors (A)

        public VoltageDividerResult(double vOut, double vDropR1, double vDropR2, double current) {
            this.vOut    = vOut;
            this.vDropR1 = vDropR1;
            this.vDropR2 = vDropR2;
            this.current = current;
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates individual resistor and voltage inputs.
     *
     * @param vin  Input voltage in Volts
     * @param r1   Upper resistor value in Ohms
     * @param r2   Lower resistor value in Ohms
     * @throws IllegalArgumentException for negative voltages or non-positive resistors
     * @throws ArithmeticException if R1 + R2 == 0 (impossible given validation but kept explicit)
     */
    private void validate(double vin, double r1, double r2) {
        if (vin < 0) {
            throw new IllegalArgumentException("Input voltage (Vin) cannot be negative.");
        }
        if (r1 <= 0) {
            throw new IllegalArgumentException("R1 must be greater than zero (positive resistance required).");
        }
        if (r2 <= 0) {
            throw new IllegalArgumentException("R2 must be greater than zero (positive resistance required).");
        }
        // Guard: R1 + R2 == 0 is impossible if both are > 0, but kept as an explicit
        // safety net for floating-point edge cases
        if ((r1 + r2) == 0.0) {
            throw new ArithmeticException("R1 + R2 must not be zero.");
        }
    }

    // ── Core calculation ──────────────────────────────────────────────────────

    /**
     * Performs the full voltage divider calculation and returns all derived values.
     *
     * @param vin Input voltage in Volts (V), must be >= 0
     * @param r1  Upper resistor in Ohms (Ω),  must be > 0
     * @param r2  Lower resistor in Ohms (Ω),  must be > 0
     * @return    VoltageDividerResult containing Vout, VR1, VR2, and current
     */
    public VoltageDividerResult calculate(double vin, double r1, double r2) {
        validate(vin, r1, r2);

        double totalR   = r1 + r2;
        double vOut     = vin * r2 / totalR;    // Vout = Vin * R2 / (R1 + R2)
        double current  = vin / totalR;          // I   = Vin / (R1 + R2)
        double vDropR1  = current * r1;          // VR1 = I * R1  (== Vin - Vout)
        double vDropR2  = vOut;                  // VR2 == Vout by definition

        return new VoltageDividerResult(vOut, vDropR1, vDropR2, current);
    }
}
