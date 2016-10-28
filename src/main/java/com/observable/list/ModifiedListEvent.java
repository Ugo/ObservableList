package com.observable.list;

import com.observable.list.enums.ActionType;
import com.observable.list.intf.CustomEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * Class for the event that can open on a list. It defines the action that has
 * been performed on the list and the elements that have been used for that
 * action.
 * 
 * @see ActionType
 * @see CustomEvent
 */
public class ModifiedListEvent implements CustomEvent {
	// type of action performed in the event
	public final ActionType type;
	// elements added or removed in the event
	public final Collection<?> elements;

	/**
	 * Simple constructor for a single element
	 * 
	 * @param type
	 *            the action type performed by the event
	 * @param element
	 *            the element used in the event
	 */
	public ModifiedListEvent(ActionType type, Object element) {
		this(type, Collections.singletonList(element));
	}

	/**
	 * Constructor for multiple elements
	 * 
	 * @param type
	 *            the action type performed by the event
	 * @param elements
	 *            the elements used in the event
	 */
	public ModifiedListEvent(ActionType type, Collection<?> elements) {
		this.type = type;
		this.elements = elements;
	}
}
