package com.observable.list;

import java.util.List;

import com.observable.list.intf.ListListener;

/**
 * Class implementing the ListListener interface. The source will trigger the
 * update method of the object to notify it that the source has been modified.
 */
public class TestListListener implements ListListener<ModifiedListEvent> {

	private final static String ITEMS_SEPARATOR = ",";
	private final static String ITEMS_LIST_DESC = ":";

	/**
	 * Name of the listener
	 */
    private final String listenerName;

	/**
	 * Keep the last event received by the listener
	 */
	private ModifiedListEvent lastEventReceived;

	/**
	 * Constructor setting the name.
	 * 
	 * @param name
	 *            name that will be given to the listener
	 */
	public TestListListener(String name) {
		this.listenerName = name;
	}

	/**
	 * This method only prints the details of the event just received and update
	 * the last event received.
	 */
	@Override
	public void update(List<?> list, ModifiedListEvent event) {
		this.lastEventReceived = event;

		// concatenate elements of the event
		StringBuilder strBuff = new StringBuilder();
		for (Object elem : event.elements) {
			strBuff.append(elem).append(" ");
		}
		
		// print the details of the event
		System.out.println("Listener" + ITEMS_LIST_DESC + listenerName + ITEMS_SEPARATOR + " Action" + ITEMS_LIST_DESC
				+ event.type + ITEMS_SEPARATOR + " elements" + ITEMS_LIST_DESC + strBuff.toString() + ITEMS_SEPARATOR
				+ " size of list" + ITEMS_LIST_DESC + list.size());
	}

	/**
	 * Simple getter for the last event received by the listener.
	 * 
	 * @return the last event received by the listener.
	 */
	public ModifiedListEvent getLastEventReceived() {
		return lastEventReceived;
	}
}
