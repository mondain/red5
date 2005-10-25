meta.addInterface('org.red5.server.service.IScriptServiceFCS');

function echoString(str){
	return str;
}

function echoArray(arr){
	return arr;
}


function echoList(list){
	return list;
}


function echoMultiParam(p1,p2,p3){
	return [p1,p2,p3];
}


function echoObject(obj){
	return obj;
}

function echoNumber(num){
	return num;
}

function echoBoolean(bol){
	return bol;
}

log.info("Scripted script service init");