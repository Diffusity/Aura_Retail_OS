package aura.transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionCaretaker {
    private final Map<String, TransactionMemento> mementos = new HashMap<>();

    public void save(String txnId, TransactionMemento memento) { mementos.put(txnId, memento); }
    public TransactionMemento restore(String txnId) { return mementos.get(txnId); }
    public void clear(String txnId) { mementos.remove(txnId); }
}
