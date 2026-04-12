package aura.transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// PATTERN: Iterator (Behavioral) — traverses transaction log with filters without exposing storage
public class TransactionIterator implements java.util.Iterator<Map<String, Object>> {
    private final List<Map<String, Object>> transactions;
    private int cursor = 0;

    public TransactionIterator(List<Map<String, Object>> all, String kioskIdFilter) {
        this.transactions = all.stream()
            .filter(t -> kioskIdFilter == null || kioskIdFilter.equals(t.get("kioskId")))
            .collect(Collectors.toList());
    }

    @Override public boolean hasNext() { return cursor < transactions.size(); }
    @Override public Map<String, Object> next() { return transactions.get(cursor++); }

    public List<Map<String, Object>> getLastN(int n) {
        int start = Math.max(0, transactions.size() - n);
        return transactions.subList(start, transactions.size());
    }
}
