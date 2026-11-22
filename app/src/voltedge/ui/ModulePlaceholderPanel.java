package voltedge.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * ModulePlaceholderPanel is a reusable panel serving as a clean placeholder
 * for the calculator modules during Milestone 1.
 * 
 * Demonstrates:
 * - Inheritance (extends JPanel)
 * - Encapsulation (private fields, clean constructors)
 * - Component composition (nested panels with standard LayoutManagers)
 */
public class ModulePlaceholderPanel extends JPanel {

    private final String moduleId;
    private final String moduleTitle;
    private final String formulaPreview;
    private final String description;

    public ModulePlaceholderPanel(String moduleId, String moduleTitle, String formulaPreview, String description) {
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.formulaPreview = formulaPreview;
        this.description = description;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_MAIN);
        setBorder(AppTheme.BORDER_EMPTY_PADDING);

        // Header Panel (Title + Description)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(AppTheme.BG_MAIN);

        JLabel titleLabel = new JLabel(moduleTitle);
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JLabel descLabel = new JLabel("<html><p style='width: 600px; color: #64748B;'>" + description + "</p></html>");
        descLabel.setFont(AppTheme.FONT_BODY);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(descLabel);

        // Center Content Card (Formula Box + Milestone 2 Notice)
        JPanel centerCard = new JPanel();
        centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));
        centerCard.setBackground(AppTheme.BG_CARD);
        centerCard.setBorder(AppTheme.BORDER_CARD);

        // Formula badge
        JLabel formulaHeader = new JLabel("Key Formula / Model:");
        formulaHeader.setFont(AppTheme.FONT_SUBHEADER);
        formulaHeader.setForeground(AppTheme.COLOR_PRIMARY);

        JLabel formulaText = new JLabel(formulaPreview);
        formulaText.setFont(AppTheme.FONT_CODE);
        formulaText.setForeground(AppTheme.TEXT_DARK);
        formulaText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Placeholder notice
        JLabel noticeLabel = new JLabel("<html><b>Milestone 1 Active:</b> GUI shell & navigation ready.<br>"
                + "Calculation inputs, validators, and output displays will be connected in Milestone 2.</html>");
        noticeLabel.setFont(AppTheme.FONT_BODY);
        noticeLabel.setForeground(AppTheme.TEXT_MUTED);

        centerCard.add(formulaHeader);
        centerCard.add(Box.createRigidArea(new Dimension(0, 8)));
        centerCard.add(formulaText);
        centerCard.add(Box.createRigidArea(new Dimension(0, 18)));
        centerCard.add(noticeLabel);

        // Assemble
        add(headerPanel, BorderLayout.NORTH);
        add(centerCard, BorderLayout.CENTER);
    }

    public String getModuleId() {
        return moduleId;
    }
}
