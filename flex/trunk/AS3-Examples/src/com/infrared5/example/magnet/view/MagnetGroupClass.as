package com.infrared5.example.magnet.view
{
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.events.NetStatusEvent;
	import flash.events.SyncEvent;
	import flash.geom.ColorTransform;
	import flash.net.NetConnection;
	import flash.net.SharedObject;
	
	import mx.controls.Alert;
	import mx.controls.Image;
	import mx.events.FlexEvent;
	
	import spark.components.Group;
	
	/**
	 * A Container for remote magnet objects.
	 *  
	 * @author dominickaccattato
	 * 
	 */
	public class MagnetGroupClass extends Group
	{
		private var magnetMap:Object = new Object();
		private var magnetRSO:SharedObject;
		private var _connection:NetConnection;
		
		/**
		 * Constructor. 
		 */
		public function MagnetGroupClass()
		{
			super();
			addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
		}
		
		/**
		 * An event handler for flex creation_complete events. This 
		 * is dispatched when all the children are complete.
		 *  
		 * @param event The FlexEvent dispatched.
		 * 
		 */
		private function onCreationComplete(event:FlexEvent) : void {
			// setup connection code
			_connection = new NetConnection();
			_connection.connect("rtmp://localhost/live");
			_connection.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
			_connection.client = this;
		}
		
		/**
		 * An event handler for netstatus events. These events
		 * are dispatched from a NetConnection.
		 *  
		 * @param event The NetStatusEvent dispatched.
		 * 
		 */
		private function onNetStatus(event:NetStatusEvent) : void {
			if(event.info.code == "NetConnection.Connect.Success") {
				createMagnetRSO();
			} else {
				Alert.show("error loading", "Error");
			}
		}
		
		/**
		 * A helper method for creating the magnet remote SharedObject. 
		 */
		private function createMagnetRSO() : void {
			magnetRSO = SharedObject.getRemote("coordinates", _connection.uri, false);
			magnetRSO.addEventListener(SyncEvent.SYNC, onSync);
			magnetRSO.connect(_connection);
		}
		
		/**
		 * Overrides the flex createChildren() method. Here, we 
		 * can create our children. 
		 */
		override protected function createChildren() : void {
			// use a helper to grab the image associated with the letter [A-Z]		
			var helper:MagnetSkinUtil = new MagnetSkinUtil();
			
			// create a matrix of letters
			for(var i:int=0; i<15; i++) {
				
				// loop through the character codes
				for(var j:int=65; j<90; j++) {
					
					// grab the letter from the character code
					var char:String = String.fromCharCode(j);
					
					// create an image and set the source to one of
					// the embedded letter symbols
					var magnet:Image = new Image();
					magnet = new Image();
					var letter:Sprite = helper.getLetter(char);
					magnet.source = letter;
					magnet.width = 15;
					magnet.height = 15;
					
					// move it's x/y position in the matrix coordinates
					// add a random rotation for fun
					magnet.move(15 * (j-65), (i*15));
					magnet.rotationZ = Math.random() * 15;
					
					// change the colors of the magnets
					var tmpColor:ColorTransform = magnet.transform.colorTransform;
					tmpColor.color = 0xFF0000;
					tmpColor.color = Math.round( Math.random()*0xFFFFFF );
					magnet.transform.colorTransform = tmpColor;
					
					// add the listeners so we can drag the magnets
					magnet.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
					magnet.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
					
					// give the magnet an ID and add to a magNetMap
					magnet.id = char + i;
					magnetMap[char + i] = magnet;
					
					// finally, add the magnet to the displayList
					this.addElement(magnet);
				}
			}
		}
		
		/**
		 * An event handler for when the mouse is down
		 * over the magnet.
		 *  
		 * @param event The MouseEvent dispatched.
		 * 
		 */
		private function onMouseDown(event:MouseEvent) : void {
			var magnet:Image = Image(event.currentTarget);
			magnet.startDrag();
			magnet.addEventListener(MouseEvent.MOUSE_MOVE, onMagnetMove);
		}
		
		/**
		 * An event handler for when the mouse is up
		 * over the magnet.
		 * 
		 * @param event The MouseEvent dispatched.
		 * 
		 */
		private function onMouseUp(event:MouseEvent) : void {
			var magnet:Image = Image(event.currentTarget);
			magnet.stopDrag();
			magnet.removeEventListener(MouseEvent.MOUSE_MOVE, onMagnetMove);
		}
		
		/**
		 * An event handler for when the mouse is moved
		 * while the magnet is dragging.
		 * 
		 * @param event The MouseEvent dispatched.
		 * 
		 */
		private function onMagnetMove(event:MouseEvent) : void {
			var magnet:Image = Image(event.currentTarget);
			magnetRSO.setProperty(magnet.id, {x:magnet.x, y:magnet.y});
		}
		
		
		/**
		 * An event handler for sync events dispatched from
		 * the remote SharedObject.
		 * 
		 * @param event The SyncEvent passed in.
		 * 
		 */
		private function onSync(event:SyncEvent) : void {
			
			// loop through the changeList
			for(var i:Object in event.changeList) {
				var changeObj:Object = event.changeList[i];
				
				switch(changeObj.code) {
					case "success":
						break;
					
					case "change":
						var magnetId:String = changeObj.name;
						var coordinates:Object = magnetRSO.data[magnetId];
						magnetMap[magnetId].move(coordinates.x, coordinates.y);
						break;
				}
				
			}
		}
		
	}
}