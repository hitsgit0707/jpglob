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
import voltedge.model.ResistorCalculator;

/**
 * ResistorCalculatorPanel provides the graphical interface for computing
 * equivalent resistance of resistors in Series or Parallel configurations.
 *
 * User enters resistor values as a comma-separated list (e.g. "10, 20, 30").
 * The panel validates all values before delegating to ResistorCalculator.
 *
 * Demonstrates:
 * - Delegation to model class (separation of concerns)
 * - JComboBox mode switching (Series / Parallel)
 * - Input parsing and inline error display
 * - Consistent styling with AppTheme (matches existing module panels)
 * - CalculationHistory recording
 */
public class ResistorCalculatorPanel extends JPanel {

    private final ResistorCalculator calculator = new ResistorCalculator();
    private final CalculationHistory history;

    // UI Components
    private JComboBox<String> modeSelector;
    private JTextField resistorInputField;
    private JLabel resultValueLabel;
    private JLabel formulaStepLabel;
    private JLabel errorLabel;

    public ResistorCalculatorPanel() {
        this(null);
    }

    public ResistorCalculatorPanel(CalculationHistory history) {
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("3. Resistor Series & Parallel Calculator");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Enter resistor values separated by commas (e.g. 10, 20, 30). Supports any number of resistors.");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_MUTED);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitle);
        return header;
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Mode selector
        JLabel modeLabel = new JLabel("Configuration:");
        modeLabel.setFont(AppTheme.FONT_SUBHEADER);
        modeLabel.setForeground(AppTheme.TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(modeLabel, gbc);

        String[] modes = {
            "Series   —   R_total = R1 + R2 + ... + Rn",
            "Parallel —   1/R_total = 1/R1 + 1/R2 + ... + 1/Rn"
        };
        modeSelector = new JComboBox<>(modes);
        modeSelector.setFont(AppTheme.FONT_BODY);
        // Switching mode clears the result but keeps the user's typed values
        modeSelector.addActionListener(e -> clearResult());
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(modeSelector, gbc);

        // Row 1: Resistor input field
        JLabel inputLabel = new JLabel("Resistor Values (Ω):");
        inputLabel.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(inputLabel, gbc);

        resistorInputField = new JTextField(28);
        resistorInputField.setFont(AppTheme.FONT_BODY);
        resistorInputField.setToolTipText("Enter values separated by commas, e.g:  10, 20, 47, 100");
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(resistorInputField, gbc);

        JLabel unitHint = new JLabel("Ohms (Ω)  — comma-separated");
        unitHint.setFont(AppTheme.FONT_SMALL);
        unitHint.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridx = 2; gbc.gridy = 1;
        card.add(unitHint, gbc);

        // Row 2: Buttons
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

        // Row 3: Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(new Color(220, 38, 38));  // Red
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(errorLabel, gbc);

        return card;
    }

    private JPanel createResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);

        JLabel resultHeading = new JLabel("Computed Result:");
        resultHeading.setFont(AppTheme.FONT_SUBHEADER);
        resultHeading.setForeground(AppTheme.TEXT_DARK);

        resultValueLabel = new JLabel("Enter resistor values and click Calculate");
        resultValueLabel.setFont(AppTheme.FONT_TITLE);
        resultValueLabel.setForeground(AppTheme.COLOR_PRIMARY);

        formulaStepLabel = new JLabel("Formula and working will be shown here.");
        formulaStepLabel.setFont(AppTheme.FONT_CODE);
        formulaStepLabel.setForeground(AppTheme.TEXT_MUTED);

        card.add(resultHeading);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultValueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(formulaStepLabel);

        return card;
    }

    private void performCalculation() {
        errorLabel.setText(" ");
        String rawInput = resistorInputField.getText().trim();

        // Parse and validate the input string — model handles this
        List<Double> resistors;
        try {
            resistors = calculator.parseResistors(rawInput);
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Calculation Error");
            return;
        }

        // Run the selected calculation
        try {
            boolean isParallel = modeSelector.getSelectedIndex() == 1;
            double result;
            String formulaStep;

            if (!isParallel) {
                // Series
                result = calculator.calculateSeries(resistors);
                formulaStep = buildSeriesWorking(resistors, result);
            } else {
                // Parallel
                result = calculator.calculateParallel(resistors);
                formulaStep = buildParallelWorking(resistors, result);
            }

            String resultStr = CalculationUtils.formatWithUnit(result, "Ω");
            resultValueLabel.setText(resultStr);
            formulaStepLabel.setText(formulaStep);

            if (history != null) {
                String config = isParallel ? "Parallel" : "Series";
                history.addRecord("Resistor Networks", String.format("%s: [%s]", config, rawInput), resultStr);
            }

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Calculation Error");
        }
    }

    /** Builds a readable series formula string, e.g. "10 + 20 + 30 = 60 Ω" */
    private String buildSeriesWorking(List<Double> resistors, double result) {
        StringBuilder sb = new StringBuilder("R_total = ");
        for (int i = 0; i < resistors.size(); i++) {
            sb.append(CalculationUtils.formatValue(resistors.get(i)));
            if (i < resistors.size() - 1) sb.append(" + ");
        }
        sb.append(" = ").append(CalculationUtils.formatValue(result)).append(" Ω");
        return sb.toString();
    }

    /** Builds a readable parallel formula string, e.g. "1/(1/10 + 1/10) = 5 Ω" */
    private String buildParallelWorking(List<Double> resistors, double result) {
        StringBuilder sb = new StringBuilder("R_total = 1 / (");
        for (int i = 0; i < resistors.size(); i++) {
            sb.append("1/").append(CalculationUtils.formatValue(resistors.get(i)));
            if (i < resistors.size() - 1) sb.append(" + ");
        }
        sb.append(") = ").append(CalculationUtils.formatValue(result)).append(" Ω");
        return sb.toString();
    }

    /** Clears only the result area, leaving user's typed input intact. */
    private void clearResult() {
        errorLabel.setText(" ");
        resultValueLabel.setText("Enter resistor values and click Calculate");
        formulaStepLabel.setText("Formula and working will be shown here.");
    }

    /** Full reset — clears inputs and results. */
    private void resetInputs() {
        resistorInputField.setText("");
        errorLabel.setText(" ");
        resultValueLabel.setText("Enter resistor values and click Calculate");
        formulaStepLabel.setText("Formula and working will be shown here.");
    }
}
