/**
 * Copyright (c) 2018, 1Kosmos Inc. All rights reserved.
 * Licensed under 1Kosmos Open Source Public License version 1.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of this license at
 *    https://github.com/1Kosmos/1Kosmos_License/blob/main/LICENSE.txt
 */
package com.blockid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockIDPasswordlessController {
	
	@GetMapping("/healthcheck")
	public String healthcheck() {
		return "Greetings from BlockID Passwordless REST API";
	}
	
	@Autowired private BlockIDPasswordlessApplication blockidPasswordlessApplication;

    @RequestMapping(method = RequestMethod.GET,value="/getSessionURL")  
    public @ResponseBody String getSessionURL() {
        return blockidPasswordlessApplication.getSessionURL().toString();
    }
	
    @RequestMapping(method = RequestMethod.POST,value="/pollSession")  
    public @ResponseBody String pollSession(@RequestParam String sessionID) {
        return blockidPasswordlessApplication.pollSession(sessionID).toString();
    }

}
