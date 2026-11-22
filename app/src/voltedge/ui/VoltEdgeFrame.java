package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import voltedge.model.CalculationHistory;

/**
 * VoltEdgeFrame is the top-level window for the VoltEdge application.
 * 
 * Demonstrates:
 * - GUI Architecture: JFrame with BorderLayout
 * - Navigation Pattern: CardLayout with a Sidebar control
 * - Event Handling: ActionListener callbacks
 * - Collections: Map<String, JButton> for managing active button states
 * - Centralized CalculationHistory management
 */
public class VoltEdgeFrame extends JFrame implements NavigationListener {

    // Unique Card Identifiers
    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_OHMS_LAW = "ohms_law";
    public static final String CARD_POWER = "power";
    public static final String CARD_RESISTORS = "resistors";
    public static final String CARD_VOLTAGE_DIVIDER = "voltage_divider";
    public static final String CARD_CAPACITORS = "capacitors";
    public static final String CARD_CONVERTER = "converter";
    public static final String CARD_HISTORY = "history";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new HashMap<>();
    private final CalculationHistory history = new CalculationHistory();
    private String currentCard = CARD_DASHBOARD;

    public VoltEdgeFrame() {
        super("VoltEdge — Electrical Engineering Calculator");
        initializeWindow();
        buildUI();
        navigateTo(CARD_DASHBOARD);
    }

    private void initializeWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 560));
        setPreferredSize(new Dimension(1020, 660));
        setLocationRelativeTo(null); // Center on screen
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout());

        // 1. Sidebar on WEST
        JPanel sidebar = createSidebar();
        getContentPane().add(sidebar, BorderLayout.WEST);

        // 2. Central Content Area (CardLayout)
        setupContentCards();
        getContentPane().add(contentCards, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(AppTheme.BG_SIDEBAR);

        // Sidebar Header / Branding
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(AppTheme.BG_SIDEBAR);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(22, 18, 22, 18));

        JLabel brandTitle = new JLabel("VoltEdge");
        brandTitle.setFont(AppTheme.FONT_TITLE);
        brandTitle.setForeground(AppTheme.TEXT_LIGHT);

        JLabel brandSubtitle = new JLabel("EE Calculator Suite");
        brandSubtitle.setFont(AppTheme.FONT_SMALL);
        brandSubtitle.setForeground(AppTheme.COLOR_ACCENT);

        brandPanel.add(brandTitle);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        brandPanel.add(brandSubtitle);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Sidebar Navigation Links
        JPanel navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setBackground(AppTheme.BG_SIDEBAR);
        navList.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        navList.add(createNavButton("Dashboard", CARD_DASHBOARD));
        navList.add(Box.createRigidArea(new Dimension(0, 6)));
        navList.add(createNavButton("1. Ohm's Law", CARD_OHMS_LAW));
        navList.add(createNavButton("2. Electrical Power", CARD_POWER));
        navList.add(createNavButton("3. Resistor Networks", CARD_RESISTORS));
        navList.add(createNavButton("4. Voltage Divider", CARD_VOLTAGE_DIVIDER));
        navList.add(createNavButton("5. Capacitor Networks", CARD_CAPACITORS));
        navList.add(createNavButton("6. Unit Converter", CARD_CONVERTER));
        navList.add(Box.createRigidArea(new Dimension(0, 10)));
        navList.add(createNavButton("📜 History Log", CARD_HISTORY));

        sidebar.add(navList, BorderLayout.CENTER);

        // Sidebar Footer / System Info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(AppTheme.BG_SIDEBAR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 12, 0));

        JLabel footerLabel = new JLabel("VoltEdge v1.0 • Java 17");
        footerLabel.setFont(AppTheme.FONT_SMALL);
        footerLabel.setForeground(AppTheme.TEXT_MUTED);
        footerPanel.add(footerLabel);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String label, String cardKey) {
        JButton btn = new JButton(label);
        btn.setFont(AppTheme.FONT_NAV);
        btn.setForeground(AppTheme.TEXT_NAV);
        btn.setBackground(AppTheme.BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        btn.addActionListener(e -> navigateTo(cardKey));

        navButtons.put(cardKey, btn);
        return btn;
    }

    private void setupContentCards() {
        // Dashboard card
        contentCards.add(new DashboardPanel(this), CARD_DASHBOARD);

        // Modules 1 to 6 (Integrated with persistent CalculationHistory)
        contentCards.add(new OhmsLawPanel(history), CARD_OHMS_LAW);
        contentCards.add(new PowerCalculatorPanel(history), CARD_POWER);
        contentCards.add(new ResistorCalculatorPanel(history), CARD_RESISTORS);
        contentCards.add(new VoltageDividerPanel(history), CARD_VOLTAGE_DIVIDER);
        contentCards.add(new CapacitorCalculatorPanel(history), CARD_CAPACITORS);
        contentCards.add(new UnitConverterPanel(history), CARD_CONVERTER);

        // Calculation History Panel
        contentCards.add(new HistoryPanel(history), CARD_HISTORY);
    }

    @Override
    public void navigateTo(String cardKey) {
        if (!navButtons.containsKey(cardKey)) {
            return;
        }

        // Switch visible card
        cardLayout.show(contentCards, cardKey);
        currentCard = cardKey;

        // Update active nav button appearance
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton button = entry.getValue();
            if (entry.getKey().equals(cardKey)) {
                button.setBackground(AppTheme.BG_NAV_ACTIVE);
                button.setForeground(AppTheme.TEXT_LIGHT);
            } else {
                button.setBackground(AppTheme.BG_SIDEBAR);
                button.setForeground(AppTheme.TEXT_NAV);
            }
        }
    }
}
