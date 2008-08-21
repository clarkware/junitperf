package com.clarkware.junitperf;

import junit.framework.Test;

/**
 * The <code>ExampleResponseTimeUnderLoadTest</code> demonstrates 
 * how to decorate a <code>TimedTest</code> as a <code>LoadTest</code> 
 * to measure response time under load.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.LoadTest
 * @see com.clarkware.junitperf.TimedTest
 */

public class ExampleResponseTimeUnderLoadTest {

    public static Test suite() {
     
        int maxUsers = 10;
        long maxElapsedTime = 1050;
        
        Test testCase = new ExampleTestCase("testOneSecondResponse");
        Test timedTest = new TimedTest(testCase, maxElapsedTime);
        Test loadTest = new LoadTest(timedTest, maxUsers);
 
        return loadTest;
    }
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
