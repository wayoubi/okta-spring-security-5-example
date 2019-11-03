package com.okta.developer.oidc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OidcApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OidcApplication.class);
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("server.port", "443");
		configuration.put("server.ssl.key-store", "resources/store.okta-demo.online.jks");
		configuration.put("server.ssl.keyStoreType", "jks");
		configuration.put("server.ssl.keyAlias", "store.okta-demo.online");
		configuration.put("server.ssl.key-store-password", "changeit");
		
		
		
		app.setDefaultProperties(configuration);
		app.run(args);
	}
}
