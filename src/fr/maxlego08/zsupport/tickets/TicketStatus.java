package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.actions.TicketChooseType;
import fr.maxlego08.zsupport.tickets.actions.TicketOpen;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginInformation;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginNeedVerification;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginVerifyPurchase;
import fr.maxlego08.zsupport.tickets.actions.TicketSelectPlugin;

public enum TicketStatus {

    // Choisir le type du ticket
    CHOOSE_TYPE(TicketChooseType.class, "waiting"),
    // Choisir le plugin
    CHOOSE_PLUGIN(TicketSelectPlugin.class),
    // Vérifier si le client a acheté le plugin
    PLUGIN_VERIFY_PURCHASE(TicketPluginVerifyPurchase.class),
    // Si le plugin est acheté, Demander les informations pour le support
    PLUGIN_INFORMATION(TicketPluginInformation.class),
    PLUGIN_VERIFY_NEED_INFORMATION(TicketPluginNeedVerification.class),
    VERIFY_PURCHASE,
    SPIGOT_ACCESS,
    QUESTION,
    WAITING,
    OPEN(TicketOpen.class, "#%id%-%plugin%"),
    CLOSE("#%id%-close"),

    ;

    private final Class<? extends TicketAction> action;
    private final String channelName;

    TicketStatus(Class<? extends TicketAction> action, String channelName) {
        this.action = action;
        this.channelName = channelName;
    }

    TicketStatus(Class<? extends TicketAction> action) {
        this.action = action;
        this.channelName = null;
    }

    TicketStatus(String channelName) {
        this.action = null;
        this.channelName = channelName;
    }

    TicketStatus() {
        this.action = null;
        this.channelName = null;
    }

    public TicketAction getAction() {
        if (this.action == null) return null;
        try {
            return this.action.getConstructor().newInstance();
        } catch (Exception exception) {
            return null;
        }
    }

    public String getChannelName() {
        return channelName;
    }
}
