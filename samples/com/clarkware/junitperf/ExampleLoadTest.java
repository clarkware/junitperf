package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.RepeatedTest;

/**
 * The <code>ExampleLoadTest</code> demonstrates how to 
 * decorate a <code>Test</code> as a <code>LoadTest</code>.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.LoadTest
 * @see com.clarkware.junitperf.TimedTest
 */

public class ExampleLoadTest {

	public static final long toleranceInMillis = 100;

	public static Test suite() {
		TestSuite suite = new TestSuite();

		//
		// Pick any example tests below.
		//
		suite.addTest(makeStateful10UserLoadTest());
		suite.addTest(makeStateful10UserLoadTestMethod());
		suite.addTest(make1SecondResponse10UserLoad1SecondDelayIterationTest());
		//suite.addTest(make1SecondResponse1UserLoadTest());
		//suite.addTest(make1SecondResponse2UserLoadTest());
		//suite.addTest(make1SecondResponse1UserLoadIterationTest());
		//suite.addTest(make1SecondResponse1UserLoadRepeatedTest());
		//suite.addTest(make1SecondResponse2UserLoad2SecondDelayTest());

		return suite;
	}

	/**
	 * Decorates a stateful test as a 10 user load test,
	 * providing each user with a different test instance
	 * to ensure thread safety.
	 * 
	 * @return Test.
	 */ 
	protected static Test makeStateful10UserLoadTest() {

		int users = 10;
		int iterations = 1;

		Test factory = new TestFactory(ExampleStatefulTestCase.class);

		Test loadTest = new LoadTest(factory, users, iterations);

		return loadTest;
	}

	/**
	 * Decorates a stateful test method as a 10 user load test,
	 * providing each user with a different test instance
	 * to ensure thread safety.
	 * 
	 * @return Test.
	 */ 
	protected static Test makeStateful10UserLoadTestMethod() {

		int users = 10;
		int iterations = 1;

		Test factory = 
			new TestMethodFactory(ExampleStatefulTestCase.class, "testState");

		Test loadTest = new LoadTest(factory, users, iterations);

		return loadTest;
	}

	/**
	 * Decorates a one second response time test as a one
	 * user load test with a maximum elapsed time of 1 second
	 * and a 0 second delay between users.
	 * 
	 * @return Test.
	 */ 
	protected static Test make1SecondResponse1UserLoadTest() {

		int users = 1;
		long maxElapsedTimeInMillis = 1000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test loadTest = new LoadTest(testCase, users);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	/**
	 * Decorates a one second response time test as a two
	 * user load test with a maximum elapsed time of 1.5 
	 * seconds and a 0 second delay between users.
	 * 
	 * @return Test.
	 */ 
	protected static Test make1SecondResponse2UserLoadTest() {

		int users = 2;
		long maxElapsedTimeInMillis = 1500 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test loadTest = new LoadTest(testCase, users);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	/**
	 * Decorates a one second response time test as a one
	 * user load test with 10 iterations per user, a maximum 
	 * elapsed time of 12 seconds, and a 0 second delay
	 * between users.
	 * 
	 * @see testOneSecondResponseOneUserLoadRepeatedTest
	 * @return Test.
	 */ 
	protected static Test make1SecondResponse1UserLoadIterationTest() {

		int users = 1;
		int iterations = 10;
		long maxElapsedTimeInMillis = 10000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test loadTest = new LoadTest(testCase, users, iterations);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	/**
	 * Decorates a one second response time test as a one
	 * user load test with 10 iterations per user, a maximum 
	 * elapsed time of 12 seconds, and a 0 second delay
	 * between users.
	 * 
	 * @see testOneSecondResponseOneUserLoadIterationTest
	 * @return Test.
	 */ 
	protected static Test make1SecondResponse1UserLoadRepeatedTest() {

		int users = 1;
		int iterations = 10;
		long maxElapsedTimeInMillis = 10000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test repeatedTest = new RepeatedTest(testCase, iterations);
		Test loadTest = new LoadTest(repeatedTest, users);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	/**
	 * Decorates a one second response time test as a two
	 * user load test with a maximum elapsed time of 4 seconds
	 * and a 2 second delay between users.
	 * 
	 * @return Test.
	 */ 
	protected static Test make1SecondResponse2UserLoad2SecondDelayTest() {

		int users = 2;
		Timer timer = new ConstantTimer(2000);
		long maxElapsedTimeInMillis = 4000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test loadTest = new LoadTest(testCase, users, timer);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	/**
	 * Decorates a one second response time test as a 10
	 * user load test with 10 iterations per user, a maximum 
	 * elapsed time of 20 seconds, and a 1 second delay
	 * between users.
	 * 
	 * @return Test.
	 */ 
	protected static Test 
	make1SecondResponse10UserLoad1SecondDelayIterationTest() {

		int users = 10;
		int iterations = 10;
		Timer timer = new ConstantTimer(1000);
		long maxElapsedTimeInMillis = 20000 + toleranceInMillis;

		Test testCase = new ExampleTestCase("testOneSecondResponse");

		Test loadTest = new LoadTest(testCase, users, iterations, timer);
		Test timedTest = new TimedTest(loadTest, maxElapsedTimeInMillis);

		return timedTest;
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
