package com.observable.list;

import static com.observable.list.enums.ActionType.ADD;
import static com.observable.list.enums.ActionType.REMOVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.observable.list.intf.ListListener;
import com.observable.list.intf.Observable;

/**
 * This class is an extension of the ArrayList class. The difference is that it
 * allows listeners to register and unregister. If any modification is performed
 * on the list, all the listeners will be notified and will receive informations
 * on what has been modified.
 * 
 * @param <T>
 *            type of the objects contained in the list
 */
public class ObservableList<T> extends ArrayList<T> implements Observable<ModifiedListEvent> {

	private static final long serialVersionUID = 1L;

	/**
	 * list of listeners: synchronized list is used here to avoid registering
	 * and unregistering listeners while iterating on the listeners. As the
	 * standard ArrayList implementation is not synchronized a synchronized list
	 * is used here.
	 */
	private List<ListListener<ModifiedListEvent>> listeners = Collections
			.synchronizedList(new ArrayList<ListListener<ModifiedListEvent>>());

	/**
	 * Null listener exception message
	 */
	public final static String NULL_LISTENER_EXCEPTION = "Null Listener";

	/**
	 * Simple method to register a listener
	 * 
	 * @param listener
	 *            to be added to the Observable List
	 */
	@Override
	public void register(ListListener<ModifiedListEvent> listener) {
		if (listener == null) {
			throw new NullPointerException(NULL_LISTENER_EXCEPTION);
		}
		listeners.add(listener);
	}

	/**
	 * Simple method to unregister a listener
	 * 
	 * @param listener
	 *            to be removed from the Observable List
	 */
	@Override
	public void unregister(ListListener<ModifiedListEvent> listener) {
		if (listener == null) {
			throw new NullPointerException(NULL_LISTENER_EXCEPTION);
		}
		listeners.remove(listener);
	}

	/**
	 * @return the number of listeners currently registered in the list
	 */
	public int getNumberListeners() {
		return listeners.size();
	}

	/**
	 * Method to notify all the listeners that an action has been performed on
	 * the list.
	 * 
	 * @param event
	 *            event holds the details of the action that has been performed
	 *            on the list.
	 */
	@Override
	public void notifyAllListeners(ModifiedListEvent event) {

		// from the official java API for synchronized list:
		// it is imperative that the user manually synchronize the list when
		// iterating over it.
		synchronized (listeners) {
			Iterator<ListListener<ModifiedListEvent>> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				iterator.next().update(this, event);
			}
		}
	}

	/**
	 * Method to add one element to the list. It notifies the listeners
	 * accordingly.
	 */
	@Override
	public boolean add(T element) {
		boolean result = super.add(element);
		if (result) {
			notifyAllListeners(new ModifiedListEvent(ADD, element));
			return result;
		}
		return false;
	}

	/**
	 * Method to add one element to the list from a given index. It notifies the
	 * listeners accordingly.
	 */
	@Override
	public void add(int index, T element) {
		super.add(index, element);
		notifyAllListeners(new ModifiedListEvent(ADD, element));
	}

	/**
	 * Method to add several elements to the list. It notifies the listeners
	 * accordingly.
	 */
	@Override
	public boolean addAll(Collection<? extends T> elements) {
		boolean result = super.addAll(elements);
		if (result) {
			notifyAllListeners(new ModifiedListEvent(ADD, elements));
			return result;
		}

		return false;
	}

	/**
	 * Method to add several elements to the list from a given index. It
	 * notifies the listeners accordingly.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> elements) {
		boolean result = super.addAll(index, elements);
		if (result) {
			notifyAllListeners(new ModifiedListEvent(ADD, elements));
			return result;
		}
		return false;
	}

	/**
	 * Method to clear the list. All the elements will be removed. It notifies
	 * the listeners accordingly.
	 */
	@Override
	public void clear() {
		List<T> elementsToRemove = new ArrayList<>();
		elementsToRemove.addAll(this);
		super.clear();
		if (!elementsToRemove.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, elementsToRemove));
		}
	}

	/**
	 * Method to remove the element at the index ind in the list. It notifies
	 * the listeners accordingly.
	 */
	@Override
	public T remove(int ind) {
		T elementRemoved = super.remove(ind);
		notifyAllListeners(new ModifiedListEvent(REMOVE, elementRemoved));
		return elementRemoved;
	}

	/**
	 * Method to remove an element in the list. It notifies the listeners
	 * accordingly.
	 */
	@Override
	public boolean remove(Object element) {
		boolean result = super.remove(element);
		if (result) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, element));
			return result;
		}
		return false;
	}

	/**
	 * Method to remove several elements in the list. As there can be some
	 * elements that don't exist in the list, a check should be performed before
	 * to properly populate the notification. It notifies the listeners
	 * accordingly.
	 */
	@Override
	public boolean removeAll(Collection<?> elements) {
		List<Object> elementsToRemove = new ArrayList<>();
		for (Object elem : elements) {
			if (this.contains(elem)) {
				elementsToRemove.add(elem);
			}
		}
		boolean result = super.removeAll(elements);
		if (result && !elementsToRemove.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, elementsToRemove));
			return result;
		}

		return false;
	}

	/**
	 * This method replaces an element in the array list, so two notifications
	 * should be sent to the listeners. One event for the removal of one element
	 * in the list and one event for the addition of one element in the list.
	 */
	@Override
	public T set(int index, T element) {
		T oldValue = super.set(index, element);
		notifyAllListeners(new ModifiedListEvent(REMOVE, oldValue));
		notifyAllListeners(new ModifiedListEvent(ADD, element));

		return oldValue;
	}

	/**
	 * Method to remove all the elements in the list between two indexes. It
	 * notifies the listeners accordingly.
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		List<T> rangeElements = new ArrayList<T>(super.subList(fromIndex, toIndex));
		super.removeRange(fromIndex, toIndex);
		notifyAllListeners(new ModifiedListEvent(REMOVE, rangeElements));
	}

	/**
	 * Method to remove all the elements in the list that match the predicate
	 * given in parameter. It notifies the listeners accordingly.
	 * 
	 * Please note that this method is only available from Java8.
	 */
	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		List<T> elementsToRemove = new ArrayList<T>();
		for (T item : this) {
			if (filter.test(item)) {
				elementsToRemove.add(item);
			}
		}
		boolean result = super.removeIf(filter);

		if (result && !elementsToRemove.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, elementsToRemove));
			return result;
		}

		return false;
	}

	/**
	 * Method to replace all the elements in the list with the operator given in
	 * parameter. It notifies the listeners accordingly. Two notifications
	 * should be sent here since data are replaced, so one notifications for
	 * removal and one for addition.
	 * 
	 * Please note that this method is only available from Java8.
	 */
	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		List<T> removedElements = new ArrayList<T>();
		List<T> addedElements = new ArrayList<T>();
		// the check is performed on each item in the list to know if it will be
		// replaced or not
		for (T item : this) {
			T modifiedItem = operator.apply((T) item);
			if (!item.equals(modifiedItem)) {
				removedElements.add(item);
				addedElements.add(modifiedItem);
			}
		}
		super.replaceAll(operator);
		if (!removedElements.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, removedElements));
		}
		if (!addedElements.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(ADD, addedElements));
		}
	}

	/**
	 * Method to retain elements in the list that are only present in the list
	 * given in parameter, all the other elements will be removed from the list.
	 * The listeners will be notified accordingly.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		List<T> removedElements = new ArrayList<T>();
		for (T item : this) {
			if (c != null && !c.contains(item)) {
				removedElements.add(item);
			}
		}
		if (!removedElements.isEmpty()) {
			notifyAllListeners(new ModifiedListEvent(REMOVE, removedElements));
		}
		return super.retainAll(c);
	}
}
