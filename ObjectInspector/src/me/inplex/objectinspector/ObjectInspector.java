///////////////////////////////////////////////////////////
/*
 * ObjectInspector.java
 * 
 * Inpspects an object and records the changes of its fields
 * Example usage:
 *	Point p = new Point(1, 2);
 *	ObjectInspector oi = new ObjectInspector(new AtomicReference<Object>(p));
 *	oi.start();
 *	Thread.sleep(40);
 *	p.x = 1337;
 *	p.y = 3100;
 *	Thread.sleep(40);
 *	System.out.println(Arrays.toString(oi.getChanges().toArray()));
 *  oi.stop();
 */
///////////////////////////////////////////////////////////

package me.inplex.objectinspector;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectInspector {

	private AtomicReference<Object> reference;
	private HashMap<Integer, Object> values;
	private HashMap<Integer, Object> lastValues;
	private int interval;
	private boolean output;
	private Thread thread;
	private ArrayList<ObjectChange> changes;
	private boolean changed = false;
	private boolean running;

	/**
	 * Constructs a ObjectInspector with given reference
	 * 
	 * @param reference the reference to the inspected object
	 */

	public ObjectInspector(AtomicReference<Object> reference) {
		this(reference, 20, false);
	}

	/**
	 * Constructs a ObjectInspector with given reference and interval
	 * 
	 * @param reference the reference to the inspected object
	 * @param interval the update rate in milliseconds default is 20
	 */

	public ObjectInspector(AtomicReference<Object> reference, int interval) {
		this(reference, interval, false);
	}

	/**
	 * Constructs a ObjectInspector with given reference and output
	 * 
	 * @param reference the reference to the inspected object
	 * @param output if true, the ObjectInspector will give output when the
	 *            inspected Object changes
	 */

	public ObjectInspector(AtomicReference<Object> reference, boolean output) {
		this(reference, 20, output);
	}

	/**
	 * Constructs a ObjectInspector with given reference, interval and output
	 * 
	 * @param reference the reference to the inspected object
	 * @param interval the update rate in milliseconds default is 20
	 * @param output if true, the ObjectInspector will give output when the
	 *            inspected Object changes
	 */

	public ObjectInspector(AtomicReference<Object> reference, int interval, boolean output) {
		this.reference = reference;
		this.values = new HashMap<Integer, Object>();
		this.lastValues = new HashMap<Integer, Object>();
		this.interval = interval;
		this.changes = new ArrayList<ObjectChange>();
		this.output = output;
	}

	/**
	 * Checks if the Object has changed any of its fields
	 * 
	 * @return true, if the inspected object has changed since the last call
	 */

	public boolean hasChanged() {
		if (changed) {
			changed = false;
			return true;
		}
		return false;
	}

	/**
	 * Returns the last change of the object
	 * 
	 * @return the last ObjectChange of the inspected object null, if there was
	 *         no change
	 */

	public ObjectChange getLastChange() {
		if (changes.size() == 0)
			return null;
		return changes.get(changes.size() - 1);
	}

	/**
	 * Returns all changes of the object
	 * 
	 * @return an ArrayList containing all changes of the inspected object
	 *         sorted beginning with the oldest and ending with the newest
	 *         change
	 */

	public ArrayList<ObjectChange> getChanges() {
		return changes;
	}

	/**
	 * Starts the ObjectInspector
	 */

	public void start() {
		this.running = true;
		this.thread = new Thread() {
			@Override
			public void run() {
				try {
					Object object = reference.get();
					for (int i = 0; i < object.getClass().getFields().length; i++) {
						object.getClass().getFields()[i].setAccessible(true);
						values.put(i, object.getClass().getFields()[i].get(object));
					}
					for (int i = 0; i < object.getClass().getFields().length; i++) {
						object.getClass().getFields()[i].setAccessible(true);
						lastValues.put(i, object.getClass().getFields()[i].get(object));
					}
					while (running) {
						object = reference.get();
						for (int i = 0; i < object.getClass().getFields().length; i++) {
							object.getClass().getFields()[i].setAccessible(true);
							values.put(i, object.getClass().getFields()[i].get(object));
						}
						for (int key : values.keySet()) {
							Object value = values.get(key);
							if (!lastValues.get(key).equals(value)) {
								ObjectChange c = new ObjectChange(object.getClass().getFields()[key].getName(), lastValues.get(key), values.get(key));
								changes.add(c);
								changed = true;
								if (output) {
									System.out.println("Change: " + c.getName() + " from " + c.getFrom() + " to " + c.getTo());
								}
							}
						}
						for (int key : values.keySet()) {
							lastValues.put(key, values.get(key));
						}
						sleep(interval);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		this.thread.start();
	}

	/**
	 * Stops the ObjectInspector
	 * 
	 * @throws InterruptedException if the thread could not be joined
	 */

	public void stop() throws InterruptedException {
		this.running = false;
		this.thread.join();
	}

	public static void main(String[] args) throws Exception {
		Point p = new Point(1, 2);
		ObjectInspector oi = new ObjectInspector(new AtomicReference<Object>(p));
		oi.start();
		Thread.sleep(40);
		p.x = 1337;
		p.y = 3100;
		Thread.sleep(40);
		System.out.println(Arrays.toString(oi.getChanges().toArray()));
	}

}