package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.actions.TicketChooseType;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginInformation;
import fr.maxlego08.zsupport.tickets.actions.TicketSelectPlugin;

public enum TicketStatus {

    CHOOSE_TYPE(TicketChooseType.class),
    CHOOSE_PLUGIN(TicketSelectPlugin.class),
    PLUGIN_INFORMATION(TicketPluginInformation.class),
    VERIFY_PURCHASE,
    SPIGOT_ACCESS,
    QUESTION,
    WAITING,
    OPEN,
    CLOSE,

    ;

    private final Class<? extends TicketAction> action;

    TicketStatus(Class<? extends TicketAction> action) {
        this.action = action;
    }

    TicketStatus() {
        this.action = null;
    }

    public TicketAction getAction() {
        if (this.action == null) return null;
        try {
            return this.action.getConstructor().newInstance();
        } catch (Exception exception) {
            return null;
        }
    }
}
