package voltedge.model;

/**
 * CircuitCalculator is the abstract base class for all calculation models in VoltEdge.
 * 
 * Demonstrates:
 * - Abstract Classes
 * - Encapsulation (protected/private fields, getters)
 * - Inheritance hierarchy for engineering calculations
 */
public abstract class CircuitCalculator {

    private final String moduleName;
    private final String formulaDescription;

    public CircuitCalculator(String moduleName, String formulaDescription) {
        this.moduleName = moduleName;
        this.formulaDescription = formulaDescription;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getFormulaDescription() {
        return formulaDescription;
    }
}
