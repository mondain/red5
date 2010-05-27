package com.infrared5.example.magnet.view
{
	import flash.display.Sprite;

	public class MagnetSkinUtil
	{
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaA")] public var A:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaB")] public var B:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaC")] public var C:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaD")] public var D:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaE")] public var E:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaF")] public var F:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaG")] public var G:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaH")] public var H:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaI")] public var I:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaJ")] public var J:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaK")] public var K:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaL")] public var L:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaM")] public var M:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaN")] public var N:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaO")] public var O:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaP")] public var P:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaQ")] public var Q:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaR")] public var R:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaS")] public var S:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaT")] public var T:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaU")] public var U:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaV")] public var V:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaW")] public var W:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaX")] public var X:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaY")] public var Y:Class;
		[Embed(source="assets/swf/Alphabet.swf", symbol="alphaZ")] public var Z:Class;
		
		[Embed(source="assets/swf/Alphabet.swf", symbol="fridge", scaleGridTop="9", scaleGridBottom="10", scaleGridLeft="9", scaleGridRight="10")] public var fridge:Class;
		
		
		public function MagnetSkinUtil()
		{
		}
		
		public function getLetter(char:String) : Sprite {
			return new this[char]();
		}
	}
}