package com.clarkware.junitperf;

import junit.framework.TestCase;

public class MockTestFactoryTest extends TestCase {
		
	public MockTestFactoryTest(String name) {
		super(name);
	}

	public void testSuccess() {
	}

	public void testFailure() {
		fail();
	}
}
