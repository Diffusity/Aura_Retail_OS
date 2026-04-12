package aura.commands;

import aura.interfaces.ICommand;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// PATTERN: Command (Invoker, Behavioral) — executes, undoes, and logs commands
public class CommandInvoker {
    private final Deque<ICommand> history = new ArrayDeque<>();

    public synchronized boolean execute(ICommand command) {
        boolean success = command.execute();
        command.log();
        if (success) history.push(command);
        return success;
    }

    public boolean undoLast() {
        if (history.isEmpty()) return false;
        ICommand last = history.pop();
        return last.undo();
    }

    public List<ICommand> getHistory() { return new ArrayList<>(history); }
}
