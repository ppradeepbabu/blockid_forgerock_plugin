/*
  - Data made available by nodes that have already executed are available in the sharedState variable.
  - The script should set outcome to either "true" or "false".
 */

var fr=JavaImporter(
  org.forgerock.openam.auth.node.api.Action,
  org.forgerock.http.protocol,
  org.forgerock.json,
  java.lang.String,
  com.sun.identity.authentication.callbacks.ScriptTextOutputCallback,
  org.forgerock.openam.authentication.callbacks.PollingWaitCallback,
  com.sun.identity.authentication.callbacks.HiddenValueCallback
 )

try {
	var blockIDPasswordlessURL = "https://<<HOST_NAME>>:<<PORT>>/BlockIDPasswordless";	
	if (callbacks.isEmpty()) {
        //retrieve BlockID session URL for polling
        var sessionURL =  getBlockIDSessionURL();
        var URL = sessionURL.toString();
        var elementid = "callback_1";
        var versionNumber = 20;
        var code = "L";
        var qrdata = String("QRCodeReader.createCode({ \n " +
              " id:'" + elementid + "', \n " +
              " text:'" + URL + "', \n " + 
              " version:'" + versionNumber + "', \n" +
              " code: '" + code + "' \n " +
              " });");  
        //Display QR Code via callbacks
        action = fr.Action.send(
          new fr.HiddenValueCallback("qrdata", qrdata),
          new fr.ScriptTextOutputCallback(qrdata),
          new fr.PollingWaitCallback("5000","SCAN ABOVE QR WITH BLOCKID APP")
        ).build();
    } else {
            //Poll BlockID session
            startPolling(sharedState.get("sessionID").toString());
    }  
} catch (err) {
  	logger.error("Unable to display QR Code");
}  

function getBlockIDSessionURL() {
  	 try{
           logger.message ("BlockID QR Code Display :::getBlockIDSessionURL()  Start ");
           //Initiate session URL against getSessionURL REST endpoint
           var request = new org.forgerock.http.protocol.Request();
           request.setUri(blockIDPasswordlessURL + "/getSessionURL");
           request.setMethod("GET");
           var response = httpClient.send(request).get();
           var jsonResult = response.getEntity().getJson();
           logger.message("JSON Value of result is: " + jsonResult);
           var jsonObject= new org.forgerock.json.JsonValue(jsonResult);
           //Add sessionID into sharedstate for polling
           sharedState.put("sessionID",jsonObject.get('sessionID').toString().replaceAll('\"', ''));
           var sessionURL = jsonObject.get('sessionURL').toString().replaceAll('\"', '');
           logger.message("BlockID QR Code Display :::getBlockIDSessionURL()  End "); 
           return sessionURL.toString();
     } 
  	 catch (err) {
             logger.error("BlockID Session Creation REST Endpoint is not available");
     }
}

function startPolling(sessionID){
	try {
   		      logger.message("BlockID QR Code Display :::startPolling()  Start "); 
              logger.message("sessionID value inside startPolling: " + sessionID); 
              var pollingURL = blockIDPasswordlessURL + "/pollSession?sessionID="+sessionID;

              //Initiate polling session against pollSession REST endpoint
              var request = new org.forgerock.http.protocol.Request();
              request.setUri(pollingURL);
              request.setMethod("POST");
              var response = httpClient.send(request).get();
              var jsonResult = response.getEntity().getJson();
              var jsonObject= new org.forgerock.json.JsonValue(jsonResult);
              logger.message("JSON Object is: " + jsonObject + " AND jsonObject.status is: " + jsonObject.get('status'));
              if(jsonObject.get('status') == 200){
                //logger.message("User Data Object is: " +jsonObject.get('userData'));
                var userid = jsonObject.get('userData').get('userid').toString().replaceAll('\"', '');
                if(userid == ""){
                  logger.error("userid not found in received response from polling");
                }else{
                  //mark it as success processing state and redirect
                  logger.message(userid + " is the user ID....");
                  logger.message(jsonObject.get('userData').get('did').toString().replaceAll('\"', '')+" is did");
                  sharedState.put("username",userid.toString());
                  //retrieve referer & target application value
                  var referer = requestHeaders.get("referer").get(0).toString();
                  if(referer.indexOf("resourceURL=") > 0) {
                      var myArray = referer.split("resourceURL=");
                      var targetURL = myArray[1].toString();
                      logger.error ("Target Application URL value is: "+ decodeURIComponent(targetURL));
                      //sharedState.put("successUrl",decodeURIComponent(targetURL));
                      sharedState.put("goto",decodeURIComponent(targetURL));
                  }
                  outcome = "true";
                } 
              }
              else if(jsonObject.get('status') == 404){
                logger.message("******User data not present. Polling again**********");
                startPolling(sessionID);
              } 
              else{
                action = fr.Action.send("false").withErrorMessage("Something went wrong during BlockID Polling Session").build();
              }  
              logger.message("BlockID QR Code Display :::startPolling()  End ");  
    } catch (err) {
      		  logger.error("BlockID Session Polling has resulted in error");
    }  
}