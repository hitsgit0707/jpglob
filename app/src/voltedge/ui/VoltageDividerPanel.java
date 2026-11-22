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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import voltedge.model.CalculationHistory;
import voltedge.model.CalculationUtils;
import voltedge.model.VoltageDividerCalculator;
import voltedge.model.VoltageDividerCalculator.VoltageDividerResult;

/**
 * VoltageDividerPanel provides the graphical interface for the voltage divider
 * calculator module.
 *
 * Demonstrates:
 * - Delegation to VoltageDividerCalculator model
 * - Three-input GridBagLayout form (matches existing module style)
 * - Multi-line result display using stacked JLabels
 * - Inline validation error feedback
 * - CalculationHistory recording
 */
public class VoltageDividerPanel extends JPanel {

    private final VoltageDividerCalculator calculator = new VoltageDividerCalculator();
    private final CalculationHistory history;

    // Input fields
    private JTextField vinField;
    private JTextField r1Field;
    private JTextField r2Field;

    // Result labels
    private JLabel resultVoutLabel;
    private JLabel resultVR1Label;
    private JLabel resultVR2Label;
    private JLabel resultCurrentLabel;
    private JLabel formulaStepLabel;
    private JLabel errorLabel;

    public VoltageDividerPanel() {
        this(null);
    }

    public VoltageDividerPanel(CalculationHistory history) {
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

        JLabel title = new JLabel("4. Voltage Divider Calculator");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel(
            "Computes output voltage, resistor voltage drops, and series current for a two-resistor divider network."
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

        // ── Row 0: Section label ──────────────────────────────────────────────
        JLabel inputsHeading = new JLabel("Circuit Parameters:");
        inputsHeading.setFont(AppTheme.FONT_SUBHEADER);
        inputsHeading.setForeground(AppTheme.TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        card.add(inputsHeading, gbc);
        gbc.gridwidth = 1;

        // ── Row 1: Vin ────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(makeLabel("Input Voltage (Vin):"), gbc);

        vinField = new JTextField(12);
        vinField.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 1;
        card.add(vinField, gbc);

        gbc.gridx = 2;
        card.add(makeUnitLabel("Volts (V)"), gbc);

        // ── Row 2: R1 ─────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(makeLabel("Upper Resistor (R1):"), gbc);

        r1Field = new JTextField(12);
        r1Field.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 1;
        card.add(r1Field, gbc);

        gbc.gridx = 2;
        card.add(makeUnitLabel("Ohms (Ω)"), gbc);

        // ── Row 3: R2 ─────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 3;
        card.add(makeLabel("Lower Resistor (R2):"), gbc);

        r2Field = new JTextField(12);
        r2Field.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 1;
        card.add(r2Field, gbc);

        gbc.gridx = 2;
        card.add(makeUnitLabel("Ohms (Ω)"), gbc);

        // ── Row 4: Buttons ────────────────────────────────────────────────────
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

        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(buttonBar, gbc);

        // ── Row 5: Error label ────────────────────────────────────────────────
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(new Color(220, 38, 38));
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2;
        card.add(errorLabel, gbc);

        return card;
    }

    // ── Result Card ───────────────────────────────────────────────────────────

    private JPanel createResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);

        JLabel resultHeading = new JLabel("Computed Results:");
        resultHeading.setFont(AppTheme.FONT_SUBHEADER);
        resultHeading.setForeground(AppTheme.TEXT_DARK);

        // Primary result — Vout — displayed large like other modules
        resultVoutLabel = new JLabel("Enter values and click Calculate");
        resultVoutLabel.setFont(AppTheme.FONT_TITLE);
        resultVoutLabel.setForeground(AppTheme.COLOR_PRIMARY);

        // Secondary results — smaller, muted
        resultVR1Label     = new JLabel(" ");
        resultVR2Label     = new JLabel(" ");
        resultCurrentLabel = new JLabel(" ");
        formulaStepLabel   = new JLabel("Formula will be shown here.");

        for (JLabel lbl : new JLabel[]{ resultVR1Label, resultVR2Label,
                                        resultCurrentLabel, formulaStepLabel }) {
            lbl.setFont(AppTheme.FONT_CODE);
            lbl.setForeground(AppTheme.TEXT_MUTED);
        }

        card.add(resultHeading);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultVoutLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultVR1Label);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(resultVR2Label);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(resultCurrentLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(formulaStepLabel);

        return card;
    }

    // ── Calculation logic ─────────────────────────────────────────────────────

    private void performCalculation() {
        errorLabel.setText(" ");

        // 1. Read and check for empty fields
        String vinText = vinField.getText().trim();
        String r1Text  = r1Field.getText().trim();
        String r2Text  = r2Field.getText().trim();

        if (vinText.isEmpty() || r1Text.isEmpty() || r2Text.isEmpty()) {
            errorLabel.setText("All three fields (Vin, R1, R2) are required.");
            return;
        }

        // 2. Parse to double
        double vin, r1, r2;
        try {
            vin = Double.parseDouble(vinText);
            r1  = Double.parseDouble(r1Text);
            r2  = Double.parseDouble(r2Text);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid input: please enter numeric values only.");
            return;
        }

        // 3. Delegate to model — model throws descriptive exceptions on bad values
        try {
            VoltageDividerResult res = calculator.calculate(vin, r1, r2);

            // Primary result
            resultVoutLabel.setText("Vout = " + CalculationUtils.formatWithUnit(res.vOut, "V"));

            // Secondary derived values
            resultVR1Label.setText(
                "Voltage across R1:  VR1 = " + CalculationUtils.formatWithUnit(res.vDropR1, "V")
            );
            resultVR2Label.setText(
                "Voltage across R2:  VR2 = " + CalculationUtils.formatWithUnit(res.vDropR2, "V")
            );
            resultCurrentLabel.setText(
                "Series current:      I  = " + CalculationUtils.formatWithUnit(res.current, "A")
            );

            // Formula with substituted values
            formulaStepLabel.setText(String.format(
                "Vout = %s V × %s Ω / (%s Ω + %s Ω) = %s V",
                CalculationUtils.formatValue(vin),
                CalculationUtils.formatValue(r2),
                CalculationUtils.formatValue(r1),
                CalculationUtils.formatValue(r2),
                CalculationUtils.formatValue(res.vOut)
            ));

            if (history != null) {
                history.addRecord("Voltage Divider",
                    String.format("Vin = %s V, R1 = %s Ω, R2 = %s Ω",
                        CalculationUtils.formatValue(vin), CalculationUtils.formatValue(r1), CalculationUtils.formatValue(r2)),
                    String.format("Vout = %s V, I = %s A",
                        CalculationUtils.formatValue(res.vOut), CalculationUtils.formatValue(res.current)));
            }

        } catch (IllegalArgumentException | ArithmeticException ex) {
            errorLabel.setText(ex.getMessage());
            resultVoutLabel.setText("Calculation Error");
            clearSecondaryResults();
        }
    }

    private void resetInputs() {
        vinField.setText("");
        r1Field.setText("");
        r2Field.setText("");
        errorLabel.setText(" ");
        resultVoutLabel.setText("Enter values and click Calculate");
        formulaStepLabel.setText("Formula will be shown here.");
        clearSecondaryResults();
    }

    private void clearSecondaryResults() {
        resultVR1Label.setText(" ");
        resultVR2Label.setText(" ");
        resultCurrentLabel.setText(" ");
    }

    // ── Small factory helpers (avoid repetition) ───────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_BODY);
        return lbl;
    }

    private JLabel makeUnitLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_MUTED);
        return lbl;
    }
}
