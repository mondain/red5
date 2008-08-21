/**
 * Application installer main script
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
	
	import flash.events.*;
	import flash.media.*;
	import flash.net.*;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.core.*;
	import mx.events.*;
	import mx.rpc.events.ResultEvent;
	
	private var nc:NetConnection;
	private var ns:NetStream;

	[Bindable]
	private var hostString:String = 'localhost';

	[Bindable]
	public var clientId:String = '';
	
	[Bindable]	
	public var applicationList:ArrayCollection = new ArrayCollection();
	
	[Bindable]	
	public var selectedFilename:String = null;
	
	public function init():void {
		Security.allowDomain("*");
		
		var pattern:RegExp = new RegExp("http://([^/]*)/");				
		if (pattern.test(Application.application.url) == true) {
			var results:Array = pattern.exec(Application.application.url);
			hostString = results[1];
			//need to strip the port to avoid confusion
			if (hostString.indexOf(":") > 0) {
				hostString = hostString.split(":")[0];
			}
		}
		log('Host: ' + hostString);	
	}

	public function onBWDone():void {
		// have to have this for an RTMP connection
		log('onBWDone');
	}

	public function onBWCheck(... rest):uint {
		log('onBWCheck');
		//have to return something, so returning anything :)
		return 0;
	}

	private function netStatusHandler(event:NetStatusEvent):void {
		log('Net status: '+event.info.code);
        switch (event.info.code) {
            case "NetConnection.Connect.Success":
                connectorbtn.label = "Disconnect";             
            	getList();
            	//applistRPC.send();
                break;
            case "NetConnection.Connect.Failed":
                break;
            case "NetConnection.Connect.Rejected":
            	break;
            case "NetConnection.Connect.Closed":	                
				connectorbtn.label = 'Connect';
            	listbtn.enabled = false;
				break;                
        }				
	}	
	       
	//called by the server
	public function setClientId(param:Object):void {
		log('Set client id called: '+param);
		clientId = param as String;
		log('Setting client id: '+clientId);
	}
	
	//called by the server in the event of a server side error
	public function onAlert(alert:Object):void {
		log('Got an alert: '+alert);
		Alert.show(String(alert), 'Alert');
	}	
	
	public function connect():void {       	
    	log('Trying to ' + connectorbtn.label);	
		if (connectorbtn.label === 'Connect') {
			//  create the netConnection
			nc = new NetConnection();
			nc.objectEncoding = ObjectEncoding.AMF3;
			//  set it's client/focus to this
			nc.client = this;
	
			// add listeners for netstatus and security issues
			nc.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);
			nc.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
			nc.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
			nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR, asyncErrorHandler);
	
		    nc.connect('rtmp://' + hostString + '/installer', null);   
		} else if (connectorbtn.label === 'Disconnect') {
			if (nc.connected) {
				nc.close();
			}
		}	    					
    }
    
    public function getList():void {
    	var res:Responder = new Responder(handleAppList, null);
       	nc.call("installer.getApplicationList", res);    	
    }
    
	//callback handler
    public function handleAppList(resp:Object):void {
		//log('handle Application list ' + resp);
		traceObject(resp);
		try {
			var s:String = resp.body as String;
			//log('Raw string: ' + s);
			var xml:XML = new XML(s);
			//log('XML: ' + xml);
			var arr:Array = new Array();
			for each (var property:XML in xml..application) {
				log('Property: ' + property);				
				var item:Item = new Item();
				item.name = String(property.@name);
				item.description = property.desc;
				item.author = property.author;
				item.filename = property.filename;
				arr.push(item);
			}
			applicationList = new ArrayCollection(arr);
			log('Got the application list');
			listbtn.enabled = true;
  		} catch (e) {
  			log(e);
  		}		
	}    
	
	private function rpcHandler(event:ResultEvent):void {
		var arr:Array = new Array();
		for each (var s:XML in event.result..application){
			log(s);
			var item:Item = new Item();
			item.name = String(s.@name);
			item.description = s.desc;
			item.author = s.author;
			item.filename = s.filename;
			arr.push(item);
		}
		applicationList = new ArrayCollection(arr);
		listbtn.enabled = true;
	}	
	
	public function handleClick(event:ListEvent):void {
		traceObject(event);
        if (event.rowIndex > 0) {
        	selectedApp.text = grid.selectedItem.name;
        	selectedFilename = grid.selectedItem.filename;
        }
	}
    
   	public function install():void {   
   		if (selectedFilename != null) {
   			nc.call("installer.install", null, selectedFilename);   
   		}
   	}

   	public function uninstall():void {   
   		
   	}
    
	private function securityErrorHandler(e:SecurityErrorEvent):void {
		log('Security Error: '+e);
	}

	private function ioErrorHandler(e:IOErrorEvent):void {
		log('IO Error: '+e);
	}
	
	private function asyncErrorHandler(e:AsyncErrorEvent):void {
		log('Async Error: '+e);
	}
	
	public function log(text:String):void {
		trace(text);
		messages.text += text + '\n';
	}

	public function traceObject(obj:Object, indent:uint = 0):void {
	    var indentString:String = "";
	    var i:uint;
	    var prop:String;
	    var val:*;
	    for (i = 0; i < indent; i++) {
	        indentString += "\t";
	    }
	    for (prop in obj) {
	        val = obj[prop];
	        if (typeof(val) == "object") {
	            log(indentString + " " + i + ": [Object]");
	            traceObject(val, indent + 1);
	        } else {
	            log(indentString + " " + prop + ": " + val);
	        }
	    }
	}    
    	
	
