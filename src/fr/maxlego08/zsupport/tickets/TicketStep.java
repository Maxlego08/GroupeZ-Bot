package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.tickets.steps.TicketChoosePlugin;
import fr.maxlego08.zsupport.tickets.steps.TicketOrder;
import fr.maxlego08.zsupport.tickets.steps.TicketPlugin;
import fr.maxlego08.zsupport.tickets.steps.TicketSpigot;
import fr.maxlego08.zsupport.tickets.steps.TicketTypeStep;

public enum TicketStep {

	CHOOSE_TICKET_TYPE(new TicketTypeStep()), 
	
	CHOOSE_PLUGIN(new TicketChoosePlugin()),
	
	CHOOSE_SPIGOT(new TicketSpigot()),
	
	ORDER(new TicketOrder()),
	
	PLUGIN(new TicketPlugin()),
	
	;
	
	private final Step step;

	/**
	 * @param step
	 */
	private TicketStep(Step step) {
		this.step = step;
	}

	/**
	 * @return the step
	 */
	public Step getStep() {
		return step.clone();
	}
	
	
	
}
