package com.observable.list;

import static com.observable.list.enums.ActionType.ADD;
import static com.observable.list.enums.ActionType.REMOVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for multiple ObservableLists and TestListListeners interacting
 * together. This class doesn't focus on each method defined in the
 * {@link ObservableList} class but on the interaction between ObservableLists
 * and TestListListeners.
 * 
 * @see ObservableListTest for detailed tests on all the methods
 */
public class TestMultiple {

	private final ObservableList<Object> list1 = new ObservableList<>();
	private final ObservableList<Object> list2 = new ObservableList<>();
	private final TestListListener listener1 = new TestListListener("listen_1");
	private final TestListListener listener2 = new TestListListener("listen_1_and_2");
	private final TestListListener listener3 = new TestListListener("listen_2");

	/**
	 * Method that will be called before each test, it initializes the objects
	 * needed in the tests
	 */
	@Before
	public void beforeTestInit() {
		// register list1 with listener 1 and 2
		list1.register(listener1);
		list1.register(listener2);

		// register list2 with listener 2 and 3
		list2.register(listener2);
		list2.register(listener3);
	}

	/**
	 * Basic test on Add and Remove methods
	 */
	@Test
	public void testBasicAddRemove() {
		// basic add test
		list1.add("val1");
		// test the event is not null
		assertNotNull(listener1.getLastEventReceived());
		// test that both listener 1 and 2 have received the same event
		assertEquals(listener1.getLastEventReceived(), listener2.getLastEventReceived());
		// check the event
		assertEquals(listener1.getLastEventReceived().type, ADD);
		assertListEquals(listener1.getLastEventReceived().elements, Collections.singletonList("val1"));

		// basic remove test
		list1.remove("val1");
		// test the event is not null
		assertNotNull(listener1.getLastEventReceived());
		// test that both listeners have received the same event
		assertEquals(listener1.getLastEventReceived(), listener2.getLastEventReceived());
		// check the event
		assertEquals(listener1.getLastEventReceived().type, REMOVE);
		assertListEquals(listener1.getLastEventReceived().elements, Collections.singletonList("val1"));

		// basic add all test
		List<Object> tempList = new ArrayList<>(Arrays.asList("val1", "val2"));
		list2.addAll(tempList);
		// test the event that have been received by the 2 listeners
		assertNotNull(listener3.getLastEventReceived());
		// test that both listeners have received the same event
		assertEquals(listener2.getLastEventReceived(), listener3.getLastEventReceived());
		// check the event
		assertEquals(listener3.getLastEventReceived().type, ADD);
		assertListEquals(listener3.getLastEventReceived().elements, tempList);

		// basic remove all test
		list2.removeAll(tempList);
		// test the event that have been received by the 2 listeners
		assertNotNull(listener3.getLastEventReceived());
		// test that both listeners have received the same event
		assertEquals(listener2.getLastEventReceived(), listener3.getLastEventReceived());
		// check the event
		assertEquals(listener3.getLastEventReceived().type, REMOVE);
		assertListEquals(listener3.getLastEventReceived().elements, tempList);
	}

	/**
	 * A more complex test where a list of very different objects is copied from
	 * list1 to list2. The events are then checked when there is an add and
	 * remove.
	 */
	@Test
	public void testSeveralObjectAddRemove() {
		// populate first list with different kind of objects
		List<Object> tempList = Arrays.asList("String", 10, 4.0,
				new Date(System.currentTimeMillis()), new ObservableList<>(), new HashMap<String, String>(),
				listener3, this, Arrays.asList("test1", "test2"), "last");
		list1.addAll(tempList);

		// populate second list by removing first item in list1 until it is
		// empty
		int iter = 0;
		while (!list1.isEmpty()) {
			list2.add(list1.remove(0));

			// listener 1 and 2 should receive remove events and listeners 2 and
			// 3 should receive add events
			assertNotNull(listener1.getLastEventReceived());
			assertNotNull(listener2.getLastEventReceived());
			assertNotNull(listener3.getLastEventReceived());

			// check the on the last event for listener 1
			assertEquals(listener1.getLastEventReceived().type, REMOVE);
			assertListEquals(listener1.getLastEventReceived().elements, Collections.singletonList(tempList.get(iter)));

			// last event for listener 2 is the same as 3 since the add is
			// perform after the remove
			assertEquals(listener2.getLastEventReceived(), listener3.getLastEventReceived());
			assertEquals(listener2.getLastEventReceived().type, ADD);
			assertListEquals(listener2.getLastEventReceived().elements, Collections.singletonList(tempList.get(iter)));

			iter++;
		}

		// final check on the two lists
		assertListEquals(list1, Collections.emptyList());
		assertListEquals(list2, tempList);
	}

	/**
	 * This test will swap the two lists using the set method of the observable
	 * list. Checks are performed on the notifications during the swap as well.
	 * Checks are also performed on the lists to ensure the swap is correct.
	 */
	@Test
	public void testSwapTwoLists() {
		List<Object> firstList = Arrays.asList("val1", "val2", "val3", "val4", "val5", "val6");
		List<Object> secondList = Arrays.asList("test1", "test2", "test3", "test4", "test5", "test6");
		list1.addAll(firstList);
		list2.addAll(secondList);

		for (int iter = 0; iter < list1.size(); iter++) {
			// swap elements
			list1.set(iter, list2.set(iter, list1.get(iter)));

			// check the last event for each listener (in the set method a
			// REMOVE then an ADD event is sent so it is expected that the last
			// event will be an ADD)

			// listener1 (and 2)
			assertNotNull(listener1.getLastEventReceived());
			assertEquals(listener1.getLastEventReceived(), listener2.getLastEventReceived());
			assertEquals(listener1.getLastEventReceived().type, ADD);
			assertListEquals(listener1.getLastEventReceived().elements,
					Collections.singletonList(secondList.get(iter)));

			// listener3
			assertNotNull(listener3.getLastEventReceived());
			assertEquals(listener3.getLastEventReceived().type, ADD);
			assertListEquals(listener3.getLastEventReceived().elements, Collections.singletonList(firstList.get(iter)));
		}

		// check that the two lists have been swapped correctly
		assertListEquals(list1, secondList);
		assertListEquals(list2, firstList);
	}

	/**
	 * This test defines one listener and many ObservableList. The listener is
	 * registered to all the ObservableList objects. A different element will be
	 * added in each ObservableList. The notification received by the listener
	 * is then tested.
	 */
	@Test
	public void testOneListenerManyObservables() {
		int numberObservable = 100;
		TestListListener theListener = new TestListListener("theListener");
		List<ObservableList<String>> listObservable = new ArrayList<>();
		for (int iter = 0; iter < numberObservable; iter++) {
			ObservableList<String> observObj = new ObservableList<>();
			observObj.register(theListener);
			listObservable.add(observObj);
		}

		// add objects in each Observable list and check the notification in
		// the listener
		List<String> tempList = new ArrayList<>();
		for (int iter = 0; iter < numberObservable; iter++) {
			String str = "val_" + iter;
			tempList.add(str);
			listObservable.get(iter).addAll(tempList);

			// check notification
			assertNotNull(theListener.getLastEventReceived());
			assertEquals(theListener.getLastEventReceived().type, ADD);
			assertListEquals(theListener.getLastEventReceived().elements, new ArrayList<>(tempList));
		}
	}

	/**
	 * This test defines one ObservableList and many listeners. The listeners
	 * are all registered to the ObservableList. Some elements are added to the
	 * ObservableList, then all the notifications received by the listener are
	 * checked.
	 */
	@Test
	public void testOneObservableManyListeners() {
		// create all listeners and the observable
		int numberListener = 100;
		List<TestListListener> listeners = new ArrayList<>();
		ObservableList<String> observableObj = new ObservableList<>();
		for (int iter = 0; iter < numberListener; iter++) {
			TestListListener listener = new TestListListener("theListener_" + iter);
			listeners.add(listener);
			observableObj.register(listener);
		}

		// add some values
		List<String> tempList = Arrays.asList("val1", "val2");
		observableObj.addAll(tempList);

		// check the notifications
		for (TestListListener listener : listeners) {
			assertNotNull(listener.getLastEventReceived());
			assertEquals(listener.getLastEventReceived().type, ADD);
			assertListEquals(listener.getLastEventReceived().elements, new ArrayList<>(tempList));
		}
	}

	/**
	 * Tool method to assert that two lists are equal.
	 * 
	 * @param list1
	 *            first list to compare
	 * @param list2
	 *            second list to compare
	 */
    private void assertListEquals(List<Object> list1, List<Object> list2) {
		assertEquals(new Integer(list1.size()), new Integer(list2.size()));
		assertTrue(list1.equals(list2));
	}

	/**
	 * Tool method to assert that two lists are equal.
	 * 
	 * @param list1
	 *            first list to compare
	 * @param list2
	 *            second list to compare
	 */
    private void assertListEquals(Collection<?> list1, List<Object> list2) {
		assertListEquals(new ArrayList<>(list1), list2);
	}

}
