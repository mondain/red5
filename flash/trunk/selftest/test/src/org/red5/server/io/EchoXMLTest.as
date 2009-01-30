package org.red5.server.io
{
  import asunit.framework.Assert;
  
  import net.theyard.components.netconnectionsm.NetConnectionStateMachine;
  import net.theyard.components.sm.events.StateChangeEvent;
  import net.theyard.components.test.YardTestCase;
  import net.theyard.components.Utils;
  import flash.net.ObjectEncoding;

  /**
   * Tests if we can transmit a array to red5 and get it back intact. 
   *
   * We test both AMF0 and AMF3 and expect the array to look the
   * same to us in both modes.
   */
  public class EchoXMLTest extends YardTestCase
  {
      // the net connection state machine which manages the connection
      // with the server

      private var nc:NetConnectionStateMachine;
      
      // a test array to be reflected off the server
      
      private const testXML:XML =
      <employees>
          <employee ssn="123-123-1234">
              <name first="John" last="Doe"/>
              <address>
                  <street>11 Main St.</street>
                  <city>San Francisco</city>
                  <state>CA</state>
                  <zip>98765</zip>
              </address>
          </employee>
          <employee ssn="789-789-7890">
              <name first="Mary" last="Roe"/>
              <address>
                  <street>99 Broad St.</street>
                  <city>Newton</city>
                  <state>MA</state>
                  <zip>01234</zip>
              </address>
          </employee>
      </employees>;
      
      /**
       * The URI the tests expect a running server on.
       * @see net.theyard.components.test.DefaultFixtures#RUNNING_SERVER_URI
       */
      
      public static const RUNNING_SERVER_URI:String = 
        net.theyard.components.test.DefaultFixtures.RUNNING_SERVER_URI;
      
      /**
       * Create the echo array call test.
       *
       * @param name the name of the test.
       */

      public function EchoXMLTest(name:String=null)
      {
        super(name);

        // create the net connection state machine

        nc = new NetConnectionStateMachine();
      }
      
      /**
       * @see net.theyard.components.test.YardTestCase#setUp()
       */

      protected override function setUp():void
      {
        super.setUp();
      }
      
      /**
       * @see net.theyard.components.test.YardTestCase#tearDown()
       */

      protected override function tearDown():void
      {
        super.tearDown();
      }
        
      /** Run the echo array test using AMF0.
       *
       * NOTE: This test is disabled because under AMF0 the result array
       * is returned as the first element of yet another array.  This
       * differs from AMF3 where the result array is returned directly.
       *
       * This test should be re-enabled once that bug is fixed
       */
 
      public function testEchoXMLAmf0():void
      {
        // set encoding 

        nc.objectEncoding = ObjectEncoding.AMF0;

        // run the test
        
        startEchoXMLTest();
      }

      /** Run the echo array test using AMF3. */

      public function testEchoXMLAmf3():void
      {
        // set encoding 

        nc.objectEncoding = ObjectEncoding.AMF3;

        // run the test

        startEchoXMLTest();
      }

      /** Test that a XML string can be sent to the server and echoed back. */
      
      public function startEchoXMLTest():void
      {

        // Make sure we have valid xml
        assertTrue("XML data is empty", testXML == null || testXML.length > 0);

        // indicate that this is an asynchronous test and all action
        // associated with this test should complete within the allotted
        // time (1000 milliseconds in this case).  farther down in the
        // test code finishAsyncTest() is called to signal successfull
        // test completion. if finishAsyncTest() is NOT called within
        // the allotted time the test will fail.

        startAsyncTest(1000);

        // add a listener to the net connection state machine to detects
        // when it has reached the connected state and then can continue
        // with this test

        nc.addEventListener(StateChangeEvent.STATE_CHANGE, onConnect_EchoXML);
        
        // connect to the server

        nc.connect(RUNNING_SERVER_URI);
      }

      /** Handle the connection event. This is called when the client
       * has completed conecting to the server.
       *
       * @param state the state of the connection to the server
       */

      public function onConnect_EchoXML(state:StateChangeEvent):void
      {
        // if the client is conneced to the server, continue the test
        
        if (nc.getState() == NetConnectionStateMachine.STATE_CONNECTED)
        {
          // remove the event listener

          nc.removeEventListener(
            StateChangeEvent.STATE_CHANGE, onConnect_EchoXML);

          // call the "echoXML" method on the echo service on the
          // server passing in the onConnect_EchoXML callback and the
          // test array
          
          nc.call(
            "echo.echoXML", onCallbackSuccess_EchoXML, null, testXML);
        }
      }
      
      /** Handle callback from the "echoXML" call on the server.
       *
       * @param result the return value from the call to "echoXML"
       */

      public function onCallbackSuccess_EchoXML(resultXML:XML):void
      {
        assertTrue(
          "original array length " + testXML.length + " != " + 
          "received array length " + resultXML.length, 
          testXML.length == resultXML.length);
        for (var i:String in testXML)
        {
          assertTrue(
            "original array[" + i + "] " + testXML[i] + " != " + 
            "received array[" + i + "] " + resultXML[i], 
            testXML[i] == resultXML[i]);
        }

        // close the connection to the server

        nc.close();

        // signal that we have completed the asynchronous test we
        // initiated with startAsyncTest() at the start of the test

        finishAsyncTest();
      }
  }
}
