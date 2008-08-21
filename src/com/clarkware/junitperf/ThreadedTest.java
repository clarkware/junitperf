package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * The <code>ThreadedTest</code> is a test decorator that
 * runs a test in a separate thread.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ThreadedTest implements Test {

	private final Test test;
	private final ThreadGroup group;
	private final ThreadBarrier barrier;
	
	/**
	 * Constructs a <code>ThreadedTest</code> to decorate the
	 * specified test using the same thread group as the
	 * current thread.
	 *
	 * @param test Test to decorate.
	 */
	public ThreadedTest(Test test) {
		this(test, null, new ThreadBarrier(1));
	}

	/**
	 * Constructs a <code>ThreadedTest</code> to decorate the
	 * specified test using the specified thread group and
	 * thread barrier.
	 *
	 * @param test Test to decorate.
	 * @param group Thread group.
	 * @param barrier Thread barrier.
	 */
	public ThreadedTest(Test test, ThreadGroup group, ThreadBarrier barrier) {
		this.test = test;
		this.group = group;
		this.barrier = barrier;
	}

	/**
	 * Returns the number of test cases in this threaded test.
	 *
	 * @return Number of test cases.
	 */
	public int countTestCases() {
		return test.countTestCases();
	}

	/**
	 * Runs this test.
	 *
	 * @param result Test result.
	 */
	public void run(TestResult result) {
		Thread t = new Thread(group, new TestRunner(result));
		t.start();
	}
	
	
	class TestRunner implements Runnable  {
		
		private TestResult result;
		
		public TestRunner(TestResult result)  {
			this.result = result;
		}
		
		public void run() {
			test.run(result);
			barrier.onCompletion(Thread.currentThread());
		}
	}

	/**
	 * Returns the test description.
	 *
	 * @return Description.
	 */
	public String toString() {
		return "ThreadedTest: " + test.toString();
	}
}
