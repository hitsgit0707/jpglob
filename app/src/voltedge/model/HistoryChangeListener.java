package voltedge.model;

/**
 * HistoryChangeListener defines a callback contract for components
 * that need to be notified when the calculation history is modified.
 * 
 * Demonstrates:
 * - Java Interfaces
 * - Observer / Event Listener pattern
 */
@FunctionalInterface
public interface HistoryChangeListener {
    void onHistoryChanged();
}
