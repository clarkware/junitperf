package com.clarkware.junitperf;

import junit.framework.TestCase;

public class MockTestWithState extends TestCase {
	

	private boolean _flag;
	private int _data;

	public MockTestWithState(String name) {
		super(name);
	}

	protected void setUp() {
		_flag = true;
		_data = 1;
	}

	protected void tearDown() {
		_flag = false;
		_data = 0;
	}

	public void testInvariant() {

		assertEquals(true, _flag);

		taskSwitch();

		assertEquals(1, _data);
	}
	
	protected void taskSwitch() {
		try {
			Thread.yield();
			Thread.sleep(10);
		} catch(Exception ignore) {}
	}
}