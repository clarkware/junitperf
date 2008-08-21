package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The <code>AllTests</code> is a <code>TestCase</code> 
 * for all JUnitPerf tests.
 * 
 * @author <a href="mailto:mike@clarkware.com">Mike Clark</a>
 * @author <a href="http://www.clarkware.com">Clarkware Consulting, Inc.</a>
 */

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(LoadTestTest.suite());
		suite.addTest(TimedTestTest.suite());
		suite.addTest(TestFactoryTest.suite());
		return suite;
	}
		
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
