package com.clarkware.junitperf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * The <code>TestFactory</code> class creates thread-local 
 * <code>TestSuite</code> instances.
 * <p>
 * This factory class should be used in cases when a stateful test
 * is intended to be decorated by a <code>LoadTest</code>.  A stateful
 * test is defined as any test that defines test-specific state in
 * its <code>setUp()</code> method.
 * </p>
 * <p>
 * Use of the <code>TestFactory</code> ensures that each thread spawned
 * by a <code>LoadTest</code> contains its own <code>TestSuite</code>
 * instance containing all tests defined in the specified 
 * <code>TestCase</code> class.
 * </p>
 * <p>
 * A typical usage scenario is as follows:
 * <blockquote>
 * <pre>
 * Test factory = new TestFactory(YourTestCase.class);
 * LoadTest test = new LoadTest(factory, numberOfUsers, ...);
 * ...
 * </pre>
 * </blockquote>
 * </p>
 * <p>
 * Of course, static variables cannot be protected externally, so tests
 * intended to be run in a multi-threaded environment should ensure
 * that the use of static variables is thread-safe.
 * </p>
 * <p>
 * This class is dependent on Java 2.  For earlier platforms a 
 * local cache implementation should be changed to use, for example, 
 * a HashMap to track thread-local information.
 * </p>
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 * @author Ervin Varga
 *
 * @see com.clarkware.junitperf.LoadTest
 */

public class TestFactory implements Test {
    
	protected final Class testClass;
	private TestSuite suite; 
	private final TestCache testCache;
    
	/**
	 * Constructs a <code>TestFactory</code> instance.
	 *
	 * @param testClass The <code>TestCase</code> class to load test.
	 */
	public TestFactory(Class testClass) {

		if (!(TestCase.class.isAssignableFrom(testClass))) {
			throw new IllegalArgumentException("TestFactory must " +
				"be constructed with a TestCase class.");
		}

		this.testClass = testClass;
		this.testCache = new TestCache();
	}

	/**
	 * Runs an instance of the <code>Test</code> class and 
	 * collects its result in the specified <code>TestResult</code>. 
	 * <p>
	 * Each invocation of this method triggers the creation of a 
	 * new <code>Test</code> class instance as specified in the
	 * construction of this <code>TestFactory</code>.
	 *
	 * @param result Test result.
	 */
	public void run(TestResult result) {
		getTest().run(result);        
	}
    
	/**
	 * Returns the number of tests in this test.
	 *
	 * @return Number of tests.
	 */
	public int countTestCases() {
		return getTestSuite().countTestCases();
	}

	/**
	 * Returns the test description.
	 *
	 * @return Description.
	 */
	public String toString() {
		return "TestFactory: " + getTestSuite().toString();
	}
	
	protected Test getTest() {
		return testCache.getTest();
	}
	
	protected TestSuite getTestSuite() {
		if (suite == null) {
			suite = makeTestSuite();
		}
		
		return suite;
	}
	
	protected TestSuite makeTestSuite() {
		return new TestSuite(testClass);
	}

	/*
	 * The <code>TestCache</code> class provides thread-local
	 * instances of a <code>TestSuite</code> class containing
	 * tests defined in the <code>TestCase</code> class
	 * specified in the <code>TestFactory</code>.
     */ 
	private final class TestCache {

		private final ThreadLocal _localCache = new ThreadLocal() {

			protected Object initialValue() {
				return makeTestSuite();
			}
		};
        
		/*
		 * Returns the <code>Test</code> instance local to the 
		 * calling thread.
		 *
		 * @return Thread-local <code>Test</code> instance.
		 */
		Test getTest() {
			return (Test)_localCache.get();
		}
	}
}
