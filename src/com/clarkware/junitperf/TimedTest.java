package com.clarkware.junitperf;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.extensions.TestDecorator;

/**
 * The <code>TimedTest</code> is a test decorator that 
 * runs a test and measures the elapsed time of the test.
 * <p>
 * A <code>TimedTest</code> is constructed with a specified
 * maximum elapsed time.  By default, a <code>TimedTest</code> 
 * will wait for the completion of its decorated test and
 * then fail if the maximum elapsed time was exceeded.
 * Alternatively, a <code>TimedTest</code> can be constructed 
 * to immediately signal a failure when the maximum elapsed time 
 * of its decorated test is exceeded.  In other words, the 
 * <code>TimedTest</code> will <b>not</b> wait for its decorated 
 * test to run to completion if the maximum elapsed time is 
 * exceeded.
 * </p>
 * <p>
 * For example, to decorate the <code>ExampleTest</code> 
 * as a <code>TimedTest</code> that waits for the 
 * <code>ExampleTest</code> test case to run 
 * to completion and then fails if the maximum elapsed 
 * time of 2 seconds is exceeded, use: 
 * <blockquote>
 * <pre>
 * Test timedTest = new TimedTest(new TestSuite(ExampleTest.class), 2000);
 * </pre>
 * </blockquote> 
 * or, to time a single test method, use: 
 * <blockquote>
 * <pre>
 * Test timedTest = new TimedTest(new ExampleTest("testSomething"), 2000);
 * </pre>
 * </blockquote> 
 * <p>
 * Alternatively, to decorate the <code>ExampleTest.testSomething()</code>
 * test as a <code>TimedTest</code> that fails immediately when
 * the maximum elapsed time of 2 seconds is exceeded, use:
 * <blockquote>
 * <pre>
 * Test timedTest = new TimedTest(new ExampleTest("testSomething"), 2000, false);
 * </pre>
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 * @author Ervin Varga
 */

public class TimedTest extends TestDecorator {

	private final long maxElapsedTime;
	private final boolean waitForCompletion;
	private boolean maxElapsedTimeExceeded;
	private boolean isQuiet;

	/**
	 * Constructs a <code>TimedTest</code> to decorate the 
	 * specified test with the specified maximum elapsed time.
	 * <p>
	 * The <code>TimedTest</code> will wait for the completion 
	 * of its decorated test and then fail if the maximum elapsed 
	 * time was exceeded.
	 * 
	 * @param test Test to decorate.
	 * @param maxElapsedTime Maximum elapsed time (ms).
	 */
	public TimedTest(Test test, long maxElapsedTime) {
		this(test, maxElapsedTime, true);
	}
	
	/**
	 * Constructs a <code>TimedTest</code> to decorate the 
	 * specified test with the specified maximum elapsed time.
	 * 
	 * @param test Test to decorate.
	 * @param maxElapsedTime Maximum elapsed time (ms).
	 * @param waitForCompletion <code>true</code> (default) to 
	 *        indicate that the <code>TimedTest</code> should wait 
	 *        for its decorated test to run to completion and then 
	 *        fail if the maximum elapsed time was exceeded; 
	 *        <code>false</code> to indicate that the 
	 *        <code>TimedTest</code> should immediately signal 
	 *        a failure when the maximum elapsed time is exceeded.
	 */
	public TimedTest(Test test, long maxElapsedTime, boolean waitForCompletion) {
		super(test);
		this.maxElapsedTime = maxElapsedTime;
		this.waitForCompletion = waitForCompletion;
		maxElapsedTimeExceeded = false;
		isQuiet = false;
	}
	
	/**
	 * Disables the output of the test's elapsed time.
	 */
	public void setQuiet() {
		isQuiet = true;
	}

	/**
	 * Returns the number of tests in this timed test.
	 *
	 * @return Number of tests.
	 */
	public int countTestCases() {
		return super.countTestCases();
	}
	
	/**
	 * Determines whether the maximum elapsed time of
	 * the test was exceeded.
	 *
	 * @return <code>true</code> if the max elapsed time
	 *         was exceeded; <code>false</code> otherwise.
	 */
	public boolean outOfTime() {
		return maxElapsedTimeExceeded;		
	}

	/**
	 * Runs the test.
	 *
	 * @param result Test result.
	 */
	public void run(TestResult result) {
		//
		// TODO: May require a strategy pattern
		//       if other algorithms emerge.
		// 
		if (waitForCompletion) {
			runUntilTestCompletion(result);
		} else {
			runUntilTimeExpires(result);
		}
	}
	
	/**
	 * Runs the test until test completion and then signals
	 * a failure if the maximum elapsed time was exceeded.
	 *
	 * @param result Test result.
	 */
	protected void runUntilTestCompletion(TestResult result) {

		long beginTime = System.currentTimeMillis();

		super.run(result);

		long elapsedTime = getElapsedTime(beginTime);
		printElapsedTime(elapsedTime);

		if (elapsedTime > maxElapsedTime) {	
			maxElapsedTimeExceeded = true;			
			result.addFailure(getTest(),
				new AssertionFailedError("Maximum elapsed time exceeded!" +
					" Expected " + maxElapsedTime + "ms, but was " +
					elapsedTime + "ms."));
            result.endTest(getTest());
		}
	}
	
	/**
	 * Runs the test and immediately signals a failure 
	 * when the maximum elapsed time is exceeded.
	 *
	 * @param result Test result.
	 */
	protected void runUntilTimeExpires(final TestResult result) {
	
		Thread t = new Thread(new Runnable() {
			public void run() {
				TimedTest.super.run(result);
				
				// IBM's JVM prefers this instead:
				// run(result);
			}
		});

		long beginTime = System.currentTimeMillis();
		
		t.start();

		try {
		
			t.join(maxElapsedTime);
		
		} catch(InterruptedException ignored) {}

		printElapsedTime(getElapsedTime(beginTime));
	
		if (t.isAlive()) {				
			maxElapsedTimeExceeded = true;
			result.addFailure(getTest(),
				new AssertionFailedError("Maximum elapsed time (" + maxElapsedTime + " ms) exceeded!"));
			result.endTest(getTest());
        }	
	}

	protected long getElapsedTime(long beginTime) {
		long endTime = System.currentTimeMillis();
		return endTime - beginTime;
	}
	
	protected void printElapsedTime(long elapsedTime) {
		if (!isQuiet) {
			System.out.println(toString() + ": " + elapsedTime + " ms");
			System.out.flush();
		}
	}
		
	/**
	 * Returns the test description.
	 *
	 * @return Description.
	 */
	public String toString() {
		if (waitForCompletion) {
			return "TimedTest (WAITING): " + super.toString();
		} else {
			return "TimedTest (NON-WAITING): " + super.toString();
		}
	}
}
