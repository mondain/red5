package org.red5.flash.bwcheck
{
	import flash.net.Responder;
	
	public class ServerClientBandwidth extends BandwidthDetection
	{
		
		private var _service:String;
		private var info:Object = new Object();
		private var res:Responder;
		
		public function ServerClientBandwidth()
		{
			res = new Responder(onResult, onStatus);
		}

		public function onBWCheck(obj:Object):void
		{
				trace("Checking Bandwidth");
				//dispatchStatus(info);
				//log.data = "Checking Bandwidth ..... \n\n";
		}
			
		public function onBWDone(kbitDown:String, deltaDown:String, deltaTime:String, latency:String):void 
		{ 
  			var info:Object = new Object();
			info.kbitDown = kbitDown;
			info.deltaDown = deltaDown;
			info.deltaTime = deltaTime;
			info.latency = latency;
			//info.KBytes = KBytes;
			
			dispatchComplete(info);
  			
  			//log.data += "\n\n kbit Down: " + kbitDown + " Delta Down: " + deltaDown + " Delta Time: " + deltaTime + " Latency: " + latency; 
		} 
		
		public function set service(service:String):void
		{
			_service = service;
		}
		
		public function start():void
		{
			nc.client = this;
			nc.call(_service,res);
		}
		
		private function onResult(obj:Object):void
		{
					
		}
		
		private function onStatus(obj:Object):void
		{
			switch (obj.code)
			{
				case "NetConnection.Call.Failed":
					dispatchFailed(obj);
				break;
			}

		}
	}
}