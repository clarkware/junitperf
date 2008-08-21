package com.clarkware.junitperf;

import java.io.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import com.clarkware.junitperf.*;

/**
 * The <code>ExampleDataDrivenLoadTest</code> demonstrates one
 * technique for creating a load test where each thread uses
 * a distinct piece of test data.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 *
 * @see com.clarkware.junitperf.LoadTest
 */

public class ExampleDataDrivenLoadTest extends TestCase {

  private static final int CONCURRENT_USERS = 5;
  private static Iterator testDataIterator;

  public ExampleDataDrivenLoadTest(String name, ArrayList testData) {
    super(name);
    testDataIterator = testData.iterator();
  }

  public static Test suite() {

    ArrayList testData = new ArrayList();
    testData.add("A");  
    testData.add("B");  
    testData.add("C");  
    testData.add("D");  
    testData.add("E");  

    Test test = new ExampleDataDrivenLoadTest("testData", testData);
    LoadTest loadTest = new LoadTest(test, CONCURRENT_USERS);
    return loadTest;
  }
    
  private synchronized static String getNextTestData() {
    return (String)testDataIterator.next();
  }
    
  public void testData() {
    String data = getNextTestData();
    System.out.println(data);
  }
}
