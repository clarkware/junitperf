package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The <code>ExampleStatefulTestCase</code> is an example
 * stateful <code>TestCase</code>. 
 * <p>
 * If the <code>testState()</code> test is run without a
 * <code>TestMethodFactory</code>, then all threads of a
 * <code>LoadTest</code> will share the objects in the 
 * test fixture and may cause some tests to fail.
 * <p>
 * To ensure that each thread running the test method has a 
 * thread-local test fixture, use: 
 * <blockquote>
 * <pre>
 * Test factory = 
 *     new TestMethodFactory(ExampleStatefulTestCase.class, "testState");
 * LoadTest test = new LoadTest(factory, numberOfUsers, ...);
 * ...
 * </pre>
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ExampleStatefulTestCase extends TestCase {

	private boolean _flag; 
	private int _data; 

    public ExampleStatefulTestCase(String name) {
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

    /**
     * This test may fail in a <code>LoadTest</code> if run
     * without a <code>TestMethodFactory</code>.
     */
	public void testState() throws Exception {
		assertEquals(true, _flag);
		Thread.yield();
		assertEquals(1, _data);
	}

	public static Test suite() {
		return new TestSuite(ExampleStatefulTestCase.class);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
