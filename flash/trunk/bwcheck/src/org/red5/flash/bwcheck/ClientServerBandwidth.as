package org.red5.flash.bwcheck
{
	import flash.net.NetConnection;
	import flash.net.Responder;
	import flash.events.EventDispatcher;
	
	import org.red5.flash.bwcheck.events.BandwidthDetectEvent;
	
	[Event(name=BandwidthDetectEvent.DETECT_STATUS, type="org.red5.flash.bwcheck.events.BandwidthDetectEvent")]
	[Event(name=BandwidthDetectEvent.DETECT_COMPLETE, type="org.red5.flash.bwcheck.events.BandwidthDetectEvent")]
	
	public class ClientServerBandwidth extends EventDispatcher
	{
		private var nc:NetConnection;
		private var res:Responder;
		private var payload:Array = new Array();
		
		private var latency:int = 0;
		private var cumLatency:int = 1;
		private var bwTime:int = 0;
		private var count:int = 0;
		private var sent:int = 0;
		private var kbitUp:int = 0;
		private var deltaUp:int = 0;
		private var deltaTime:int = 0;
		private var overhead:int = 0;

		private var pakSent:Array = new Array();
		private var pakRecv:Array = new Array();
		private var beginningValues:Object = {};
		
		private var _service:String;
		
		public function ClientServerBandwidth()
		{
			for (var i:int = 0; i < 1200; i++){
				payload[i] = Math.random();	//16K approx
			}
			
			res = new Responder(onResult, onStatus);
			
			
		}
		
		private function dispatch(info:Object, eventName:String):void
		{
			var event:BandwidthDetectEvent = new BandwidthDetectEvent(eventName);
			event.info = info;
			dispatchEvent(event);
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
			var obj:Array = new Array();
			obj[0] = "1";
			nc.call(_service, res, new Array());
			dispatch(null, BandwidthDetectEvent.DETECT_STATUS);
		}
		
		private function onResult(obj:Object):void
		{
			var now:int = (new Date()).getTime()/1;
			if(sent == 0) {
				this.beginningValues = obj;
				this.beginningValues.time = now;
				this.pakSent[sent++] = now;
				nc.call(_service, res, now);
				dispatch(null, BandwidthDetectEvent.DETECT_STATUS);
			} else {
				this.pakRecv[this.count] = now;
				trace( "Packet interval = " + (this.pakRecv[this.count] - this.pakSent[this.count])*1  );
				this.count++;
				var timePassed:int = (now - this.beginningValues.time);
				
				if (this.count == 1) {
					this.latency = Math.min(timePassed, 800);
					this.latency = Math.max(this.latency, 10);
					this.overhead = obj.cOutBytes - this.beginningValues.cOutBytes;
					trace("overhead: "+this.overhead);
					this.pakSent[sent++] = now;
					nc.call(_service, res, now, payload);
					dispatch(null, BandwidthDetectEvent.DETECT_STATUS);
				}
				trace("count: "+this.count+ " sent: "+this.sent+" timePassed: "+timePassed+" latency: "+this.latency);
			
				// If we have a hi-speed network with low latency send more to determine
				// better bandwidth numbers, send no more than 6 packets
				if ( (this.count >= 1) && (timePassed<1000))
				{
					this.pakSent[sent++] = now;
					this.cumLatency++;
					nc.call(_service, res, now, payload);
					dispatch(null, BandwidthDetectEvent.DETECT_STATUS);
				} else if ( this.sent == this.count ) {	
					// See if we need to normalize latency
					if ( this.latency >= 100 )
					{ // make sure we detect sattelite and modem correctly
						if (  this.pakRecv[1] - this.pakRecv[0] > 1000 )
						{
							this.latency = 100;
						}
					}
					payload = new Array();
					// Got back responses for all the packets compute the bandwidth.
					var stats:Object = obj;
					deltaUp = (stats.cOutBytes - this.beginningValues.cOutBytes)*8/1000;
					deltaTime = ((now - this.beginningValues.time) - (this.latency * this.cumLatency) )/1000;
					if ( deltaTime <= 0 )
						deltaTime = (now - this.beginningValues.time)/1000;
					
					kbitUp = Math.round(deltaUp/deltaTime);
					dispatch(null, BandwidthDetectEvent.DETECT_COMPLETE);
					trace("onBWDone: kbitUp = " + kbitUp + ", deltaUp= " + deltaUp + ", deltaTime = " + deltaTime + ", latency = " + this.latency + " KBytes " + (stats.cOutBytes - this.beginningValues.cOutBytes)/1024) ;
				}
			}
		}
		
		private function onStatus(obj:Object):void
		{
			
		}
	}
}
