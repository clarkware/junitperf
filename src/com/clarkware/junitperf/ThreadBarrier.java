package com.clarkware.junitperf;

/**
 * The <code>ThreadBarrier</code> class provides a callback
 * method for threads to signal their completion. 
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ThreadBarrier {

	public int returnedCount;
	public final int dispatchedCount;

	/**
	 * Constructs a <code>ThreadBarrier</code> with the
	 * specified number of threads to wait for.
	 *
	 * @param numDispatched Number of threads dispatched.
	 */
	public ThreadBarrier(int numDispatched) {
		returnedCount = 0;
		dispatchedCount = numDispatched;
	}

	/**
	 * Called when the specified thread is complete.
	 *
	 * @param t Completed thread.
	 */
	public synchronized void onCompletion(Thread t) {
		returnedCount++;
	}

	/**
	 * Determines whether the thread barrier has been reached -
	 * when all dispatched threads have returned.
	 *
	 * @return <code>true</code> if the barrier has been reached;
	 *         <code>false</code> otherwise.
	 */
	public boolean isReached() {
		return (returnedCount >= dispatchedCount);
	}
	
	/**
	 * Cancels the specified number of threads.
	 *
	 * @param threadCount Number of threads to cancel.
	 */
	public synchronized void cancelThreads(int threadCount) {
		returnedCount += threadCount;
	}
}
