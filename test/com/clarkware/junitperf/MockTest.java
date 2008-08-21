package com.clarkware.junitperf;

import junit.framework.TestCase;

public class MockTest extends TestCase {
		
	public MockTest(String name) {
		super(name);
	}

	public void testSuccess() {
	}

	public void testFailure() {
		fail();
	}

	public void testError() {
		throw new RuntimeException();
	}
	
	public void testOneSecondExecutionTime() throws Exception {
		Thread.sleep(1000);
	}

	public void testOneSecondExecutionTimeWithFailure() throws Exception {
		Thread.sleep(1000);
		fail();
	}

	public void testInfiniteExecutionTime() {
		while (true) {
		}
	}

	public void testLongExecutionTime() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ignored) {}
	}

	public void testAtomic2SecondResponseWithWorkerThread() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {}
			}
		});

		t.start();

		try {
			Thread.sleep(1000);
			// don't wait for worker thread to finish
		} catch (InterruptedException ignored) {}
	}

	public void testNonAtomic2SecondResponseWithWorkerThread() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {}
			}
		});

		t.start();

		try {

			Thread.sleep(1000);
			// wait for worker thread to finish
			t.join();

		} catch (InterruptedException ignored) {}
	}

	public void testRogueThread() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (Exception ignored) {}
				}
			}
		});

		t.start();

		assertTrue(true);
	}
}
