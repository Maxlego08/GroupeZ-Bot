package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.actions.TicketChooseType;
import fr.maxlego08.zsupport.tickets.actions.TicketOpen;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginInformation;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginNeedVerification;
import fr.maxlego08.zsupport.tickets.actions.TicketPluginVerifyPurchase;
import fr.maxlego08.zsupport.tickets.actions.TicketQuestionModal;
import fr.maxlego08.zsupport.tickets.actions.TicketQuestionOpen;
import fr.maxlego08.zsupport.tickets.actions.TicketSelectPlugin;
import fr.maxlego08.zsupport.tickets.actions.TicketSpigotModal;
import fr.maxlego08.zsupport.tickets.actions.TicketSpigotOpen;
import fr.maxlego08.zsupport.tickets.actions.TicketVerification;
import fr.maxlego08.zsupport.tickets.actions.TicketVerificationPlugin;
import fr.maxlego08.zsupport.tickets.actions.TicketZMenuClose;
import fr.maxlego08.zsupport.tickets.actions.TicketZMenuVerification;

public enum TicketStatus {

    CHOOSE_TYPE(TicketChooseType.class, "waiting"),
    CHOOSE_PLUGIN(TicketSelectPlugin.class),
    PLUGIN_VERIFY_PURCHASE(TicketPluginVerifyPurchase.class),
    PLUGIN_INFORMATION(TicketPluginInformation.class),
    PLUGIN_VERIFY_NEED_INFORMATION(TicketPluginNeedVerification.class),
    VERIFY_PURCHASE(TicketVerification.class, "#%id%-verify"),
    VERIFY_PURCHASE_PLUGIN(TicketVerificationPlugin.class),
    VERIFY_ZMENU_PURCHASE(TicketZMenuVerification.class, "#%id%-zmenu"),
    VERIFY_ZMENU_CLOSE(TicketZMenuClose.class),
    SPIGOT_ACCESS_MODAL(TicketSpigotModal.class, "#%id%-spigot"),
    SPIGOT_ACCESS(TicketSpigotOpen.class),
    QUESTION_MODAL(TicketQuestionModal.class, "#%id%-question"),
    QUESTION(TicketQuestionOpen.class),
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
