package com.observable.list.intf;

/**
 * Interface for any List that can be observed
 */
public interface Observable<T extends CustomEvent> {

	/**
	 * Method to register a listener to the Observable
	 * 
	 * @param listener
	 *            listener that will be registered
	 */
	public void register(ListListener<T> listener);

	/**
	 * Method to unregister a listener to the Observable
	 * 
	 * @param listener
	 *            listener that will be unregistered
	 */
	public void unregister(ListListener<T> listener);

	/**
	 * Method called by the Observable to notify all the listeners that an event
	 * has occurred.
	 * 
	 * @param event
	 *            event that has occurred on the Observable
	 */
	public void notifyAllListeners(T event);
}
