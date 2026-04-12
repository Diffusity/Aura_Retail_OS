package aura.interfaces;

// PATTERN: Chain of Responsibility (Behavioral) — handler decides to handle or forward
public interface IFailureHandler {
    void setNext(IFailureHandler next);
    boolean handle(String failureType, String context);
}
