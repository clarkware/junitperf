package com.clarkware.junitperf;

/**
 * The <code>Timer</code> interface defines the common interface 
 * implemented by all classes whose instances serve as pluggable timers.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public interface Timer {

	/**
	 * Returns the timer delay.
	 *
	 * @return Delay (in milliseconds).
	 */
    public long getDelay();
}
