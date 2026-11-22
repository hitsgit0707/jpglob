package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * DashboardPanel represents the home screen of VoltEdge.
 * It provides an overview of all 6 calculator modules and allows one-click
 * navigation to each module.
 * 
 * Demonstrates:
 * - Event Handling (MouseListener, ActionListener)
 * - Nested Layout Managers (BorderLayout, GridLayout, BoxLayout)
 * - Callback interfaces for UI navigation
 */
public class DashboardPanel extends JPanel {

    private final NavigationListener navListener;

    public DashboardPanel(NavigationListener navListener) {
        this.navListener = navListener;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);

        // Main content wrapper inside a scroll pane for smaller screens
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppTheme.BG_MAIN);
        contentPanel.setBorder(AppTheme.BORDER_EMPTY_PADDING);

        // 1. Hero / Header Section
        JPanel heroPanel = createHeroPanel();
        contentPanel.add(heroPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Section Heading
        JLabel sectionLabel = new JLabel("Available Calculation Modules");
        sectionLabel.setFont(AppTheme.FONT_HEADER);
        sectionLabel.setForeground(AppTheme.TEXT_DARK);
        contentPanel.add(sectionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Grid of Module Cards (2 rows x 3 columns)
        JPanel cardGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        cardGrid.setBackground(AppTheme.BG_MAIN);

        cardGrid.add(createModuleCard(
            "Ohm's Law Calculator",
            "Calculate Voltage (V), Current (I), or Resistance (R) from any two known variables.",
            "V = I · R",
            "ohms_law"
        ));

        cardGrid.add(createModuleCard(
            "Electrical Power",
            "Evaluate power dissipation using standard voltage, current, and resistance relations.",
            "P = V·I = I²R = V²/R",
            "power"
        ));

        cardGrid.add(createModuleCard(
            "Resistor Networks",
            "Determine equivalent resistance for multiple resistors connected in series or parallel.",
            "Req = R1 + R2 | 1/Σ(1/Rn)",
            "resistors"
        ));

        cardGrid.add(createModuleCard(
            "Voltage Divider",
            "Compute the output voltage across resistor divider networks with load considerations.",
            "Vout = Vin · [R2 / (R1 + R2)]",
            "voltage_divider"
        ));

        cardGrid.add(createModuleCard(
            "Capacitor Networks",
            "Compute total capacitance for capacitors arranged in parallel or series configurations.",
            "Ceq = C1 + C2 | 1/Σ(1/Cn)",
            "capacitors"
        ));

        cardGrid.add(createModuleCard(
            "Unit Converter",
            "Convert between engineering metric prefixes (pico, nano, micro, milli, kilo, mega).",
            "10⁻¹² (p) ↔ 10⁶ (M)",
            "converter"
        ));

        contentPanel.add(cardGrid);

        // Put everything in a smooth JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeroPanel() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(AppTheme.BG_CARD);
        hero.setBorder(AppTheme.BORDER_CARD);

        JLabel appTitle = new JLabel("VoltEdge — Electrical Engineering Calculator");
        appTitle.setFont(AppTheme.FONT_TITLE);
        appTitle.setForeground(AppTheme.COLOR_PRIMARY);

        JLabel subtitle = new JLabel("A modular Core Java desktop application for circuit calculations, network analysis, and unit conversions.");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_MUTED);

        JLabel techBadge = new JLabel("Built with: Java 17 • Standard Java Swing & AWT • Object-Oriented Architecture");
        techBadge.setFont(AppTheme.FONT_SMALL);
        techBadge.setForeground(AppTheme.COLOR_ACCENT);

        hero.add(appTitle);
        hero.add(Box.createRigidArea(new Dimension(0, 6)));
        hero.add(subtitle);
        hero.add(Box.createRigidArea(new Dimension(0, 8)));
        hero.add(techBadge);

        return hero;
    }

    private JPanel createModuleCard(String title, String description, String formula, String targetKey) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(AppTheme.BORDER_CARD);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Center: Title + Description + Formula Preview
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(AppTheme.BG_CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SUBHEADER);
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JLabel descLabel = new JLabel("<html><p style='color: #64748B; font-size: 11px;'>" + description + "</p></html>");
        descLabel.setFont(AppTheme.FONT_SMALL);

        JLabel formulaLabel = new JLabel(formula);
        formulaLabel.setFont(AppTheme.FONT_CODE);
        formulaLabel.setForeground(AppTheme.COLOR_PRIMARY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(descLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(formulaLabel);

        // Bottom: Launch action button
        JButton launchBtn = new JButton("Open Calculator →");
        launchBtn.setFont(AppTheme.FONT_SMALL);
        launchBtn.setForeground(AppTheme.COLOR_PRIMARY);
        launchBtn.setBackground(AppTheme.BG_MAIN);
        launchBtn.setFocusPainted(false);
        launchBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        launchBtn.addActionListener(e -> {
            if (navListener != null) {
                navListener.navigateTo(targetKey);
            }
        });

        // Click card directly to navigate as well
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navListener != null) {
                    navListener.navigateTo(targetKey);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(241, 245, 249));
                textPanel.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(AppTheme.BG_CARD);
                textPanel.setBackground(AppTheme.BG_CARD);
            }
        });

        card.add(textPanel, BorderLayout.CENTER);
        card.add(launchBtn, BorderLayout.SOUTH);

        return card;
    }
}
