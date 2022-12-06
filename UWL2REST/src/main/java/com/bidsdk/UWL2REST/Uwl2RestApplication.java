/*
 *
 * Copyright 1Kosmos Inc
 */
package com.bidsdk.UWL2REST;

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

import com.bidsdk.BIDSDK;
import com.bidsdk.BIDSessions;
import com.bidsdk.model.BIDDevice;
import com.bidsdk.model.BIDPoNData;
import com.bidsdk.model.BIDSession;
import com.bidsdk.model.BIDSessionResponse;
import com.bidsdk.model.BIDTenant;

@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class Uwl2RestApplication extends SpringBootServletInitializer{
	
	private final Logger logger = LoggerFactory.getLogger(Uwl2RestApplication.class); 
	
	@Autowired
	private BlockIDConfig blockIDConfig = new BlockIDConfig();
		
	public static void main(String[] args) {
		SpringApplication.run(Uwl2RestApplication.class, args);
	}
	
	@Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	  return builder.sources(Uwl2RestApplication.class);
	 }

	public JSONObject getSessionURL() {
		BIDTenant bidTenant = new BIDTenant();
		bidTenant.dns = blockIDConfig.getdnsurl();
		bidTenant.communityName = blockIDConfig.getcommunityname();

		BIDSDK.getInstance().setupTenant(bidTenant, blockIDConfig.getlicensekey());
		logger.debug("UWL 2.0 setup loaded");

		BIDSession session = BIDSessions.createNewSession("Fingerprint",
				"firstname, lastname, ial, aal, device_info, location");
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
		BIDTenant bidTenant = new BIDTenant();
		bidTenant.dns = blockIDConfig.getdnsurl();
		bidTenant.communityName = blockIDConfig.getcommunityname();

		BIDSDK.getInstance().setupTenant(bidTenant, blockIDConfig.getlicensekey());
		logger.debug("polling sessionID : " + sessionID);
		BIDSessionResponse sessionResp = BIDSessions.pollSession(sessionID, true, true);
		logger.debug(sessionResp.message);
		Map<String, Object> responseUserData = sessionResp.user_data;
		int status = sessionResp.status;
		String data = sessionResp.data;
		
		logger.debug("  data read completed ....... ");
		BIDPoNData accountData = sessionResp.account_data;
		String lat = "";
		String longi = "";
		String deviceName = "";
		if (accountData != null) {
			BIDDevice bidDevice = accountData.device;
			lat = bidDevice.locLat.toString();
			longi = bidDevice.locLon.toString();
			deviceName = bidDevice.deviceName;
			logger.debug(" reading lattitude longitude & devicename....... "+lat+longi+deviceName);
		}


		JSONObject responseJson = new JSONObject();
		responseJson.put("userData", responseUserData);
		responseJson.put("status", status);
		responseJson.put("data", data);
		responseJson.put("lat", lat);
		responseJson.put("longi", longi);
		responseJson.put("device", deviceName);

		logger.debug("response JSON from pollSession method: " + responseJson);
		return responseJson;
	}
	
}
