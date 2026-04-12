package aura.transaction;

import aura.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionLog {
    private final List<Transaction> transactions = new ArrayList<>();

    public void append(Transaction t) {
        transactions.add(t);
        PersistenceManager.getInstance().saveTransaction(t);
    }

    // PATTERN: Iterator — creates a filtered iterator without exposing the internal list
    public TransactionIterator iterator(String kioskIdFilter) {
        return new TransactionIterator(
            PersistenceManager.getInstance().loadTransactions(), kioskIdFilter
        );
    }
}
