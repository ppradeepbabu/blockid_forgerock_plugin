/**
 * Copyright (c) 2018, 1Kosmos Inc. All rights reserved.
 * Licensed under 1Kosmos Open Source Public License version 1.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of this license at
 *    https://github.com/1Kosmos/1Kosmos_License/blob/main/LICENSE.txt
 */
package com.blockid;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.bidsdk.BIDSessions;
import com.bidsdk.model.BIDSession;
import com.bidsdk.model.BIDSessionResponse;
import com.bidsdk.model.BIDTenantInfo;

@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class BlockIDPasswordlessApplication extends SpringBootServletInitializer{
	
	private final Logger logger = LoggerFactory.getLogger(BlockIDPasswordlessApplication.class); 
	
	@Autowired
	private BlockIDConfig blockIDConfig = new BlockIDConfig();
		
	public static void main(String[] args) {
		SpringApplication.run(BlockIDPasswordlessApplication.class, args);
	}
	
	@Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	  return builder.sources(BlockIDPasswordlessApplication.class);
	 }

	public JSONObject getSessionURL() {
		logger.debug("UWL 2.0 setup loaded");
		String dns = blockIDConfig.getdnsurl();
		String communityName = blockIDConfig.getcommunityname();
		String licenseKey = blockIDConfig.getlicensekey();
	
		BIDTenantInfo tenantInfo = new BIDTenantInfo(dns, communityName, licenseKey);
		 
		BIDSession session = BIDSessions.createNewSession(tenantInfo, "Fingerprint", "firstname, lastname, ial, aal, device_info, location");

		String sessionID = session.sessionId;
		String sessionURL = session.url;
		String QRCodeURL = sessionURL + "/session/" + sessionID;

		logger.debug("QR Code URL:"+QRCodeURL);
		JSONObject responseJson = new JSONObject();
		responseJson.put("sessionURL", QRCodeURL);
		responseJson.put("sessionID", sessionID);
		return responseJson;
	}

	public JSONObject pollSession(String sessionID) {
		logger.debug("SessionID in Uwl2RestApplication pollSession : " + sessionID);
		
		String dns = blockIDConfig.getdnsurl();
		String communityName = blockIDConfig.getcommunityname();
		String licenseKey = blockIDConfig.getlicensekey();
	
		BIDTenantInfo tenantInfo = new BIDTenantInfo(dns, communityName, licenseKey);
		 
		BIDSession session = BIDSessions.createNewSession(tenantInfo, "Fingerprint", "firstname, lastname, ial, aal, device_info, location");

		logger.debug("polling sessionID : " + sessionID);
		BIDSessionResponse sessionResp = BIDSessions.pollSession(tenantInfo, sessionID, true, true);
		logger.debug(sessionResp.message);
		Map<String, Object> responseUserData = sessionResp.user_data;
		int status = sessionResp.status;
		String data = sessionResp.data;
		
		logger.debug("  data read completed ....... ");


		JSONObject responseJson = new JSONObject();
		responseJson.put("userData", responseUserData);
		responseJson.put("status", status);
		responseJson.put("data", data);

		logger.debug("response JSON from pollSession method: " + responseJson);
		return responseJson;
	}
	
}
