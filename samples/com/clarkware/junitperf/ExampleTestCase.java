package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The <code>ExampleTestCase</code> is an example
 * stateless <code>TestCase</code>.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ExampleTestCase extends TestCase {

    public ExampleTestCase(String name) {
        super(name);
    }

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testOneSecondResponse() throws Exception {
		Thread.sleep(1000);
	}
	
	public static Test suite() {
		return new TestSuite(ExampleTestCase.class);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
