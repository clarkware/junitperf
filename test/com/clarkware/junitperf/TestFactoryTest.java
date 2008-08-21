package com.clarkware.junitperf;

import junit.framework.*;

/**
 * The <code>TestFactoryTest</code> is a <code>TestCase</code>
 * for the <code>TestFactory</code> and <code>TestMethodFactory</code>
 * classes.
 *
 * @author <a href="mailto:mike@clarkware.com">Mike Clark</a>
 * @author <a href="http://www.clarkware.com">Clarkware Consulting, Inc.</a>
 *
 * @see junit.framework.TestCase
 */

public class TestFactoryTest extends TestCase {

	private TestSuite _allMethodsTestSuite;
	private TestSuite _oneMethodTestSuite;
		
	private Class _testClass;
	
	public TestFactoryTest(String name) {
		super(name);
		
		_testClass = com.clarkware.junitperf.MockTestFactoryTest.class;
		
		_allMethodsTestSuite = new TestSuite(_testClass);
		
		_oneMethodTestSuite = new TestSuite();
		_oneMethodTestSuite.addTest(new MockTestFactoryTest("testSuccess"));
	}
	
	public void testAllTestMethods() {
		
		TestFactory testFactory = new TestFactory(_testClass);
		
		assertEquals(2, testFactory.countTestCases());
		
		assertEqualsTestSuite(_allMethodsTestSuite, testFactory.getTest());
		
		TestResult result = new TestResult();
		testFactory.run(result);
		
		assertEquals(2, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testAllTestMethodsSameSuiteForSameThread() {
		
		TestFactory testFactory = new TestFactory(_testClass);
		
		Test test1 = testFactory.getTest();
		Test test2 = testFactory.getTest();
		
		assertSame(test1, test2);
		
		assertEqualsTestSuite(_allMethodsTestSuite, test1);
		assertEqualsTestSuite(_allMethodsTestSuite, test2);		
	}
	
	public void testAllTestMethodsDifferentSuiteForDifferentThread() {
		
		TestFactory testFactory = new TestFactory(_testClass);

		MockRunnable runner1 = new MockRunnable(testFactory);
		MockRunnable runner2 = new MockRunnable(testFactory);
		
		Thread thread1 = new Thread(runner1);
		Thread thread2 = new Thread(runner2);

		thread1.start();
		thread2.start();
		
		sleep();
		
		Test test1 = runner1.getTest();
		Test test2 = runner2.getTest();
		
		assertTrue(!(test1 == test2));

		assertEqualsTestSuite(_allMethodsTestSuite, test1);
		assertEqualsTestSuite(_allMethodsTestSuite, test2);
	}

	public void testOneTestMethodSuccess() {
		
		TestMethodFactory testFactory = 
			new TestMethodFactory(_testClass, "testSuccess");
		
		assertEquals(1, testFactory.countTestCases());
		
		assertEqualsTestSuite(_oneMethodTestSuite, testFactory.getTest());
		
		TestResult result = new TestResult();
		testFactory.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(0, result.failureCount());
	}
	
	public void testOneTestMethodFailure() {
		
		TestMethodFactory testFactory = 
			new TestMethodFactory(_testClass, "testFailure");
		
		assertEquals(1, testFactory.countTestCases());
		
		assertEqualsTestSuite(_oneMethodTestSuite, testFactory.getTest());
		
		TestResult result = new TestResult();
		testFactory.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneTestMethodNoSuchMethod() {
		
		TestMethodFactory testFactory = 
			new TestMethodFactory(_testClass, "testFoo");
		
		TestResult result = new TestResult();
		testFactory.run(result);
		
		assertEquals(1, result.runCount());
		assertEquals(0, result.errorCount());
		assertEquals(1, result.failureCount());
	}
	
	public void testOneTestMethodSameSuiteForSameThread() {
		
		TestFactory testFactory = 
			new TestMethodFactory(_testClass, "testSuccess");
		
		Test test1 = testFactory.getTest();
		Test test2 = testFactory.getTest();
		
		assertSame(test1, test2);
		
		assertEqualsTestSuite(_oneMethodTestSuite, test1);
		assertEqualsTestSuite(_oneMethodTestSuite, test2);		
	}
		
	public void testOneTestMethodDifferentSuiteForDifferentThread() {
		
		TestFactory testFactory = 
			new TestMethodFactory(_testClass, "testSuccess");

		MockRunnable runner1 = new MockRunnable(testFactory);
		MockRunnable runner2 = new MockRunnable(testFactory);
		
		Thread thread1 = new Thread(runner1);
		Thread thread2 = new Thread(runner2);

		thread1.start();
		thread2.start();
		
		sleep();
		
		Test test1 = runner1.getTest();
		Test test2 = runner2.getTest();
		
		assertTrue(!(test1 == test2));

		assertEqualsTestSuite(_oneMethodTestSuite, test1);
		assertEqualsTestSuite(_oneMethodTestSuite, test2);
	}
	
	
	public void testClassNotATestCase() {
		
		try {
			
			TestFactory testFactory = new TestFactory(java.lang.String.class);
			fail("Class not assignable to TestCase!");
			
			testFactory = new TestMethodFactory(java.lang.String.class, "");
			fail("Class not assignable to TestCase!");
				
		} catch(IllegalArgumentException success) {
		}
	}
	
	protected void sleep() {
		try {
			Thread.sleep(250);
		} catch (InterruptedException ignored) {
		}
	}
	
	protected void assertEqualsTestSuite(Test t1, Test t2) {
		
		assertTrue(t1 instanceof TestSuite);
		assertTrue(t2 instanceof TestSuite);
		
		TestSuite suite1 = (TestSuite)t1;
		TestSuite suite2 = (TestSuite)t2;	
		
		assertEquals(suite1.countTestCases(), suite2.countTestCases());
		assertEquals(suite1.getName(), suite2.getName()); 
	}
	
	private static class MockRunnable implements Runnable {
		
		private TestFactory _factory;
		private Test _test;
		
		public MockRunnable(TestFactory factory) {
			_factory = factory;
		}
		
		public void run() {
			_test = _factory.getTest();
		}
		
		public Test getTest() {
			return _test;
		}
	}
	
	public static Test suite() {
		return new TestSuite(TestFactoryTest.class);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}
}
