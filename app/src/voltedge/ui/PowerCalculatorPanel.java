package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import voltedge.model.PowerCalculator;

/**
 * PowerCalculatorPanel provides the graphical interface for computing
 * electrical power using three formula combinations (V·I, I²R, V²/R).
 *
 * Demonstrates:
 * - GUI Event Handling with JComboBox mode switching
 * - Delegation to PowerCalculator model (separation of logic from UI)
 * - Dynamic label/unit updates based on user selection
 * - CalculationHistory recording
 */
public class PowerCalculatorPanel extends JPanel {

    private final PowerCalculator calculator = new PowerCalculator();
    private final CalculationHistory history;

    // UI Components
    private JComboBox<String> modeSelector;
    private JLabel input1Label;
    private JTextField input1Field;
    private JLabel unit1Label;

    private JLabel input2Label;
    private JTextField input2Field;
    private JLabel unit2Label;

    private JLabel resultValueLabel;
    private JLabel formulaStepLabel;
    private JLabel errorLabel;

    public PowerCalculatorPanel() {
        this(null);
    }

    public PowerCalculatorPanel(CalculationHistory history) {
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

        // Set default input mode
        updateInputMode(0);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("2. Electrical Power Calculator");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Calculates power dissipation using voltage, current, or resistance combinations.");
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

        // Row 0: Formula selector
        JLabel modeLabel = new JLabel("Select Formula:");
        modeLabel.setFont(AppTheme.FONT_SUBHEADER);
        modeLabel.setForeground(AppTheme.TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(modeLabel, gbc);

        String[] modes = {
            "P = V · I         (Voltage × Current)",
            "P = I² · R        (Current² × Resistance)",
            "P = V² / R        (Voltage² ÷ Resistance)"
        };
        modeSelector = new JComboBox<>(modes);
        modeSelector.setFont(AppTheme.FONT_BODY);
        modeSelector.addActionListener(e -> updateInputMode(modeSelector.getSelectedIndex()));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(modeSelector, gbc);

        // Row 1: Input 1
        input1Label = new JLabel("Voltage (V):");
        input1Label.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(input1Label, gbc);

        input1Field = new JTextField(12);
        input1Field.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(input1Field, gbc);

        unit1Label = new JLabel("Volts (V)");
        unit1Label.setFont(AppTheme.FONT_SMALL);
        unit1Label.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridx = 2; gbc.gridy = 1;
        card.add(unit1Label, gbc);

        // Row 2: Input 2
        input2Label = new JLabel("Current (I):");
        input2Label.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        card.add(input2Label, gbc);

        input2Field = new JTextField(12);
        input2Field.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 1; gbc.gridy = 2;
        card.add(input2Field, gbc);

        unit2Label = new JLabel("Amperes (A)");
        unit2Label.setFont(AppTheme.FONT_SMALL);
        unit2Label.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridx = 2; gbc.gridy = 2;
        card.add(unit2Label, gbc);

        // Row 3: Buttons
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

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(buttonBar, gbc);

        // Row 4: Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
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

        resultValueLabel = new JLabel("Enter values and click Calculate");
        resultValueLabel.setFont(AppTheme.FONT_TITLE);
        resultValueLabel.setForeground(AppTheme.COLOR_PRIMARY);

        formulaStepLabel = new JLabel("Formula will be shown here.");
        formulaStepLabel.setFont(AppTheme.FONT_CODE);
        formulaStepLabel.setForeground(AppTheme.TEXT_MUTED);

        card.add(resultHeading);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultValueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(formulaStepLabel);

        return card;
    }

    private void updateInputMode(int modeIndex) {
        errorLabel.setText(" ");
        switch (modeIndex) {
            case 0: // P = V * I
                input1Label.setText("Voltage (V):");
                unit1Label.setText("Volts (V)");
                input2Label.setText("Current (I):");
                unit2Label.setText("Amperes (A)");
                formulaStepLabel.setText("Target Formula: P = V · I");
                break;
            case 1: // P = I^2 * R
                input1Label.setText("Current (I):");
                unit1Label.setText("Amperes (A)");
                input2Label.setText("Resistance (R):");
                unit2Label.setText("Ohms (Ω)");
                formulaStepLabel.setText("Target Formula: P = I² · R");
                break;
            case 2: // P = V^2 / R
                input1Label.setText("Voltage (V):");
                unit1Label.setText("Volts (V)");
                input2Label.setText("Resistance (R):");
                unit2Label.setText("Ohms (Ω)");
                formulaStepLabel.setText("Target Formula: P = V² / R");
                break;
            default:
                break;
        }
    }

    private void performCalculation() {
        errorLabel.setText(" ");
        String text1 = input1Field.getText().trim();
        String text2 = input2Field.getText().trim();

        if (text1.isEmpty() || text2.isEmpty()) {
            errorLabel.setText("Please enter values in both input fields.");
            return;
        }

        double val1, val2;
        try {
            val1 = Double.parseDouble(text1);
            val2 = Double.parseDouble(text2);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid input: Please enter valid numeric values.");
            return;
        }

        int mode = modeSelector.getSelectedIndex();
        try {
            double power;
            String formulaStep;
            String inputs;

            if (mode == 0) {
                // P = V * I
                power = calculator.calculatePower(val1, val2);
                formulaStep = String.format("P = %s V × %s A = %s W",
                        CalculationUtils.formatValue(val1),
                        CalculationUtils.formatValue(val2),
                        CalculationUtils.formatValue(power));
                inputs = String.format("V = %s V, I = %s A", CalculationUtils.formatValue(val1), CalculationUtils.formatValue(val2));
            } else if (mode == 1) {
                // P = I^2 * R
                power = calculator.calculatePower(val1, val2, true);
                formulaStep = String.format("P = (%s A)² × %s Ω = %s W",
                        CalculationUtils.formatValue(val1),
                        CalculationUtils.formatValue(val2),
                        CalculationUtils.formatValue(power));
                inputs = String.format("I = %s A, R = %s Ω", CalculationUtils.formatValue(val1), CalculationUtils.formatValue(val2));
            } else {
                // P = V^2 / R
                power = calculator.calculatePowerVR(val1, val2);
                formulaStep = String.format("P = (%s V)² / %s Ω = %s W",
                        CalculationUtils.formatValue(val1),
                        CalculationUtils.formatValue(val2),
                        CalculationUtils.formatValue(power));
                inputs = String.format("V = %s V, R = %s Ω", CalculationUtils.formatValue(val1), CalculationUtils.formatValue(val2));
            }

            String resultStr = CalculationUtils.formatWithUnit(power, "W");
            resultValueLabel.setText(resultStr);
            formulaStepLabel.setText(formulaStep);

            if (history != null) {
                history.addRecord("Electrical Power", inputs, resultStr);
            }

        } catch (ArithmeticException | IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Calculation Error");
        }
    }

    private void resetInputs() {
        input1Field.setText("");
        input2Field.setText("");
        errorLabel.setText(" ");
        resultValueLabel.setText("Enter values and click Calculate");
        updateInputMode(modeSelector.getSelectedIndex());
    }
}
