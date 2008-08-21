package com.clarkware.junitperf;

import java.util.Random;

/**
 * The <code>RandomTimer</code> is a <code>Timer</code>
 * with a random delay and a uniformly distributed variation.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 * 
 * @see com.clarkware.junitperf.Timer
 */

public class RandomTimer implements Timer {

    private final Random random;
    private final long delay;
    private final double variation;

	/**
	 * Constructs a <code>RandomTimer</code> with the
	 * specified minimum delay and variation.
	 *
	 * @param delay Minimum delay (ms).
	 * @param variation Variation (ms).
	 */
    public RandomTimer(long delay, double variation) {
		this.delay = delay;	
		this.variation = variation;
        this.random = new Random();
    }

	/**
	 * Returns the timer delay.
	 *
	 * @return Delay (ms).
	 */
    public long getDelay() {
        return (long) Math.abs((random.nextDouble() * variation) + delay);
    }
}
