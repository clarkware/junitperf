package com.clarkware.junitperf;

import junit.framework.Test;

/**
 * The <code>ExampleTimedTest</code> demonstrates how to 
 * decorate a <code>Test</code> as a <code>TimedTest</code>.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.TimedTest
 */

public class ExampleTimedTest {

	public static final long toleranceInMillis = 100;

	public static Test suite() {
		
		long maxElapsedTimeInMillis = 1000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");
		Test timedTest = new TimedTest(testCase, maxElapsedTimeInMillis);

		return timedTest;
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
