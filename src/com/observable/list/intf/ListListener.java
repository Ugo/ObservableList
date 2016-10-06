package com.observable.list.intf;

import java.util.List;

/**
 * Interface that should implement any listener to an observable list
 * 
 * @param T
 *            type of the custom event that will be listened to
 */
public interface ListListener<T extends CustomEvent> {

	/**
	 * Method called by the source (to which the listener is attached) when an
	 * event occurs.
	 * 
	 * @param list
	 *            list on which the event has occurred
	 * 
	 * @param event
	 *            event received by the listener
	 */
	public void update(final List<?> list, T event);
}
