package com.observable.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.observable.list.ObservableList;
import com.observable.list.utils.DummyListListener;

/**
 * Test class to unit test all the methods of the ObservableList class.
 */
public class ObservableListTest {

	/**
	 * Define all the objects that will be used in the tests.
	 */
	ObservableList<Object> list;
	DummyListListener dummyListener;
	DummyListListener dummyListener2;
	List<Object> threeElemList = new ArrayList<Object>(Arrays.asList("elem1", "elem2", "elem3"));
	List<Object> fiveElemList = new ArrayList<Object>(Arrays.asList("elem1", "elem2", "elem3", "elem4", "elem5"));
	List<Object> emptyList = Collections.emptyList();
	
	// to remove end
	
	/**
	 * Method called before each test, it initializes the objects needed in the
	 * tests
	 */
	@Before
	public void beforeTestInit() {
		list = new ObservableList<Object>();
		dummyListener = new DummyListListener();
		dummyListener2 = new DummyListListener();
	}

	/**
	 * Basic test for the register method
	 */
	@Test
	public void testRegister() {
		list.register(dummyListener);
		assertListNumberListenersEquals(1);

		list.register(dummyListener2);
		assertListNumberListenersEquals(2);
	}

	/**
	 * Basic test for the unregister method
	 */
	@Test
	public void testUnregister() {
		list.register(dummyListener);
		list.register(dummyListener2);
		assertListNumberListenersEquals(2);

		list.unregister(dummyListener2);
		assertListNumberListenersEquals(1);

		list.unregister(dummyListener);
		assertListNumberListenersEquals(0);
	}

	/**
	 * Test of the case where non registered listener is unregistered, it should
	 * not do anything particular (no error, no exception)
	 */
	@Test
	public void testUnregisterWrongListener() {
		list.register(dummyListener);
		assertListNumberListenersEquals(1);

		list.unregister(dummyListener2);
		assertListNumberListenersEquals(1);
	}

	/**
	 * Register a null listener: an exception should be thrown
	 */
	@Test(expected = NullPointerException.class)
	public void testRegisterNullListener() {
		list.register(null);
	}

	/**
	 * Unregister a null listener: an exception should be thrown
	 */
	@Test(expected = NullPointerException.class)
	public void testUnregisterNullListener() {
		list.unregister(null);
	}
	
	/**
	 * Test the add(T) method, it should:
	 * <ul>
	 * <li>add one element to the list</li>
	 * <li>notify the listeners of an add action</li>
	 * </ul>
	 */
	@Test
	public void testAdd() {
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		// test on a normal value
		Object temp = new Object();
		list.add(temp);

		assertAddActionHappened();
		assertAddedListEqualsTo(Arrays.asList(temp));
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(Arrays.asList(temp));

		// test on a null value
		list.unregister(dummyListener);
		list = new ObservableList<Object>();
		dummyListener = new DummyListListener();
		list.register(dummyListener);
		list.add(null);

		assertAddActionHappened();
		assertAddedListEqualsTo(Collections.singletonList(null));
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(Collections.singletonList(null));
	}

	/**
	 * Test the add(int index, T element) method, it should:
	 * <ul>
	 * <li>add one element to the list at the correct index</li>
	 * <li>notify the listeners of an add action</li>
	 * </ul>
	 */
	@Test
	public void testAddIndex() {
		String obj1 = "obj1";
		list.add(obj1);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);

		String obj2 = "obj2";
		list.add(0, obj2);

		assertAddActionHappened();
		assertAddedListEqualsTo(Arrays.asList(obj2));
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(Arrays.asList(obj2, obj1));
	}

	/**
	 * Test the addAll(Collection<? extends T> elements) method, it should:
	 * <ul>
	 * <li>add all elements to the list</li>
	 * <li>notify the listeners of an add action</li>
	 * </ul>
	 */
	@Test
	public void testAddAll() {
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);

		list.addAll(threeElemList);

		assertAddActionHappened();
		assertAddedListEqualsTo(threeElemList);
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(threeElemList);
	}

	/**
	 * Test the addAll(Collection<? extends T> elements) method, it should:
	 * <ul>
	 * <li>add all elements to the list at the correct index</li>
	 * <li>notify the listeners of an add action</li>
	 * </ul>
	 */
	@Test
	public void testAddAllIndex() {
		Object temp = new Object();
		list.add(temp);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);

		list.addAll(0, threeElemList);

		assertAddActionHappened();
		List<Object> tempList = new ArrayList<Object>();
		tempList.addAll(threeElemList);
		tempList.add(temp);
		assertAddedListEqualsTo(threeElemList);
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(tempList);
	}

	/**
	 * Test the clear() method, it should:
	 * <ul>
	 * <li>clear all the elements of the list</li>
	 * <li>notify the listeners of an add action</li>
	 * </ul>
	 */
	@Test
	public void testClear() {

		list.addAll(fiveElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);

		list.clear();

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(fiveElemList);
		assertListEqualsTo(emptyList);
	}

	/**
	 * Test the remove(int ind) method, it should:
	 * <ul>
	 * <li>remove the element in the list at the correct index</li>
	 * <li>notify the listeners of a remove action</li>
	 * </ul>
	 */
	@Test
	public void testRemoveIndex() {
		list.addAll(threeElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.remove(1);

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(Collections.singletonList((Object) "elem2"));
		assertListEqualsTo(Arrays.asList("elem1", "elem3"));
	}

	/**
	 * Test the remove(Object element) method, it should:
	 * <ul>
	 * <li>remove the element in the list given as a parameter</li>
	 * <li>notify the listeners of a remove action</li>
	 * </ul>
	 */
	@Test
	public void testRemove() {
		list.addAll(threeElemList);

		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.remove("elem2");

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(Collections.singletonList((Object) "elem2"));
		assertListEqualsTo(Arrays.asList("elem1", "elem3"));
	}

	/**
	 * Test the remove(Object element) method with an object that doesn't exist
	 * in the list, it should not modify the list and no notification should be
	 * sent.
	 */
	@Test
	public void testRemoveWrongObject() {
		list.addAll(threeElemList);

		list.register(dummyListener);
		list.remove("dummyObject");

		assertNothingHappened(threeElemList);
	}

	/**
	 * Test the removeAll(Collection<?> elements) method, it should:
	 * <ul>
	 * <li>remove all the elements in the list given in parameter</li>
	 * <li>notify the listeners of a remove action</li>
	 * </ul>
	 */
	@Test
	public void testRemoveAll() {
		list.addAll(fiveElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.removeAll(threeElemList);

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(threeElemList);
		assertListEqualsTo(Arrays.asList("elem4", "elem5"));
	}

	/**
	 * Test the removeAll(Collection<?> elements) method, with elements that
	 * don't exist in the list. The list should not be modified and no
	 * notification should be sent.
	 */
	@Test
	public void testRemoveAllWrong() {
		list.addAll(fiveElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.removeAll(Arrays.asList("elem6", "elem7", "elem8"));

		assertNothingHappened(fiveElemList);
	}

	/**
	 * Test the removeAll(Collection<?> elements) method, with elements that
	 * both exist and don't exist in the list. Only the elements existing in the
	 * list should be removed. Listeners should be notified.
	 */
	@Test
	public void testRemoveAllHalfWrong() {
		list.addAll(fiveElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.removeAll(Arrays.asList("elem1", "elem2", "elem7", "elem8"));

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(Arrays.asList("elem1", "elem2"));
		assertListEqualsTo(Arrays.asList("elem3", "elem4", "elem5"));
	}

	/**
	 * Test the set(int index, T element) method, it should:
	 * <ul>
	 * <li>set the element given in parameter at the index given in parameter
	 * </li>
	 * <li>notify the listeners of a remove action and of an add action (since
	 * an element is replaced in the list</li>
	 * </ul>
	 */
	@Test
	public void testSet() {
		list.addAll(threeElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		String newElem = "newElem2";
		list.set(1, newElem);

		assertBothActionsHappened();
		assertAddedListEqualsTo(Arrays.asList(newElem));
		assertRemovedListEqualsTo(Arrays.asList("elem2"));
		assertListEqualsTo(Arrays.asList("elem1", newElem, "elem3"));
		assertEquals(list.get(1), newElem);
	}

	/**
	 * Test the removeRange(int fromIndex, int toIndex) method, it should:
	 * <ul>
	 * <li>remove all the elements in the list between fromIndex and toIndex
	 * </li>
	 * <li>notify the listeners of a remove action</li>
	 * </ul>
	 */
	@Test
	public void testRemoveRange() {
		list.addAll(fiveElemList);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.removeRange(1, 3);

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(Arrays.asList("elem2", "elem3"));
		assertListEqualsTo(Arrays.asList("elem1", "elem4", "elem5"));
	}

	/**
	 * Test the removeIf(Predicate<? super T> filter) method, it should:
	 * <ul>
	 * <li>remove all the elements that match the predicate in the list</li>
	 * <li>notify the listeners of a remove action</li>
	 * </ul>
	 */
	@Test
	public void testRemoveIf() {
		list.addAll(threeElemList);
		Integer testInt = new Integer(10);
		list.add(testInt);

		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.removeIf(new IsStringPredicate());
		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(threeElemList);
		assertListEqualsTo(Arrays.asList(testInt));
	}

	/**
	 * Simple Predicate to test if an Object is a String or not, this will be
	 * used only in the testRemoveIf() method
	 */
	class IsStringPredicate implements Predicate<Object> {
		@Override
		public boolean test(Object t) {
			if (t != null && t instanceof String) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Test the removeIf(Predicate<? super T> filter) method, when no element in
	 * the list match the predicate. The list should not be modified and no
	 * notification should be sent.
	 */
	@Test
	public void testRemoveIfNoMatch() {
		list.addAll(fiveElemList);
		list.register(dummyListener);
		list.removeIf(new Predicate<Object>() {
			// always return false i.e the match never happens.
			@Override
			public boolean test(Object t) {
				return false;
			}
		});

		assertNothingHappened(fiveElemList);
	}

	/**
	 * Test the replaceAll(UnaryOperator<T> operator) method, it should:
	 * <ul>
	 * <li>apply the operator on all the elements in the list</li>
	 * <li>notify the listeners of a remove and an add action</li>
	 * </ul>
	 */
	@Test
	public void testReplaceAll() {
		list.addAll(threeElemList);
		Integer temp = new Integer(10);
		list.add(temp);
		assertFalse(dummyListener.hasBeenNotified());
		list.register(dummyListener);
		list.replaceAll(new AddNewOperator());

		assertBothActionsHappened();
		assertAddedListEqualsTo(Arrays.asList("elem1New", "elem2New", "elem3New"));
		assertRemovedListEqualsTo(Arrays.asList("elem1", "elem2", "elem3"));
		assertListEqualsTo(Arrays.asList("elem1New", "elem2New", "elem3New", temp));

	}

	/**
	 * Simple UnaryOperator that will append "New" to the object if it is a
	 * String
	 */
	class AddNewOperator implements UnaryOperator<Object> {
		@Override
		public Object apply(Object t) {
			if (t != null && t instanceof String) {
				return (String) t + "New";
			}
			return t;
		}
	}

	/**
	 * Test the replaceAll(UnaryOperator<T> operator) method when no element in
	 * the list is modified. The list should not be modified and no notification
	 * should be sent.
	 */
	@Test
	public void testReplaceAllNoMatch() {
		list.addAll(threeElemList);
		list.register(dummyListener);
		list.replaceAll(new UnaryOperator<Object>() {
			// the object is not modified by the operator
			@Override
			public Object apply(Object t) {
				return t;
			}
		});

		assertNothingHappened(threeElemList);
	}

	@Test
	public void testRetainAll() {
		list.addAll(fiveElemList);
		list.register(dummyListener);
		list.retainAll(threeElemList);

		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(Arrays.asList("elem4", "elem5"));
		assertListEqualsTo(threeElemList);
	}

	@Test
	public void testRetainAllNoRemove() {
		list.addAll(threeElemList);
		list.register(dummyListener);
		list.retainAll(fiveElemList);

		assertNothingHappened(threeElemList);
	}
	
	@Test
	public void testRetainAllRemove() {
		list.addAll(threeElemList);
		list.register(dummyListener);
		list.retainAll(emptyList);
		
		assertRemoveActionHappened();
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(threeElemList);
		assertListEqualsTo(emptyList);
	}
	
	@After
	public void afterTest() {
		list = null;
		dummyListener = null;
		dummyListener2 = null;
	}

	/***********************************
	 * Tool methods
	 ***********************************/

	/**
	 * Tool method that will check if the number of listeners for the
	 * ObservableList is equal to the one given in parameter.
	 * 
	 * @param numberListeners
	 *            number of listeners that should be listening to the list.
	 */
	private void assertListNumberListenersEquals(int numberListeners) {
		assertEquals(new Integer(numberListeners), new Integer(list.getNumberListeners()));
	}

	/**
	 * Tool method to compare a list to the list of added elements in the dummy
	 * listener.
	 * 
	 * @param listToCompare
	 *            list to compare to the added elements
	 */
	private void assertAddedListEqualsTo(List<Object> listToCompare) {
		assertTrue(dummyListener.getAddedElements().equals(listToCompare));
	}

	/**
	 * Tool method to compare a list to the list of removed elements in the
	 * dummy listener.
	 * 
	 * @param listToCompare
	 *            list to compare to the removed elements
	 */
	private void assertRemovedListEqualsTo(List<Object> listToCompare) {
		assertTrue(dummyListener.getRemovedElements().equals(listToCompare));
	}

	/**
	 * Tool method to compare a list to the main list.
	 * 
	 * @param listToCompare
	 *            list to compare to the main list
	 */
	private void assertListEqualsTo(List<Object> listToCompare) {
		assertTrue(list.equals(listToCompare));
	}

	/**
	 * Tool method to verify that the listener has been notified and that the
	 * action type was Add.
	 */
	private void assertAddActionHappened() {
		assertTrue(dummyListener.hasBeenNotified());
		assertTrue(dummyListener.isAddAction());
		assertFalse(dummyListener.isRemoveAction());
	}

	/**
	 * Tool method to verify that the listener has been notified and that the
	 * action type was Remove.
	 */
	private void assertRemoveActionHappened() {
		assertTrue(dummyListener.hasBeenNotified());
		assertFalse(dummyListener.isAddAction());
		assertTrue(dummyListener.isRemoveAction());
	}

	/**
	 * Tool method to verify that the listener has been notified and that it
	 * received both action types.
	 */
	private void assertBothActionsHappened() {
		assertTrue(dummyListener.hasBeenNotified());
		assertTrue(dummyListener.isAddAction());
		assertTrue(dummyListener.isRemoveAction());
	}

	/**
	 * Tool method to verify that no notification has been sent, basically that
	 * nothing happened. The list used in the test is compare to the one given
	 * in parameter to verify that they are the same.
	 *
	 * @param originalList
	 *            list to compare with the list used in the test
	 */
	private void assertNothingHappened(List<Object> originalList) {
		assertFalse(dummyListener.hasBeenNotified());
		assertFalse(dummyListener.isRemoveAction());
		assertFalse(dummyListener.isAddAction());
		assertAddedListEqualsTo(emptyList);
		assertRemovedListEqualsTo(emptyList);
		assertListEqualsTo(originalList);
	}
}
