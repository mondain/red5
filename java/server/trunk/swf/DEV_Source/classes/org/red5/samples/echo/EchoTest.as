﻿package org.red5.samples.echo
{
	/**
	 * RED5 Open Source Flash Server - http://www.osflash.org/red5
	 *
	 * Copyright (c) 2006-2007 by respective authors (see below). All rights reserved.
	 *
	 * This library is free software; you can redistribute it and/or modify it under the
	 * terms of the GNU Lesser General Public License as published by the Free Software
	 * Foundation; either version 2.1 of the License, or (at your option) any later
	 * version.
	 *
	 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
	 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
	 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
	 *
	 * You should have received a copy of the GNU Lesser General Public License along
	 * with this library; if not, write to the Free Software Foundation, Inc.,
	 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
	*/
	
	import flash.events.*;
	import flash.net.*;
	import flash.system.Capabilities;
	import flash.utils.getTimer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.*;
	import mx.core.Application;
	import mx.rpc.remoting.mxml.RemoteObject;
	import mx.utils.ObjectProxy;
	
	import org.red5.samples.echo.EchoClass;
	import org.red5.samples.echo.RemoteClass;
	import org.red5.samples.echo.ExternalizableClass;
	
	/**
	 * 
	 * @author Joachim Bauch
	 * @author Thijs Triemstra
	*/	
	public class EchoTest extends Application
	{
		[Bindable]
		private var nc: NetConnection;
		
		[Bindable]
		private var testParams: Array;
		
		[Bindable]
		private var testIndex: Number;
		
		[Bindable]
		private var AMF0Count: Number;
		
		[Bindable]
		private var testsFailed: Number;
		
		[Bindable]
		public var rtmp_txt : TextInput;
		
		[Bindable]
		public var http_txt : TextInput;
		
		[Bindable]
		public var responder : Responder;
		
		[Bindable]
		public var echoService : RemoteObject;
		
		[Bindable]
		public var username_txt : TextInput;
		
		[Bindable]
		public var password_txt : TextInput;
		
		[Bindable]
		public var fpVersion : String;
		
		[Bindable]
		public var testResult : String;
		
		private var success : String = "<font color='#149D25'>";
		private var failure : String = "<font color='#FF1300'>";
		private var timer : int;
		
		/**
	 	 * Create and send test data.
		 */	
        public function EchoTest(): void
        {
        	// Display FP version nr.
        	fpVersion = "FP " + Capabilities.version;
        	
			// stores the test data
			testParams = new Array();
			testParams.push(null);
			// test Booleans
			testParams.push(true);
			testParams.push(false);
			testParams.push("");
			testParams.push("Hello world!");
			// long Strings
			var i: Number;
			var longString: String = "";
			for (i=0; i<4000; i++)
				longString = longString + "0123456789";
			testParams.push(longString);
			var reallyLongString: String = "";
			for (i=0; i<7000; i++)
				reallyLongString = reallyLongString + "0123456789";
			testParams.push(reallyLongString);
			var evenLongerString: String = "";
			for (i=0; i<100000; i++)
				evenLongerString = evenLongerString + "0123456789";
			testParams.push(evenLongerString);
			// Strings
			var strings: Array = new Array();
			strings.push("test");
			strings.push("test");
			strings.push("test");
			strings.push("test");
			testParams.push(strings);
			// Numbers
			testParams.push(0);
			testParams.push(1);
			testParams.push(-1);
			testParams.push(256);
			testParams.push(-256);
			testParams.push(65536);
			testParams.push(-65536);
			testParams.push(0.0);
			testParams.push(1.5);
			testParams.push(-1.5);
			testParams.push(new Array());
			// Arrays
			var tmp1: Array = new Array();
			tmp1.push(1);
			testParams.push(tmp1);
			testParams.push([1, 2]);
			testParams.push([1, 2, 3]);
			var tmp2: Array = new Array();
			tmp2.push(1);
			tmp2[100] = 100;
			testParams.push(tmp2);
			var tmp3: Array = new Array();
			tmp3.push(1);
			tmp3["one"] = 1;
			testParams.push(tmp3);
			// Object
			var tmp4: Object = {a: "foo", b: "bar"};
			testParams.push(tmp4);
			var tmp5: Array = new Array();
			tmp5.push(tmp4);
			tmp5.push(tmp4);
			testParams.push(tmp5);
			// Date
			var now: Date = new Date();
			testParams.push(now);
			var tmp6: Array = new Array();
			tmp6.push(now);
			tmp6.push(now);
			testParams.push(tmp6);
			// Custom class
			var tmp7: EchoClass = new EchoClass();
			tmp7.attr1 = "one";
			tmp7.attr2 = 1;
			testParams.push(tmp7);
			var tmp8: Array = new Array();
			tmp8.push(tmp7);
			tmp8.push(tmp7);
			testParams.push(tmp8);
			// Remote class
			var remote: RemoteClass = new RemoteClass();
			remote.attribute1 = "one";
			remote.attribute2 = 2;
			testParams.push(remote);
			var tmp9: Array = new Array();
			var remote1: RemoteClass = new RemoteClass();
			remote1.attribute1 = "one";
			remote1.attribute2 = 1;
			tmp8.push(remote1);
			var remote2: RemoteClass = new RemoteClass();
			remote2.attribute1 = "two";
			remote2.attribute2 = 2;
			tmp9.push(remote2);
			testParams.push(tmp9);
			var remote3: RemoteClass = new RemoteClass();
			remote3.attribute1 = "three";
			remote3.attribute2 = 1234567890;
			testParams.push(remote3);
			var remote4: RemoteClass = new RemoteClass();
			remote4.attribute1 = "four";
			remote4.attribute2 = 1185292800000;
			testParams.push(remote4);
			AMF0Count = testParams.length;
			
			// Add AMF3 specific tests below
			var ext: ExternalizableClass = new ExternalizableClass();
			testParams.push(ext);
			var tmp_1: Array = new Array();
			tmp_1.push(ext);
			tmp_1.push(ext);
			testParams.push(tmp_1);
			// ArrayCollection
			var tmp10: ArrayCollection = new ArrayCollection();
			tmp10.addItem("one");
			tmp10.addItem(1);
			tmp10.addItem(null);
			testParams.push(tmp10);
			// ObjectProxy
			var temp11: ObjectProxy = new ObjectProxy({ a: "foo", b: 5 });
			testParams.push(temp11);
			var temp12: Array = new Array();
			temp12.push(temp11);
			temp12.push(temp11);
			testParams.push(temp12);
  			// Create responder for result and error handlers
  			responder = new Responder( onRemotingResult, onRemotingError );
  			
			// Setup NetConnection
			nc = new NetConnection();
			nc.addEventListener( NetStatusEvent.NET_STATUS, netStatusHandler ); 
			nc.addEventListener( AsyncErrorEvent.ASYNC_ERROR, netASyncError );
            nc.addEventListener( SecurityErrorEvent.SECURITY_ERROR, netSecurityError );
            nc.addEventListener( IOErrorEvent.IO_ERROR, netIOError );
        }
		
		public function onConnect(protocol: String, encoding: uint): void {
			if (nc.connected) {
				nc.close();
			}
			nc.objectEncoding = encoding;
			var url: String;
			if (protocol == "http") {
			    // Remoting...
				url = http_txt.text;
			} else {
				// RTMP...
				url = rtmp_txt.text;
			}
			testResult = "Connecting through <b>" + protocol.toUpperCase() + "</b> using <b>AMF" + encoding;
			//
			if (protocol == "remoteObject") {
				echoService.endpoint = http_txt.text;
				if ( username_txt.text.length > 0 ) {
					echoService.setCredentials( username_txt.text, password_txt.text );
					printText( " (setCredentials)" );
				}
				printText( "</b>..." );
				onTest();
				return;
			}
			//
			echoService.endpoint = null;
			//
			nc.connect( url );
			if ( username_txt.text.length > 0 ) {
				nc.addHeader("Credentials", false, {userid: username_txt.text, password: password_txt.text});
				printText( " (setCredentials)" );
			}
			//
			printText( "</b>..." );
			
			if (protocol == "http") {
				// Don't wait for a successfull connection for remoting.
				onTest();
			}
		}
		
		private function printText( msg : String ) : void
		{
			testResult += msg;
		}
		
		private function netStatusHandler(event: NetStatusEvent): void {
			switch(event.info.code) {
				case "NetConnection.Connect.Success":
					printText( "<br>" + event.info.code + ": " + event.info.description );
					onTest();
					break;
				
				case "NetConnection.Connect.Rejected":
					printText( "<br>" + event.info.code + ": " + event.info.description );
					onDisconnect();
					break;
					
				case "NetConnection.Connect.Failed":
				case "NetConnection.Connect.Closed":
					printText( "<br>" + event.info.code );
					onDisconnect();
					break;
			}
		}
		
		private function netSecurityError( event : SecurityErrorEvent ) : void 
		{
			printText( "<br><b>" + failure + "Security error</font></b> - " + event.text );
		}
				
		private function netIOError( event : IOErrorEvent ) : void 
		{
			printText( "<br><b>" + failure + "IO error</font></b> - " + event.text + "<br>" );
		}
				
		private function netASyncError( event : AsyncErrorEvent ) : void 
		{
			printText( "<br><b>" + failure + "ASync error</font></b> - " + event.error + "<br>" );
		}
		
		private function doTest(): void {
			if (testParams[testIndex] is String && (testParams[testIndex] as String).length >= 100) {
				printText( "<br>Testing String with " + testParams[testIndex].length + " chars: " );
			} else {
				printText( "<br>Testing " + testParams[testIndex] + ": " );
			}
			if (echoService.endpoint == null) {
				// NetConnection requests
				nc.call("echo", responder, testParams[testIndex]);
			} else {
				// RemotingObject requests
				echoService.echo( testParams[testIndex] );
			}
		}
		
		public function onRemotingResult( result : * ): void {
			checkResult(result);
			var testCount: Number = testParams.length;
			if (nc.objectEncoding == ObjectEncoding.AMF0) {
				testCount = AMF0Count;
			}
			printTestResults( testCount );
		}
		
		public function onRemotingError( result : * ): void {
			var fault : Object; 
			if ( result.fault != null ) {
				// RemoteObject FaultEvent
				fault = result.fault.rootCause;
			} else {
				// NetConnection result object
				fault = result;
			}
			printText( "<br><b>" + failure + "AMF error received:</font></b>");
			printText( "<br>   <b>description</b>: " + fault.description);
			printText( "<br>   <b>type</b>: " + fault.type);
			printText( "<br>   <b>level</b>: " + fault.level);
			printText( "<br>   <b>code</b>: " + fault.code);
			printText( "<br>   <b>details</b>: " + fault.details);
			//
			onDisconnect();
		}
		
		private function onTest(): void {
			testIndex = 0;
			testsFailed = 0;
			timer = getTimer();
			doTest();
		}

		private function onDisconnect(): void {
			nc.close();
		}

		private function extendedEqual(a: Object, b: Object): Boolean {
			var key: String;
			if (a == null && b != null) {
				return false;
			} else if (a != null && b == null) {
				return false;
			} else if (a is Array && b is Array) {
				if (a.length != (b as Array).length) {
					return false;
				}
				var i: Number;
				for (i=0; i<(a as Array).length; i++) {
					if (!extendedEqual((a as Array)[i], (b as Array)[i])) {
						return false;
					}
				}
				return true;
			} else if (a is ExternalizableClass && b is ExternalizableClass) {
				return (a.failed == b.failed && a.failed == 0);
			} else if (a is Object && !(b is Object)) {
				for (key in a) {
					if (!extendedEqual(a[key], (b as Array)[key])) {
						return false;
					}
				}
				return true;
			} else if (!(a is Object) && b is Object) {
				for (key in b) {
					if (!extendedEqual((a as Array)[key], b[key])) {
						return false;
					}
				}
				return true;
			} else if (a is Object && b is Object) {
				for (key in a) {
					if (!extendedEqual(a[key], b[key])) {
						return false;
					}
				}
				return true;
			} else {
				return (a == b);
			}
		}
		
		private function checkResult(result: Object): void {
		    if (extendedEqual(testParams[testIndex], result)) {
				if (result == null)
					printText( success + "OK</font> (null)" );
				else if (result is String && (result as String).length >= 1000)
					printText( success + "OK</font> (String with " + result.length + " chars)" );
				else
					printText( success + "OK</font> (" + result.toString() + ")" );
			} else {
				if (result == null)
					printText( failure + "FAILED</font> (null)" );
				else if (result is String && (result as String).length >= 1000)
					printText( failure + "FAILED</font> (String with " + result.length + " chars)" );
				else
					printText( failure + "FAILED</font> (" + result.toString() + ")" );
				testsFailed++;
			}
			testIndex += 1;
		}
		
		private function printTestResults( testCount : Number ) : void
		{
			var testTime : Number = (getTimer() - timer)/1000;
			if (testIndex < testCount) {
				doTest();
			} else if (testsFailed == 0) {
				printText( "<br><b>Successfully ran " + success + testCount + "</font> test(s) in " + testTime + " seconds.</b>" );
				onDisconnect();
			} else {
				printText( "<br><b>Ran " + success + testCount + "</font> test(s) in " + testTime + " seconds, " + 
							failure + testsFailed + "</font> test(s) failed.</b>" );
				onDisconnect();
			}
		}
		
	}
}
