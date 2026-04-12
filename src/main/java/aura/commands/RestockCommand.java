package aura.commands;

import aura.interfaces.ICommand;
import aura.kiosk.BaseKiosk;

public class RestockCommand implements ICommand {
    private final String productId;
    private final int qty;
    private final BaseKiosk kiosk;

    public RestockCommand(String productId, int qty, BaseKiosk kiosk) {
        this.productId = productId;
        this.qty = qty;
        this.kiosk = kiosk;
    }

    @Override
    public boolean execute() {
        if (!kiosk.getState().allowsRestock()) {
            System.out.println("[RestockCommand] Restock blocked by current state: " + kiosk.getState().getStateName());
            return false;
        }
        System.out.println("[RestockCommand] Restocking " + productId + " qty=" + qty);
        return true;
    }

    @Override public boolean undo() { return false; }
    @Override public void log() { System.out.println("[RestockCommand] product=" + productId + " qty=" + qty); }
    @Override public String getCommandType() { return "RESTOCK"; }
}
