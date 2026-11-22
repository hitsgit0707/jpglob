package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import voltedge.model.CalculationHistory;
import voltedge.model.HistoryRecord;

/**
 * HistoryPanel provides the user interface for viewing and managing
 * the persistent log of circuit calculations.
 * 
 * Demonstrates:
 * - Swing JTable with DefaultTableModel
 * - CardLayout for switching between Table view and Empty State view
 * - Event Handling: ActionListeners and HistoryChangeListener observer
 * - Formatted tabular data display
 */
public class HistoryPanel extends JPanel {

    private static final String CARD_TABLE = "table";
    private static final String CARD_EMPTY = "empty";

    private final CalculationHistory history;
    private final CardLayout viewCardLayout = new CardLayout();
    private final JPanel viewContainer = new JPanel(viewCardLayout);

    private DefaultTableModel tableModel;
    private JTable historyTable;
    private JLabel countBadge;

    public HistoryPanel(CalculationHistory history) {
        this.history = history;
        initializeUI();

        // Register observer to auto-refresh whenever history changes
        history.addChangeListener(this::refreshTable);
        refreshTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(AppTheme.BORDER_EMPTY_PADDING);

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Container (Switches between Table and Empty View)
        viewContainer.setBackground(AppTheme.BG_MAIN);
        viewContainer.add(createTableCard(), CARD_TABLE);
        viewContainer.add(createEmptyCard(), CARD_EMPTY);
        add(viewContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_MAIN);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Title and description on the left
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(AppTheme.BG_MAIN);

        JLabel titleLabel = new JLabel("Calculation History");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JLabel descLabel = new JLabel("Audit log of all completed calculations. Persisted locally to " + CalculationHistory.DEFAULT_FILE_NAME);
        descLabel.setFont(AppTheme.FONT_BODY);
        descLabel.setForeground(AppTheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(descLabel);

        // Actions & count badge on the right
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(AppTheme.BG_MAIN);

        countBadge = new JLabel("Records: 0");
        countBadge.setFont(AppTheme.FONT_SUBHEADER);
        countBadge.setForeground(AppTheme.COLOR_PRIMARY);

        JButton clearBtn = new JButton("Clear History");
        clearBtn.setFont(AppTheme.FONT_NAV);
        clearBtn.setBackground(new Color(239, 68, 68)); // Red #EF4444
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        clearBtn.addActionListener(e -> promptClearHistory());

        actionPanel.add(countBadge);
        actionPanel.add(clearBtn);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_CARD);
        panel.setBorder(AppTheme.BORDER_CARD);

        String[] columns = {"#", "Timestamp", "Module", "Inputs / Parameters", "Result"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(AppTheme.FONT_BODY);
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(AppTheme.FONT_SUBHEADER);
        historyTable.getTableHeader().setBackground(new Color(241, 245, 249));
        historyTable.getTableHeader().setForeground(AppTheme.TEXT_DARK);
        historyTable.setGridColor(AppTheme.BORDER_LIGHT);
        historyTable.setShowGrid(true);

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(280);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        // Center align index and timestamp
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmptyCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.BG_CARD);
        panel.setBorder(AppTheme.BORDER_CARD);

        JLabel emptyTitle = new JLabel("No Calculations Recorded Yet");
        emptyTitle.setFont(AppTheme.FONT_HEADER);
        emptyTitle.setForeground(AppTheme.TEXT_MUTED);
        emptyTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emptySubtitle = new JLabel("Perform any calculation in the modules (Ohm's Law, Power, Resistors, etc.) and it will appear here.");
        emptySubtitle.setFont(AppTheme.FONT_BODY);
        emptySubtitle.setForeground(AppTheme.TEXT_MUTED);
        emptySubtitle.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(emptyTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(emptySubtitle);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<HistoryRecord> records = history.getRecords();

        if (records.isEmpty()) {
            countBadge.setText("Records: 0");
            viewCardLayout.show(viewContainer, CARD_EMPTY);
            return;
        }

        // Show latest entries first (reverse chronological order)
        for (int i = records.size() - 1; i >= 0; i--) {
            HistoryRecord r = records.get(i);
            tableModel.addRow(new Object[]{
                i + 1,
                r.getTimestamp(),
                r.getModuleName(),
                r.getInputDetails(),
                r.getResult()
            });
        }

        countBadge.setText("Records: " + records.size());
        viewCardLayout.show(viewContainer, CARD_TABLE);
    }

    private void promptClearHistory() {
        if (history.size() == 0) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all calculation history records?\nThis will also remove them from " + CalculationHistory.DEFAULT_FILE_NAME + ".",
            "Confirm Clear History",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            history.clearHistory();
        }
    }
}
