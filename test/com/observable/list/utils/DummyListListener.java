package com.observable.list.utils;

import static com.observable.list.enums.ActionType.ADD;

import java.util.ArrayList;
import java.util.List;

import com.observable.list.ModifiedListEvent;
import com.observable.list.intf.ListListener;

/**
 * Dummy listener used in the unit test class. It simply says if it has been
 * notified or not, if the action was an Add or a Remove action.
 */
public class DummyListListener implements ListListener<ModifiedListEvent> {

	private boolean notified = false;
	private boolean isAdd = false;
	private boolean isRemove = false;
	private List<Object> removedElements = new ArrayList<>();
	private List<Object> addedElements = new ArrayList<>();

	/**
	 * Simply update the booleans depending on the event type and populate the
	 * lists accordingly.
	 */
	@Override
	public void update(List<?> list, ModifiedListEvent event) {
		notified = true;
		if (event.type == ADD) {
			isAdd = true;
			addedElements.addAll(event.elements);
		} else {
			isRemove = true;
			removedElements.addAll(event.elements);
		}
	}

	public boolean hasBeenNotified() {
		return notified;
	}

	public boolean isAddAction() {
		return isAdd;
	}

	public boolean isRemoveAction() {
		return isRemove;
	}

	public List<Object> getRemovedElements() {
		return removedElements;
	}

	public List<Object> getAddedElements() {
		return addedElements;
	}
}
