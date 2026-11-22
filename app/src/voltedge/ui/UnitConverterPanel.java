package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import voltedge.model.CalculationHistory;
import voltedge.model.UnitConverter;
import voltedge.model.UnitConverter.Category;

/**
 * UnitConverterPanel provides the graphical interface for converting between
 * electrical and frequency units across different metric prefixes.
 * 
 * Demonstrates:
 * - Delegation to UnitConverter model
 * - Dynamic JComboBox population based on category selection
 * - Interactive Swap functionality
 * - Synchronized UI reset and clearing states
 * - CalculationHistory recording
 */
public class UnitConverterPanel extends JPanel {

    private final UnitConverter converter = new UnitConverter();
    private final CalculationHistory history;

    // UI Controls
    private JComboBox<String> categoryCombo;
    private JComboBox<String> fromUnitCombo;
    private JComboBox<String> toUnitCombo;
    private JTextField inputField;

    private JLabel resultValueLabel;
    private JLabel conversionRelationLabel;
    private JLabel formulaStepLabel;
    private JLabel errorLabel;

    private boolean isUpdatingUnits = false;

    public UnitConverterPanel() {
        this(null);
    }

    public UnitConverterPanel(CalculationHistory history) {
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

        // Initial setup for the first category (Voltage)
        updateUnitsForCategory(Category.VOLTAGE);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("6. Electrical Unit Converter");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Convert between metric prefixes for Voltage, Current, Resistance, Capacitance, Power, and Frequency.");
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

        // Row 0: Category Selection
        JLabel catLabel = new JLabel("Measurement Category:");
        catLabel.setFont(AppTheme.FONT_SUBHEADER);
        catLabel.setForeground(AppTheme.TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        card.add(catLabel, gbc);

        String[] categoryNames = new String[Category.values().length];
        for (int i = 0; i < Category.values().length; i++) {
            categoryNames[i] = Category.values()[i].getDisplayName();
        }
        categoryCombo = new JComboBox<>(categoryNames);
        categoryCombo.setFont(AppTheme.FONT_BODY);
        categoryCombo.addActionListener(e -> onCategoryChanged());
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(categoryCombo, gbc);

        // Row 1: Input Value + From Unit
        JLabel inputLabel = new JLabel("Value & Source Unit:");
        inputLabel.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(inputLabel, gbc);

        inputField = new JTextField(12);
        inputField.setFont(AppTheme.FONT_BODY);
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { clearResult(); }
            @Override
            public void removeUpdate(DocumentEvent e) { clearResult(); }
            @Override
            public void changedUpdate(DocumentEvent e) { clearResult(); }
        });
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(inputField, gbc);

        fromUnitCombo = new JComboBox<>();
        fromUnitCombo.setFont(AppTheme.FONT_BODY);
        fromUnitCombo.addActionListener(e -> {
            if (!isUpdatingUnits) clearResult();
        });
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(fromUnitCombo, gbc);

        // Row 2: Swap Button + Target Unit
        JLabel toLabel = new JLabel("Destination Unit:");
        toLabel.setFont(AppTheme.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        card.add(toLabel, gbc);

        JButton swapBtn = new JButton("⇄ Swap Units");
        swapBtn.setFont(AppTheme.FONT_SMALL);
        swapBtn.setBackground(AppTheme.BG_MAIN);
        swapBtn.setForeground(AppTheme.COLOR_PRIMARY);
        swapBtn.setFocusPainted(false);
        swapBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        swapBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        swapBtn.addActionListener(e -> swapUnits());
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        card.add(swapBtn, gbc);

        toUnitCombo = new JComboBox<>();
        toUnitCombo.setFont(AppTheme.FONT_BODY);
        toUnitCombo.addActionListener(e -> {
            if (!isUpdatingUnits) clearResult();
        });
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 1;
        card.add(toUnitCombo, gbc);

        // Row 3: Action Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonBar.setBackground(AppTheme.BG_CARD);

        JButton convertBtn = new JButton("Convert");
        convertBtn.setFont(AppTheme.FONT_NAV);
        convertBtn.setBackground(AppTheme.COLOR_PRIMARY);
        convertBtn.setForeground(Color.WHITE);
        convertBtn.setFocusPainted(false);
        convertBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        convertBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        convertBtn.addActionListener(e -> performConversion());

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

        buttonBar.add(convertBtn);
        buttonBar.add(resetBtn);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        card.add(buttonBar, gbc);

        // Row 4: Error Label
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

        JLabel resultHeading = new JLabel("Conversion Result:");
        resultHeading.setFont(AppTheme.FONT_SUBHEADER);
        resultHeading.setForeground(AppTheme.TEXT_DARK);

        resultValueLabel = new JLabel("Enter a value and click Convert");
        resultValueLabel.setFont(AppTheme.FONT_TITLE);
        resultValueLabel.setForeground(AppTheme.COLOR_PRIMARY);

        conversionRelationLabel = new JLabel(" ");
        conversionRelationLabel.setFont(AppTheme.FONT_CODE);
        conversionRelationLabel.setForeground(AppTheme.TEXT_DARK);

        formulaStepLabel = new JLabel(" ");
        formulaStepLabel.setFont(AppTheme.FONT_SMALL);
        formulaStepLabel.setForeground(AppTheme.TEXT_MUTED);

        card.add(resultHeading);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(resultValueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(conversionRelationLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(formulaStepLabel);

        return card;
    }

    private void onCategoryChanged() {
        String selectedName = (String) categoryCombo.getSelectedItem();
        if (selectedName != null) {
            Category cat = Category.fromDisplayName(selectedName);
            updateUnitsForCategory(cat);
            clearResult();
        }
    }

    private void updateUnitsForCategory(Category cat) {
        isUpdatingUnits = true;
        Set<String> units = cat.getUnitNames();
        String[] unitArray = units.toArray(new String[0]);

        fromUnitCombo.setModel(new DefaultComboBoxModel<>(unitArray));
        toUnitCombo.setModel(new DefaultComboBoxModel<>(unitArray));

        // Default: from first unit, to second or base unit
        if (unitArray.length > 1) {
            fromUnitCombo.setSelectedIndex(0);
            toUnitCombo.setSelectedIndex(Math.min(2, unitArray.length - 1));
        }

        isUpdatingUnits = false;
    }

    private void swapUnits() {
        int fromIdx = fromUnitCombo.getSelectedIndex();
        int toIdx = toUnitCombo.getSelectedIndex();
        if (fromIdx != -1 && toIdx != -1) {
            isUpdatingUnits = true;
            fromUnitCombo.setSelectedIndex(toIdx);
            toUnitCombo.setSelectedIndex(fromIdx);
            isUpdatingUnits = false;

            // If input is non-empty, auto-convert for convenience
            if (!inputField.getText().trim().isEmpty()) {
                performConversion();
            } else {
                clearResult();
            }
        }
    }

    private void performConversion() {
        errorLabel.setText(" ");
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            errorLabel.setText("Please enter a numeric value to convert.");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            errorLabel.setText("Invalid input: \"" + text + "\" is not a valid number.");
            resultValueLabel.setText("Conversion Error");
            return;
        }

        Category cat = Category.fromDisplayName((String) categoryCombo.getSelectedItem());
        String fromUnit = (String) fromUnitCombo.getSelectedItem();
        String toUnit = (String) toUnitCombo.getSelectedItem();

        try {
            double converted = converter.convert(cat, fromUnit, toUnit, value);
            String formattedConverted = UnitConverter.formatConvertedValue(converted);
            resultValueLabel.setText(formattedConverted + " " + toUnit);

            // Show relationship and conversion step
            String relation = converter.getConversionRelation(cat, fromUnit, toUnit);
            conversionRelationLabel.setText("Unit Relationship: " + relation);
            formulaStepLabel.setText(String.format("Calculated: %s %s = %s %s",
                    UnitConverter.formatConvertedValue(value), fromUnit,
                    formattedConverted, toUnit));

            if (history != null) {
                history.addRecord("Unit Converter (" + cat.getDisplayName() + ")",
                        String.format("%s %s -> %s", UnitConverter.formatConvertedValue(value), fromUnit, toUnit),
                        formattedConverted + " " + toUnit);
            }

        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            resultValueLabel.setText("Conversion Error");
            conversionRelationLabel.setText(" ");
            formulaStepLabel.setText(" ");
        }
    }

    private void clearResult() {
        errorLabel.setText(" ");
        resultValueLabel.setText("Enter a value and click Convert");
        conversionRelationLabel.setText(" ");
        formulaStepLabel.setText(" ");
    }

    private void resetInputs() {
        inputField.setText("");
        categoryCombo.setSelectedIndex(0);
        updateUnitsForCategory(Category.VOLTAGE);
        clearResult();
    }
}
