package com.bidsdk.UWL2REST;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Uwl2RestController {
	
	@GetMapping("/healthcheck")
	public String healthcheck() {
		return "Greetings from BlockID UWL REST API";
	}
	
	@Autowired private Uwl2RestApplication uwl2RestApplication;

    @RequestMapping(method = RequestMethod.GET,value="/getSessionURL")  
    public @ResponseBody String getSessionURL() {
        return uwl2RestApplication.getSessionURL().toString();
    }
	
    @RequestMapping(method = RequestMethod.POST,value="/pollSession")  
    public @ResponseBody String pollSession(@RequestParam String sessionID) {
        return uwl2RestApplication.pollSession(sessionID).toString();
    }

}
