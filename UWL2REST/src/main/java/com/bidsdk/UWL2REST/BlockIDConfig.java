/*
 *
 * Copyright 1Kosmos Inc
 */
package com.bidsdk.UWL2REST;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlockIDConfig {
	
	@Value("${uwl.dns.url}")
	private String DNS_URL;
	@Value("${uwl.license.key}")
	private String LICENSE_KEY;
	@Value("${uwl.community.name}")
	private String COMMUNITY_NAME;
	
	public String getdnsurl() {
        return DNS_URL;
    }

    public void setdnsurl(String DNS_URL) {
        this.DNS_URL = DNS_URL;
    }
    
    public String getlicensekey() {
        return LICENSE_KEY;
    }

    public void setlicensekey(String LICENSE_KEY) {
        this.LICENSE_KEY = LICENSE_KEY;
    }
    
    public String getcommunityname() {
        return COMMUNITY_NAME;
    }

    public void setcommunityname(String COMMUNITY_NAME) {
        this.COMMUNITY_NAME = COMMUNITY_NAME;
    }
}
