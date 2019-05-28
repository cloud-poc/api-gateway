package org.akj.springboot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HealthCheckController {

	@Value("${health-check.enabled}")
	private boolean hcIndicator;

	@GetMapping("/health-check")
	public Map<String, Object> healthCheck() {
		Map<String, Object> response = new HashMap<String, Object>();

		if (hcIndicator) {
			response.put("health-check.status", "running");
		} else {
			response.put("health-check.enabled", hcIndicator);
		}

		return response;
	}

}
