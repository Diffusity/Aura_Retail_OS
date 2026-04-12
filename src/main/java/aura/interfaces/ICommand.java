package aura.interfaces;

// PATTERN: Command (Behavioral) — encapsulates an operation with execute/undo/log
public interface ICommand {
    boolean execute();
    boolean undo();
    void log();
    String getCommandType();
}
