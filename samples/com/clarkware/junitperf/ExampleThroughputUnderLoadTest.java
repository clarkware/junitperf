package com.clarkware.junitperf;

import junit.framework.Test;

/**
 * The <code>ExampleThroughputUnderLoadTest</code> demonstrates how to 
 * decorate a <code>LoadTest</code> as a <code>TimedTest</code> 
 * to measure throughput under load.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.LoadTest
 * @see com.clarkware.junitperf.TimedTest
 */

public class ExampleThroughputUnderLoadTest {

    public static Test suite() {
     
        int maxUsers = 10;
        long maxElapsedTime = 1500;
        
        Test testCase = new ExampleTestCase("testOneSecondResponse");
        Test loadTest = new LoadTest(testCase, maxUsers);
        Test timedTest = new TimedTest(loadTest, maxElapsedTime);

        return timedTest;
    }
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
