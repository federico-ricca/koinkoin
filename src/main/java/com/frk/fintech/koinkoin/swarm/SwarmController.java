package com.frk.fintech.koinkoin.swarm;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwarmController {

	@RequestMapping(value = "/v1/swarm", method = RequestMethod.POST)
	public HttpEntity<SwarmResponse> createSwarm(
			@RequestBody SwarmRequestBody body) {
		SwarmResponse swarmResponse = null;
		return new ResponseEntity<SwarmResponse>(swarmResponse, HttpStatus.OK);
	}

	public void startSwarm() {

	}

	public void stopSwarm() {

	}
}
