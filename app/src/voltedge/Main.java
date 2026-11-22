package voltedge;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import voltedge.ui.VoltEdgeFrame;

/**
 * Main application entry point for VoltEdge.
 * 
 * Demonstrates:
 * - Application lifecycle & entry point: public static void main(String[] args)
 * - Swing Thread-Safety: SwingUtilities.invokeLater to initialize GUI on the EDT
 * - Look and Feel initialization
 */
public class Main {

    public static void main(String[] args) {
        // Run GUI initialization on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply system look and feel for native window decorations
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fallback cleanly to default Swing appearance if system theme is unavailable
            }

            // Instantiate and display the main window
            VoltEdgeFrame frame = new VoltEdgeFrame();
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on monitor
            frame.setVisible(true);
        });
    }
}
