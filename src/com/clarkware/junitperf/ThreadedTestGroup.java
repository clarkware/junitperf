package com.clarkware.junitperf;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 * The <code>ThreadedTestGroup</code> is a <code>ThreadGroup</code>
 * that catches and handles exceptions thrown by threads created 
 * and started by <code>ThreadedTest</code> instances.
 * <p>
 * If a thread managed by a <code>ThreadedTestGroup</code> throws 
 * an uncaught exception, then the exception is added to the current 
 * test's results and all other threads are immediately interrupted.
 * </p>
 * 
 * @author Ervin Varga
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ThreadedTestGroup extends ThreadGroup {

	private final Test test;
	private TestResult testResult;
	
	/**
	 * Constructs a <code>ThreadedTestGroup</code> for the
	 * specified test.
	 *
	 * @param test Current test.
	 */
	public ThreadedTestGroup(Test test) {
		super("ThreadedTestGroup");
		this.test = test;
	}
	
	/**
	 * Sets the current test result.
	 *
	 * @param result Test result.
	 */
	public void setTestResult(TestResult result) {
		testResult = result;
	}

	/**
	 * Called when a thread in this thread group stops because of
	 * an uncaught exception.
	 * <p>
	 * If the uncaught exception is a <code>ThreadDeath</code>,
	 * then it is ignored.  If the uncaught exception is an
	 * <code>AssertionFailedError</code>, then a failure
	 * is added to the current test's result.  Otherwise, an
	 * error is added to the current test's result.
	 *
	 * @param t Originating thread.
	 * @param e Uncaught exception.
	 */
	public void uncaughtException(Thread t, Throwable e) {
		
		if (e instanceof ThreadDeath) {
			return;
		}
		
		if (e instanceof AssertionFailedError) {
			testResult.addFailure(test, (AssertionFailedError)e);
		} else {
			testResult.addError(test, e);
		}
		
		super.interrupt();
	}
}