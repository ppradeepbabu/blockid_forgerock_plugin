/*
 *
 * Copyright 1Kosmos Inc
 */
package com.bidsdk.UWL2REST;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Uwl2RestApplication.class);
	}

}
