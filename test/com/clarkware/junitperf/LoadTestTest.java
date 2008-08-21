package com.clarkware.junitperf;

import junit.framework.*;
import junit.extensions.RepeatedTest;

/**
 * The <code>LoadTestTest</code> is a <code>TestCase</code>
 * for the <code>LoadTest</code> class.
 *
 * @author <a href="mailto:mike@clarkware.com">Mike Clark</a>
 * @author <a href="http://www.clarkware.com">Clarkware Consulting, Inc.</a>
 * @author Ervin Varga
 *
 * @see junit.framework.TestCase
 */

public class LoadTestTest extends TestCase {

	private TestSuite _successSuite;
	private TestSuite _rogueThreadSuite;
	private TestSuite _failureSuite;
	private TestSuite _errorSuite;
	
	public static final long tolerance = 100;
	
	public LoadTestTest(String name) {
		super(name);
		
		_successSuite = new TestSuite();
		_failureSuite = new TestSuite();
		_rogueThreadSuite = new TestSuite();
		_errorSuite = new TestSuite();
		
		_successSuite.addTest(new MockTest("testSuccess"));
		_successSuite.addTest(new MockTest("testSuccess"));
		_rogueThreadSuite.addTest(new MockTest("testRogueThread"));
		_failureSuite.addTest(new MockTest("testFailure"));
		_errorSuite.addTest(new MockTest("testError"));
	}
	
	public void testOneUser() {
	
		Test test = new LoadTest(_successSuite, 1);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	/**
	 * This test will succeed properly only when the load 
	 * test does not enforce thread atomicity.
	 * Otherwise, if the load test enforces thread atomicity, 
	 * this test will hang indefinitely.
	 */	
	public void testOneUserRogueThread() {
		
		LoadTest test = new LoadTest(_rogueThreadSuite, 1);
		
		//test.setEnforceThreadAtomicity(true);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUser() {
		
		Test test = new LoadTest(_successSuite, 3);
		
		assertEquals(6, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(6, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUserWithIterations() {
		
		Test test = new LoadTest(_successSuite, 3, 10);
		
		assertEquals(60, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(60, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUserWithRepeatedTest() {
		
		RepeatedTest repeat = new RepeatedTest(_successSuite, 10);
		Test test = new LoadTest(repeat, 3);
		
		assertEquals(60, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(60, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUserWithDelay() {
		
		Test test = new LoadTest(_successSuite, 3, new ConstantTimer(0));
		
		assertEquals(6, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(6, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUserWithFailure() {
		
		Test test = new LoadTest(_failureSuite, 3);
		
		assertEquals(3, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(3, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(3, result.failureCount());
	}
	
	public void testMultiUserWithError() {
		
		Test test = new LoadTest(_errorSuite, 3);
		
		assertEquals(3, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(3, result.runCount());
		assertEquals(3, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testMultiUserWithStop() {

		Test test = new LoadTest(_failureSuite, 2);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		result.stop();
		test.run(result);
		
		assertEquals(0, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testNonPositiveUser() {
	
		try {
		
			Test test1 = new LoadTest(_successSuite, 0);
			fail("Should throw an IllegalArgumentException");
			
			Test test2 = new LoadTest(_successSuite, -1);
			fail("Should throw an IllegalArgumentException");
			
		} catch (IllegalArgumentException success) {
			return;
		}
	}
	
	public void testNullTimer() {
		try {
		
			Test test = new LoadTest(_successSuite, 1, null);
			fail("Should throw an IllegalArgumentException");
			
		} catch (IllegalArgumentException success) {
			return;
		}
	}
	
	public void testAtomic2SecondResponse() {
	
		Test mockTest = new MockTest("testAtomic2SecondResponseWithWorkerThread");
		Test loadTest = new LoadTest(mockTest, 1);
		Test test = new TimedTest(loadTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testAtomic2SecondResponseEnforceTestAtomicity() {
	
		Test mockTest = new MockTest("testAtomic2SecondResponseWithWorkerThread");
		LoadTest loadTest = new LoadTest(mockTest, 1);
		loadTest.setEnforceTestAtomicity(true);
		Test test = new TimedTest(loadTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
    
	public void testNonAtomic2SecondResponse() {
	
		Test mockTest = new MockTest("testNonAtomic2SecondResponseWithWorkerThread");
		LoadTest loadTest = new LoadTest(mockTest, 1);
		Test test = new TimedTest(loadTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());

		TestResult result = new TestResult();
		test.run(result);

		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testNonAtomic2SecondResponseEnforceTestAtomicity() {
	
		Test mockTest = new MockTest("testNonAtomic2SecondResponseWithWorkerThread");
		LoadTest loadTest = new LoadTest(mockTest, 1);
		loadTest.setEnforceTestAtomicity(true);
		Test test = new TimedTest(loadTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());

		TestResult result = new TestResult();
		test.run(result);

		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
   public void testTestStateConsistencyFailure() {
   	
		Test mockTest = new MockTestWithState("testInvariant");
		LoadTest test = new LoadTest(mockTest, 10, 2);
		
		test.setEnforceTestAtomicity(true);
		
		assertEquals(20, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(20, result.runCount());
		assertEquals(0, result.errorCount());
		assertTrue(result.failureCount() > 0);
    }
	 
	public void testTestStateConsistencyWithTestFactory() {
		
	 	Test testFactory = new TestFactory(MockTestWithState.class);
		LoadTest test = new LoadTest(testFactory, 10, 2);
		
		test.setEnforceTestAtomicity(true);
		
		assertEquals(20, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(20, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	 
	
	public static Test suite() {
		return new TestSuite(LoadTestTest.class);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
