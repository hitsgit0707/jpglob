package voltedge.test;

import voltedge.model.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * RunAllTests executes comprehensive unit and regression tests across
 * all six calculation modules and the history persistence engine.
 */
public class RunAllTests {

    private static int passed = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   VOLTEDGE AUTOMATED TEST SUITE EXECUTION       ");
        System.out.println("=================================================");

        testOhmsLaw();
        testPowerCalculator();
        testResistorCalculator();
        testVoltageDivider();
        testCapacitorCalculator();
        testUnitConverter();
        testCalculationHistory();

        System.out.println("\n-------------------------------------------------");
        System.out.printf("TOTAL RESULT: %d / %d tests passed successfully.%n", passed, total);
        System.out.println("-------------------------------------------------");

        if (passed != total) {
            System.err.println("TEST SUITE FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED WITH ZERO ERRORS.");
        }
    }

    private static void testOhmsLaw() {
        System.out.println("\n[Testing Module 1: Ohm's Law]");
        OhmsLawCalculator calc = new OhmsLawCalculator();

        check("Voltage V = 2.5 A * 10 Ω", calc.calculateVoltage(2.5, 10.0), 25.0);
        check("Current I = 120 V / 24 Ω", calc.calculateCurrent(120.0, 24.0), 5.0);
        check("Resistance R = 5 V / 0.02 A", calc.calculateResistance(5.0, 0.02), 250.0);

        // Edge case: Short circuit (R = 0)
        total++;
        try {
            calc.calculateCurrent(12.0, 0.0);
            fail("Expected ArithmeticException on R = 0");
        } catch (ArithmeticException ex) {
            pass("Caught expected short-circuit: " + ex.getMessage());
        }

        // Edge case: Negative resistance rejected
        total++;
        try {
            calc.calculateVoltage(2.0, -10.0);
            fail("Expected IllegalArgumentException on negative R");
        } catch (IllegalArgumentException ex) {
            pass("Caught expected negative resistance rejection: " + ex.getMessage());
        }
    }

    private static void testPowerCalculator() {
        System.out.println("\n[Testing Module 2: Electrical Power]");
        PowerCalculator calc = new PowerCalculator();

        check("Power P = 12 V * 3 A", calc.calculatePower(12.0, 3.0), 36.0);
        check("Power P = (2 A)² * 10 Ω", calc.calculatePower(2.0, 10.0, true), 40.0);
        check("Power P = (100 V)² / 25 Ω", calc.calculatePowerVR(100.0, 25.0), 400.0);

        // Edge case: R = 0 in V²/R
        total++;
        try {
            calc.calculatePowerVR(10.0, 0.0);
            fail("Expected ArithmeticException for R = 0 in PowerVR");
        } catch (ArithmeticException ex) {
            pass("Caught expected division-by-zero: " + ex.getMessage());
        }
    }

    private static void testResistorCalculator() {
        System.out.println("\n[Testing Module 3: Resistor Networks]");
        ResistorCalculator calc = new ResistorCalculator();

        check("Series 10 + 20 + 30 Ω", calc.calculateSeries(Arrays.asList(10.0, 20.0, 30.0)), 60.0);
        check("Parallel 10 Ω || 10 Ω", calc.calculateParallel(Arrays.asList(10.0, 10.0)), 5.0);
        check("Parallel 10 Ω || 20 Ω", calc.calculateParallel(Arrays.asList(10.0, 20.0)), 20.0 / 3.0);

        // Comma-separated parsing
        total++;
        List<Double> parsed = calc.parseResistors(" 10, 20, 30 ");
        if (parsed.size() == 3 && parsed.get(0) == 10.0 && parsed.get(2) == 30.0) {
            pass("Parsed comma-separated resistors: " + parsed);
        } else {
            fail("Failed to parse resistors string");
        }
    }

    private static void testVoltageDivider() {
        System.out.println("\n[Testing Module 4: Voltage Divider]");
        VoltageDividerCalculator calc = new VoltageDividerCalculator();

        VoltageDividerCalculator.VoltageDividerResult res = calc.calculate(12.0, 4.0, 2.0);
        check("Vout for Vin=12V, R1=4Ω, R2=2Ω", res.vOut, 4.0);
        check("Voltage drop VR1", res.vDropR1, 8.0);
        check("Voltage drop VR2", res.vDropR2, 4.0);
        check("Series Current I", res.current, 2.0);
        check("Conservation VR1 + VR2 == Vin", res.vDropR1 + res.vDropR2, 12.0);
    }

    private static void testCapacitorCalculator() {
        System.out.println("\n[Testing Module 5: Capacitor Networks]");
        CapacitorCalculator calc = new CapacitorCalculator();

        check("Parallel 100µF + 220µF", calc.calculateParallel(Arrays.asList(100e-6, 220e-6)), 320e-6);
        check("Series 100µF series 100µF", calc.calculateSeries(Arrays.asList(100e-6, 100e-6)), 50e-6);
        check("Series 10µF series 40µF", calc.calculateSeries(Arrays.asList(10e-6, 40e-6)), 8e-6);

        // Scientific parse
        total++;
        List<Double> parsed = calc.parseCapacitors("100e-6, 220e-6");
        if (parsed.size() == 2 && Math.abs(parsed.get(0) - 1e-4) < 1e-9) {
            pass("Scientific notation capacitance parsed: " + parsed);
        } else {
            fail("Scientific notation parse failure");
        }
    }

    private static void testUnitConverter() {
        System.out.println("\n[Testing Module 6: Unit Converter]");
        UnitConverter conv = new UnitConverter();

        check("1000 mV -> V", conv.convert(UnitConverter.Category.VOLTAGE, "mV", "V", 1000.0), 1.0);
        check("2 kV -> V", conv.convert(UnitConverter.Category.VOLTAGE, "kV", "V", 2.0), 2000.0);
        check("500 mA -> A", conv.convert(UnitConverter.Category.CURRENT, "mA", "A", 500.0), 0.5);
        check("2 kΩ -> Ω", conv.convert(UnitConverter.Category.RESISTANCE, "kΩ", "Ω", 2.0), 2000.0);
        check("1000 µF -> F", conv.convert(UnitConverter.Category.CAPACITANCE, "µF", "F", 1000.0), 0.001);
        check("1 kW -> W", conv.convert(UnitConverter.Category.POWER, "kW", "W", 1.0), 1000.0);
        check("2 MHz -> Hz", conv.convert(UnitConverter.Category.FREQUENCY, "MHz", "Hz", 2.0), 2000000.0);
        check("Same-unit 10 V -> V", conv.convert(UnitConverter.Category.VOLTAGE, "V", "V", 10.0), 10.0);

        // Negative resistance rejected
        total++;
        try {
            conv.convert(UnitConverter.Category.RESISTANCE, "kΩ", "Ω", -5.0);
            fail("Expected IllegalArgumentException for negative resistance");
        } catch (IllegalArgumentException ex) {
            pass("Negative resistance rejected as expected");
        }
    }

    private static void testCalculationHistory() {
        System.out.println("\n[Testing Calculation History & File Persistence]");
        File tempFile = new File("app_test_history.txt");
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Test missing file
        CalculationHistory history = new CalculationHistory(tempFile);
        check("Initial missing file produces 0 records", history.size(), 0.0);

        // Add records
        history.addRecord("Ohm's Law", "I = 2 A, R = 10 Ω", "20.0 V");
        history.addRecord("Power", "V = 10 V, I = 2 A", "20.0 W");
        check("Added 2 records to history", history.size(), 2.0);

        // Reload across restart
        CalculationHistory reloaded = new CalculationHistory(tempFile);
        check("Reloaded 2 records from file", reloaded.size(), 2.0);

        // Clear history
        history.clearHistory();
        check("Cleared history has 0 records", history.size(), 0.0);

        tempFile.delete();
        total++;
        pass("History persistence and clearing verified");
    }

    private static void check(String label, double actual, double expected) {
        total++;
        if (Math.abs(actual - expected) < Math.abs(expected) * 1e-9 + 1e-15) {
            pass(label + " -> " + actual);
        } else {
            fail(label + ": expected " + expected + ", got " + actual);
        }
    }

    private static void pass(String msg) {
        System.out.println("  PASS: " + msg);
        passed++;
    }

    private static void fail(String msg) {
        System.err.println("  FAIL: " + msg);
    }
}
