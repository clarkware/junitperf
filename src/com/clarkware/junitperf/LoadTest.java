package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.extensions.RepeatedTest;

/**
 * The <code>LoadTest</code> is a test decorator that runs
 * a test with a simulated number of concurrent users and
 * iterations.
 * <p>
 * In its simplest form, a <code>LoadTest</code> is constructed 
 * with a test to decorate and the number of concurrent users.
 * </p>
 * <p>
 * For example, to create a load test of 10 concurrent users
 * with each user running <code>ExampleTest</code> once and 
 * all users started simultaneously, use:
 * <blockquote>
 * <pre>
 * Test loadTest = new LoadTest(new TestSuite(ExampleTest.class), 10);
 * </pre>
 * </blockquote>
 * or, to load test a single test method, use: 
 * <blockquote>
 * <pre>
 * Test loadTest = new LoadTest(new ExampleTest("testSomething"), 10);
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * The load can be ramped by specifying a pluggable 
 * <code>Timer</code> instance which prescribes the delay
 * between the addition of each concurrent user.  A
 * <code>ConstantTimer</code> has a constant delay, with 
 * a zero value indicating that all users will be started 
 * simultaneously. A <code>RandomTimer</code> has a random 
 * delay with a uniformly distributed variation.
 * </p>
 * <p>
 * For example, to create a load test of 10 concurrent users
 * with each user running <code>ExampleTest.testSomething()</code> once and 
 * with a one second delay between the addition of users, use:
 * <blockquote>
 * <pre>
 * Timer timer = new ConstantTimer(1000);
 * Test loadTest = new LoadTest(new ExampleTest("testSomething"), 10, timer);
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * In order to simulate each concurrent user running a test for a 
 * specified number of iterations, a <code>LoadTest</code> can be 
 * constructed to decorate a <code>RepeatedTest</code>. 
 * Alternatively, a <code>LoadTest</code> convenience constructor 
 * specifying the number of iterations is provided which creates a 
 * <code>RepeatedTest</code>. 
 * </p>
 * <p>
 * For example, to create a load test of 10 concurrent users
 * with each user running <code>ExampleTest.testSomething()</code> for 20 iterations, 
 * and with a one second delay between the addition of users, use:
 * <blockquote>
 * <pre>
 * Timer timer = new ConstantTimer(1000);
 * Test repeatedTest = new RepeatedTest(new ExampleTest("testSomething"), 20);
 * Test loadTest = new LoadTest(repeatedTest, 10, timer);
 * </pre>
 * </blockquote>
 * or, alternatively, use: 
 * <blockquote>
 * <pre>
 * Timer timer = new ConstantTimer(1000);
 * Test loadTest = new LoadTest(new ExampleTest("testSomething"), 10, 20, timer);
 * </pre>
 * </blockquote> 
 * A <code>LoadTest</code> can be decorated as a <code>TimedTest</code>
 * to test the elapsed time of the load test.  For example, to decorate 
 * the load test constructed above as a timed test with a maximum elapsed 
 * time of 2 seconds, use:
 * <blockquote>
 * <pre>
 * Test timedTest = new TimedTest(loadTest, 2000);
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * By default, a <code>LoadTest</code> does not enforce test 
 * atomicity (as defined in transaction processing) if its decorated 
 * test spawns threads, either directly or indirectly.  In other words, 
 * if a decorated test spawns a thread and then returns control without 
 * waiting for its spawned thread to complete, then the test is assumed 
 * to be transactionally complete.  
 * </p>
 * <p>
 * If threads are integral to the successful completion of 
 * a decorated test, meaning that the decorated test should not be 
 * treated as complete until all of its threads complete, then  
 * <code>setEnforceTestAtomicity(true)</code> should be invoked to 
 * enforce test atomicity.  This effectively causes the load test to 
 * wait for the completion of all threads belonging to the same 
 * <code>ThreadGroup</code> as the thread running the decorated test.
 * </p>
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 * @author Ervin Varga
 */

public class LoadTest implements Test {

	private final int users;
	private final Timer timer;
	private final ThreadedTest test;
	private final ThreadedTestGroup group;
	private final ThreadBarrier barrier;
	private boolean enforceTestAtomicity;

	/**
	 * Constructs a <code>LoadTest</code> to decorate 
	 * the specified test using the specified number 
	 * of concurrent users starting simultaneously.
	 *
	 * @param test Test to decorate.
	 * @param users Number of concurrent users.
	 */
	public LoadTest(Test test, int users) {
		this(test, users, new ConstantTimer(0));
	}
	
	/**
	 * Constructs a <code>LoadTest</code> to decorate 
	 * the specified test using the specified number 
	 * of concurrent users starting simultaneously and
	 * the number of iterations per user.
	 *
	 * @param test Test to decorate.
	 * @param users Number of concurrent users.
	 * @param iterations Number of iterations per user.
	 */
	public LoadTest(Test test, int users, int iterations) {
		this(test, users, iterations, new ConstantTimer(0));
	}
		
	/**
	 * Constructs a <code>LoadTest</code> to decorate 
	 * the specified test using the specified number 
	 * of concurrent users, number of iterations per
	 * user, and delay timer.
	 *
	 * @param test Test to decorate.
	 * @param users Number of concurrent users.
	 * @param iterations Number of iterations per user.
	 * @param timer Delay timer.
	 */
	public LoadTest(Test test, int users, int iterations, Timer timer) {
		this(new RepeatedTest(test, iterations), users, timer);
	}
	
	/**
	 * Constructs a <code>LoadTest</code> to decorate 
	 * the specified test using the specified number 
	 * of concurrent users and delay timer.
	 *
	 * @param test Test to decorate.
	 * @param users Number of concurrent users.
	 * @param timer Delay timer.
	 */
	public LoadTest(Test test, int users, Timer timer) {
        
		 if (users < 1) {
            throw new IllegalArgumentException("Number of users must be > 0");
        } else if (timer == null) {
            throw new IllegalArgumentException("Delay timer is null");
        } else if (test == null) {
            throw new IllegalArgumentException("Decorated test is null");
        }
		 
		this.users = users;
		this.timer = timer;
		setEnforceTestAtomicity(false);
		this.barrier = new ThreadBarrier(users);
		this.group = new ThreadedTestGroup(this);
		this.test = new ThreadedTest(test, group, barrier);
	}
	
	/**
	 * Indicates whether test atomicity should be enforced.
	 * <p>
 	 * If threads are integral to the successful completion of 
 	 * a decorated test, meaning that the decorated test should not be 
 	 * treated as complete until all of its threads complete, then  
 	 * <code>setEnforceTestAtomicity(true)</code> should be invoked to 
 	 * enforce test atomicity.  This effectively causes the load test to 
 	 * wait for the completion of all threads belonging to the same 
 	 * <code>ThreadGroup</code> as the thread running the decorated test.
 	 * 
	 * @param isAtomic <code>true</code> to enforce test atomicity;
	 *                 <code>false</code> otherwise.
	 */
	public void setEnforceTestAtomicity(boolean isAtomic) {
		enforceTestAtomicity = isAtomic;
	}
	
	/**
	 * Returns the number of tests in this load test.
	 *
	 * @return Number of tests.
	 */
	public int countTestCases() {
		return test.countTestCases() * users;
	}

	/**
	 * Runs the test.
	 *
	 * @param result Test result.
	 */
	public void run(TestResult result) {
	
		group.setTestResult(result);

		for (int i=0; i < users; i++) {

			if (result.shouldStop()) {
				barrier.cancelThreads(users - i);
				break;
			}

			test.run(result);

            sleep(getDelay());
		}
		
		waitForTestCompletion();

		cleanup();
	}
	
	protected void waitForTestCompletion() {
		//
		// TODO: May require a strategy pattern
		//       if other algorithms emerge.
		// 
		if (enforceTestAtomicity) {
			waitForAllThreadsToComplete();
		} else {
			waitForThreadedTestThreadsToComplete();
		}
	}

	protected void waitForThreadedTestThreadsToComplete() {
		while (!barrier.isReached()) {
			sleep(50);
		}
	}
	
	protected void waitForAllThreadsToComplete() {
		while (group.activeCount() > 0) {
			sleep(50);
		}
	}
	
	protected void sleep(long time)  {
		try { 
			Thread.sleep(time); 
		} catch(Exception ignored) { } 
	}
	
	protected void cleanup() {
		try {
			group.destroy();
		} catch (Throwable ignored) { }
	}
	
	public String toString() {
		if (enforceTestAtomicity) {
			return "LoadTest (ATOMIC): " + test.toString();
		} else {
			return "LoadTest (NON-ATOMIC): " + test.toString();
		}
	}

	protected long getDelay() {
		return timer.getDelay();
	}
}
