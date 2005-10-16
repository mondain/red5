meta.addMethod('echoString','String','String');
function echoString(str){
	log.info("Hello log file");
	return str;
}
print("Woot this works");