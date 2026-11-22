package voltedge.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * AppTheme defines a unified color palette, typography, and spacing constants
 * across the entire VoltEdge application.
 * 
 * Standard Core Java concept: Static constants (public static final).
 */
public final class AppTheme {

    // Private constructor prevents instantiation of this utility class
    private AppTheme() {}

    // Primary Colors
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);      // Electric Blue #2563EB
    public static final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216);// Darker Blue #1D4ED8
    public static final Color COLOR_ACCENT = new Color(14, 165, 233);       // Cyan/Sky #0EA5E9

    // Background Colors
    public static final Color BG_MAIN = new Color(248, 250, 252);          // Light Slate #F8FAFC
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);          // Deep Slate/Navy #0F172A
    public static final Color BG_CARD = Color.WHITE;                       // Pure White
    public static final Color BG_NAV_HOVER = new Color(30, 41, 59);        // Slate #1E293B
    public static final Color BG_NAV_ACTIVE = new Color(37, 99, 235);       // Blue #2563EB

    // Text Colors
    public static final Color TEXT_DARK = new Color(15, 23, 42);           // #0F172A
    public static final Color TEXT_MUTED = new Color(100, 116, 139);       // #64748B
    public static final Color TEXT_LIGHT = new Color(241, 245, 249);       // Off-white #F1F5F9
    public static final Color TEXT_NAV = new Color(203, 213, 225);         // Light Slate #CBD5E1

    // Border Colors
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);      // #E2E8F0

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NAV = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 13);

    // Common Borders
    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_LIGHT, 1),
        BorderFactory.createEmptyBorder(16, 16, 16, 16)
    );

    public static final Border BORDER_EMPTY_PADDING = BorderFactory.createEmptyBorder(20, 24, 20, 24);
}
