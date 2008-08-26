package org.red5.flash.bwcheck
{
	import flash.events.NetStatusEvent;
	import flash.net.NetConnection;
	
	public class BandwidthDetection
	{
		private var _serverURL:String = "localhost";
		private var _serverApplication:String = "";
		private var _clientServerService:String = "";
		private var _serverClientService:String = "";
		
		private var nc:NetConnection;
		
		public function BandwidthDetection()
		{
			
		}
		
		public function set serverURL(url:String):void
		{
			_serverURL = url;
		}
		
		public function set serverApplication(app:String):void
		{
			_serverApplication = app;
		}
		
		public function set clientServerService(service:String):void
		{
			_clientServerService = service;
		}
		
		public function set serverClientService(service:String):void
		{
			_serverClientService = service;
		}
		
		private function connect():void
		{
			nc = new NetConnection();
			nc.client = this;
			nc.addEventListener(NetStatusEvent.NET_STATUS, onStatus);	
			nc.connect("rtmp://" + _serverURL + "/" + _serverApplication);
		}
		
		private function onStatus(event:NetStatusEvent):void
		{
			trace(event.info.code);
		}
			
		public function ClientServer():void
		{
			var clientServer:ClientServerBandwidth  = new ClientServerBandwidth();
			connect();
			clientServer.connection = nc;
			clientServer.service = _clientServerService;
			clientServer.start();
		}
		
		public function ServerClient():void
		{
			var serverClient:ServerClientBandwidth = new ServerClientBandwidth();
			connect();
			serverClient.connection = nc;
			serverClient.service = _serverClientService;
			serverClient.start();
		}

	}
}