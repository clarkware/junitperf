package com.clarkware.junitperf;

import java.io.*;
import java.lang.reflect.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The <code>TestMethodFactory</code> class is a <code>TestFactory</code>
 * that creates thread-local <code>TestSuite</code> instances containing
 * a specific test method of a <code>TestCase</code>.
 * <p>
 * A typical usage scenario is as follows:
 * <blockquote>
 * <pre>
 * Test factory = new TestMethodFactory(YourTestCase.class, "testSomething");
 * LoadTest test = new LoadTest(factory, numberOfUsers, ...);
 * ...
 * </pre>
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.TestFactory
 * @see com.clarkware.junitperf.LoadTest
 */

public class TestMethodFactory extends TestFactory {
    
	private final String testMethodName;

	/**
	 * Constructs a <code>TestMethodFactory</code> instance.
	 *
	 * @param testClass The <code>TestCase</code> class to load test.
	 * @param testMethodName The name of the test method to load test.
	 */
	public TestMethodFactory(Class testClass, String testMethodName) {
		super(testClass);
		this.testMethodName = testMethodName;
	}
	
	protected TestSuite makeTestSuite() {
		
		TestSuite suite = new TestSuite();
		
		Constructor constructor = null;
		
		try {
		
			constructor = getConstructor(testClass);
		
		} catch (NoSuchMethodException e) {
			suite.addTest(warning("Class " + testClass.getName() + 
				" has no public constructor TestCase(String name)"));
			return suite;
		}

		if (!Modifier.isPublic(testClass.getModifiers())) {
			suite.addTest(warning("Class " + testClass.getName() + 
				" is not public"));
			return suite;
		}
		
		addTestMethod(suite, constructor, testMethodName);

		if (suite.testCount() == 0) {
			suite.addTest(warning("No tests found in " + testClass.getName()));
		}
		
		return suite;
	}
	
	private void 
	addTestMethod(TestSuite suite, Constructor constructor, String methodName) {

		Object[] args = new Object[] { methodName };
		
		try {
			
			suite.addTest((Test)constructor.newInstance(args));
			
		} catch (InstantiationException ie) {
			suite.addTest(warning("Cannot instantiate test case: " + 
				methodName + " (" + toString(ie) + ")"));
		} catch (InvocationTargetException ite) {
			suite.addTest(warning("Exception in constructor: " + 
				methodName + " (" + toString(ite.getTargetException()) + ")"));
		} catch (IllegalAccessException iae) {
			suite.addTest(warning("Cannot access test case: " + 
				methodName + " (" + toString(iae) + ")"));
		}
	}
	
	private Constructor getConstructor(Class theClass) 
		throws NoSuchMethodException {
		
		Class[] args = { String.class };
		return theClass.getConstructor(args);
	}
	
	private Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};
	}
	
	private String toString(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();
	}
}
