package com.clarkware.junitperf;

/**
 * The <code>ConstantTimer</code> is a <code>Timer</code>
 * with a constant delay.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.Timer
 */

public class ConstantTimer implements Timer {

	private final long delay;

	/**
	 * Constructs a <code>ConstantTimer</code> with the
	 * specified delay.
	 *
	 * @param delay Delay (in milliseconds).
	 */
	public ConstantTimer(long delay) {
		this.delay = delay;
	}

	/**
	 * Returns the timer delay.
	 *
	 * @return Delay (in milliseconds).
	 */
    public long getDelay() {
		return delay;
	}
}
