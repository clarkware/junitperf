package com.clarkware.junitperf;

import junit.framework.*;

/**
 * The <code>TimedTestTest</code> is a <code>TestCase</code>
 * for the <code>TimedTest</code> class.
 *
 * @author <a href="mailto:mike@clarkware.com">Mike Clark</a>
 * @author <a href="http://www.clarkware.com">Clarkware Consulting, Inc.</a>
 * @author Ervin Varga
 *
 * @see junit.framework.TestCase
 */

public class TimedTestTest extends TestCase {

	private Test _oneSecondTest;	
	private Test _oneSecondFailedTest;
	private Timer _twoSecondDelayTimer;
	
	public static final long tolerance = 100;
	 
	public TimedTestTest(String name) {
		super(name);
		
		_oneSecondTest = 
			new MockTest("testOneSecondExecutionTime");
		
		_oneSecondFailedTest = 
			new MockTest("testOneSecondExecutionTimeWithFailure");
						
		_twoSecondDelayTimer = new ConstantTimer(2000);
	}
	
	public void testOneSecondResponseDefault() {
	
		Test test = new TimedTest(_oneSecondTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneSecondResponseNoWaitForCompletion() {
	
		Test test = new TimedTest(_oneSecondTest, 1000 + tolerance, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}

	public void testOneSecondResponseWaitForCompletion() {
	
		Test test = new TimedTest(_oneSecondTest, 1000 + tolerance, true);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneSecondResponseFailure() {
		
		Test test = new TimedTest(_oneSecondTest, 900);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneSecondResponseOneUserLoadSuccess() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 1);
		Test test = new TimedTest(loadTest, 1000 + tolerance);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneSecondResponseOneUserLoadFailure() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 1);
		Test test = new TimedTest(loadTest, 900);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneSecondResponseMultiUserLoadSuccess() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 2);
		Test test = new TimedTest(loadTest, 1500);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneSecondResponseMultiUserLoadFailure() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 2);
		Test test = new TimedTest(loadTest, 1000);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneSecondResponseMultiUserLoadTwoSecondDelaySuccess() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 2, _twoSecondDelayTimer);
		Test test = new TimedTest(loadTest, 4000 + tolerance);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneSecondResponseMultiUserLoadTwoSecondDelayFailure() {
		
		Test loadTest = new LoadTest(_oneSecondTest, 2, _twoSecondDelayTimer);
		Test test = new TimedTest(loadTest, 3700 + tolerance);
		
		assertEquals(2, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	/**
	 * This test will succeed properly only when the timed 
	 * test does not wait for the decorated test completion.
	 * Otherwise, if the timed test waits for the decorated 
	 * test completion, this test will hang indefinitely.
	 */	
	public void testInfiniteNoWaitForCompletion() {
	
		Test mockTest = new MockTest("testInfiniteExecutionTime");
		Test test = new TimedTest(mockTest, 1000 + tolerance, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}

	/**
	 * This test will succeed properly regardless of whether the 
	 * timed test waits for the decorated test completion or
	 * not.  However, when the timed test does not wait for the 
	 * decorated test completion, then it doesn't need to waste
	 * time waiting for the test to complete only then to 
	 * signal a failure.
	 */	
	public void testLongResponseNoWaitForCompletion() {
	
		Test mockTest = new MockTest("testLongExecutionTime");
		Test test = new TimedTest(mockTest, 2000 + tolerance, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	/**
	 * This test will cause the test to hang indefinitely. 
	 * The test will not properly fail after expiration 
	 * of the specified maximum elapsed time.
	 */	
	/*
	public void testInfiniteWaitForCompletion() {
	
		Test mockTest = new MockTest("testInfiniteExecutionTime");
		Test test = new TimedTest(mockTest, 1000 + tolerance, true);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	*/
	
	public void testOneSecondResponseSuccessWaiting() {
		
		Test test = new TimedTest(_oneSecondFailedTest, 1000 + tolerance, true);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneSecondResponseSuccessNonWaiting() {
			
		Test test = new TimedTest(_oneSecondFailedTest, 1000 + tolerance, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}

	/**
	 * When the timed test waits for completion, the effects of
	 * the decorated test are still traced and recorded.
	 */
	public void testOneSecondResponseFailureWaiting() {
		
		Test test = new TimedTest(_oneSecondFailedTest, 900, true);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(2, result.failureCount());
	}
	
	/**
	 * Failure(s) from a decorated test will not be detected 
	 * after the expiration of the max. elapsed time in a non-waiting
	 * timed test. This can cause possible ambiguities in the test, 
	 * especially when a decorated test has a varying execution time. 
	 *
	 * For example, if the decorated test would finish its execution in a 
	 * 800 ms then the failureCount() will return 1 because the decorated 
	 * test itself has failed.  However, if a timing would cause a failure 
	 * (the decorated test needed more then 900 ms to complete its execution) 
	 * the failureCount() will be again 1.  However, the root cause of the
	 * failure is ambiguous.
	 */
	public void testOneSecondResponseNonWaitingWithAmbiguousFailure() {
			
		Test test = new TimedTest(_oneSecondFailedTest, 900, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	/**
	 * Differentiates the cause of a non-waiting timed test's
	 * failure as a result of either the max elapsed time being
	 * exceeded or the test itself has failed.
	 */
	public void testOneSecondResponseNonWaitingWithTimeFailure() {
			
		TimedTest test = 
			new TimedTest(_oneSecondFailedTest, 900, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
		
		if (test.outOfTime()) {
			// success
		} else {
			fail("Max elapsed time exceeded!");
		} 
	}
	
	/**
	 * Differentiates the cause of a non-waiting timed test's
	 * failure as a result of either the max elapsed time being
	 * exceeded or the test itself has failed.
	 */
	public void testOneSecondResponseNonWaitingWithTestFailure() {	
	
		TimedTest test = 
			new TimedTest(_oneSecondFailedTest, 1100, false);
		
		assertEquals(1, test.countTestCases());
		
		TestResult result = new TestResult();
		test.run(result);
			
		if (test.outOfTime()) {
			fail("Should never get here!");
		} else {
			// 
			// Failed due to invalid test state.
			//
			assertEquals(1, result.runCount());
			assertEquals(0, result.errorCount());
			assertEquals(1, result.failureCount());
		}
	}
	
	public static Test suite() {
		return new TestSuite(TimedTestTest.class);
	}
	
	 public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
