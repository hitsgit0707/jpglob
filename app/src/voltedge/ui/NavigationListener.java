package voltedge.ui;

/**
 * NavigationListener is a custom interface for handling navigation events
 * between panels in VoltEdge.
 * 
 * Demonstrates:
 * - Java Interfaces & Callback Pattern
 */
@FunctionalInterface
public interface NavigationListener {
    void navigateTo(String moduleKey);
}
