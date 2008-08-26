package org.red5.flash.bwcheck
{
	import flash.net.NetConnection;
	
	public class ServerClientBandwidth
	{
		private var nc:NetConnection;
		private var _service:String;
		
		public function ServerClientBandwidth()
		{
		}
		
		public function onBWCheck(obj:Object):void
		{
				trace("Checking Bandwidth");
				//log.data = "Checking Bandwidth ..... \n\n";
		}
			
		public function onBWDone(kbitDown:String, deltaDown:String, deltaTime:String, latency:String):void 
		{ 
  			trace("kbit Down: " + kbitDown + " Delta Down: " + deltaDown + " Delta Time: " + deltaTime + " Latency: " + latency); 
  			//log.data += "\n\n kbit Down: " + kbitDown + " Delta Down: " + deltaDown + " Delta Time: " + deltaTime + " Latency: " + latency; 
		} 
		
		public function set connection(connect:NetConnection):void
		{
			nc = connect;
		}
		
		public function set service(service:String):void
		{
			_service = service;
		}
		
		public function start():void
		{
			nc.client = this;
			nc.call(_service,null);
		}
	}
}