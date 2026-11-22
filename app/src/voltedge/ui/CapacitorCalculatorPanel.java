package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import voltedge.model.CalculationHistory;
import voltedge.model.CalculationUtils;
import voltedge.model.CapacitorCalculator;

/**
 * CapacitorCalculatorPanel provides the graphical interface for computing
 * equivalent capacitance of capacitors in Parallel or Series configurations.
 *
 * The user enters capacitor values as a comma-separated list (e.g. "100e-6, 220e-6").
 * Scientific notation is supported because real-world capacitor values in Farads
 * are almost always very small numbers (µF = 1e-6, nF = 1e-9, pF = 1e-12).
 *
 * Demonstrates:
 * - Delegation to CapacitorCalculator model (separation of concerns)
 * - JComboBox mode switching (Parallel / Series)
 * - Input parsing and inline error display
 * - Consistent styling with AppTheme (matches existing module panels)
 * - CalculationHistory recording
 */
public class CapacitorCalculatorPanel extends JPanel {

    private final CapacitorCalculator calculator = new CapacitorCalculator();
    private final CalculationHistory history;

    // UI Components
    private JComboBox<String> modeSelector;
    private JTextField capacitorInputField;
    private JLabel resultValueLabel;
    private JLabel formulaStepLabel;
    private JLabel noteLabel;
    private JLabel errorLabel;

    public CapacitorCalculatorPanel() {
        this(null);
    }

    public CapacitorCalculatorPanel(CalculationHistory history) {
        this.history = history;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppTheme.BG_MAIN);
        contentPanel.setBorder(AppTheme.BORDER_EMPTY_PADDING);

        contentPanel.add(createHeaderPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(createFormCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(createResultCard());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("5. Capacitor Series & Parallel Calculator");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel(
            "Enter capacitor values separated by commas. Scientific notation accepted (e.g. 100e-6, 1e-9)."
        );
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_MUTED);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitle);
        return header;
    }

    // ── Input Form Card ───────────────────────────────────────────────────────

    private JPanel createFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // ── Row 0: Mode selector ──────────────────────────────────────────────
        JLabel modeLabel = new JLabel("Configuration:");
        modeLabel.setFont(AppTheme.FONT_SUBHEADER);
        modeLabel.setForeground(AppTheme.TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(modeLabel, gbc);

        // Note: Parallel ADDS for capacitors (opposite of resistors).
        //       Series   DIVIDES  for capacitors (opposite of resistors).
        String[] modes = {
            "Parallel  —  C_total = C1 + C2 + ... + Cn",
            "Series    —  1/C_total = 1/C1 + 1/C2 + ... + 1/Cn"
        };
        modeSelector = new JComboBox<>(modes);
        modeSelector.setFont(AppTheme.FONT_BODY);
        modeSelector.addActionListener(e -> clearResult());
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(modeSelector, gbc);

        // ── Row 1: Capacitor value input ──────────────────────────────────────
        JLabel inputLabel = new JLabel("Capacitor Values (F):");
        inputLabel.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(inputLabel, gbc);

        capacitorInputField = new JTextField(28);
        capacitorInputField.setFont(AppTheme.FONT_BODY);
        capacitorInputField.setToolTipText(
            "Enter values in Farads, comma-separated. E.g.:  100e-6, 220e-6, 10e-9"
        );
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(capacitorInputField, gbc);

        JLabel unitHint = new JLabel("Farads (F)  — comma-separated");
        unitHint.setFont(AppTheme.FONT_SMALL);
        unitHint.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridx = 2; gbc.gridy = 1;
        card.add(unitHint, gbc);

        // ── Row 2: Buttons ────────────────────────────────────────────────────
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonBar.setBackground(AppTheme.BG_CARD);

        JButton calcBtn = new JButton("Calculate");
        calcBtn.setFont(AppTheme.FONT_NAV);
        calcBtn.setBackground(AppTheme.COLOR_PRIMARY);
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        calcBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        calcBtn.addActionListener(e -> performCalculation());

        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(AppTheme.FONT_NAV);
        resetBtn.setBackground(AppTheme.BG_MAIN);
        resetBtn.setForeground(AppTheme.TEXT_DARK);
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        resetBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        resetBtn.addActionListener(e -> resetInputs());

        buttonBar.add(calcBtn);
        buttonBar.add(resetBtn);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(buttonBar, gbc);

        // ── Row 3: Error label ────────────────────────────────────────────────
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(errorLabel, gbc);

        return card;
    }

    // ── Result Card ───────────────────────────────────────────────────────────

    private JPanel createResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);

        JLabel resultHeading = new JLabel("Computed Result:");
        resultHeading.setFont(AppTheme.FONT_SUBHEADER);
        resultHeading.setForeground(AppTheme.TEXT_DARK);

        resultValueLabel = new JLabel("Enter capacitor values and click Calculate");
        resultValueLabel.setFont(AppTheme.FONT_TITLE);
        resultValueLabel.setForeground(AppTheme.COLOR_PRIMARY);

        formulaStepLabel = new JLabel("Formula and working will be shown here.");
        formulaStepLabel.setFont(AppTheme.FONT_CODE);
        formulaStepLabel.setForeground(AppTheme.TEXT_MUTED);

        // Practical note about units — capacitor values in Farads are tiny,
        // so scientific notation in the result is expected and correct.
        noteLabel = new JLabel("Tip: 1 µF = 1e-6 F  |  1 nF = 1e-9 F  |  1 pF = 1e-12 F");
        noteLabel.setFont(AppTheme.FONT_SMALL);
        noteLabel.setForeground(AppTheme.COLOR_ACCENT);

        card.add(resultHeading);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultValueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(formulaStepLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(noteLabel);

        return card;
    }

    // ── Calculation logic ─────────────────────────────────────────────────────

    private void performCalculation() {
        errorLabel.setText(" ");
        String rawInput = capacitorInputField.getText().trim();

        // Parse — model handles all parsing and format errors
        List<Double> capacitors;
        try {
            capacitors = calculator.parseCapacitors(rawInput);
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Calculation Error");
            return;
        }

        // Calculate
        try {
            boolean isParallel = modeSelector.getSelectedIndex() == 0;
            double result;
            String formulaStep;

            if (isParallel) {
                result      = calculator.calculateParallel(capacitors);
                formulaStep = buildParallelWorking(capacitors, result);
            } else {
                result      = calculator.calculateSeries(capacitors);
                formulaStep = buildSeriesWorking(capacitors, result);
            }

            String resultStr = CalculationUtils.formatWithUnit(result, "F");
            resultValueLabel.setText(resultStr);
            formulaStepLabel.setText(formulaStep);

            if (history != null) {
                String config = isParallel ? "Parallel" : "Series";
                history.addRecord("Capacitor Networks", String.format("%s: [%s]", config, rawInput), resultStr);
            }

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Calculation Error");
        }
    }

    /** Builds a readable parallel formula string: C_total = C1 + C2 + ... = result F */
    private String buildParallelWorking(List<Double> capacitors, double result) {
        StringBuilder sb = new StringBuilder("C_total = ");
        for (int i = 0; i < capacitors.size(); i++) {
            sb.append(CalculationUtils.formatValue(capacitors.get(i)));
            if (i < capacitors.size() - 1) sb.append(" + ");
        }
        sb.append(" = ").append(CalculationUtils.formatValue(result)).append(" F");
        return sb.toString();
    }

    /** Builds a readable series formula string: C_total = 1/(1/C1 + 1/C2 + ...) = result F */
    private String buildSeriesWorking(List<Double> capacitors, double result) {
        StringBuilder sb = new StringBuilder("C_total = 1 / (");
        for (int i = 0; i < capacitors.size(); i++) {
            sb.append("1/").append(CalculationUtils.formatValue(capacitors.get(i)));
            if (i < capacitors.size() - 1) sb.append(" + ");
        }
        sb.append(") = ").append(CalculationUtils.formatValue(result)).append(" F");
        return sb.toString();
    }

    /** Clears result display, keeping user's typed input. */
    private void clearResult() {
        errorLabel.setText(" ");
        resultValueLabel.setText("Enter capacitor values and click Calculate");
        formulaStepLabel.setText("Formula and working will be shown here.");
    }

    /** Full reset — clears both input and result. */
    private void resetInputs() {
        capacitorInputField.setText("");
        clearResult();
    }
}
