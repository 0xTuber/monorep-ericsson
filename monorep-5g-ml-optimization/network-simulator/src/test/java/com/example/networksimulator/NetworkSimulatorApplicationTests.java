package com.example.networksimulator;

import com.example.controller.SimulationController;
import com.example.service.SessionManager;
import com.example.model.NetworkMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(SimulationController.class)
class SimulationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SessionManager sessionManager;

	@Test
	void getSessionMetricsEndpointReturnsMetrics() throws Exception {
		String sessionId = "test-session-id";
		NetworkMetrics metrics = new NetworkMetrics();
		metrics.setLatency(30.0);
		metrics.setThroughput(400.0);
		metrics.setPacketLoss(0.05);

		when(sessionManager.getSessionMetrics(sessionId)).thenReturn(metrics);

		mockMvc.perform(get("/simulation/metrics/{sessionId}", sessionId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latency").value(30.0))
				.andExpect(jsonPath("$.throughput").value(400.0))
				.andExpect(jsonPath("$.packetLoss").value(0.05));
	}

	@Test
	void updateSessionResourcesEndpoint() throws Exception {
		String sessionId = "test-session-id";
		String requestBody = """
            {
                "allocatedBandwidth": 15.0,
                "allocatedPRBs": 20.0
            }
            """;

		doNothing().when(sessionManager).updateSessionResources(sessionId, 15.0, 20.0);

		mockMvc.perform(post("/simulation/update/{sessionId}", sessionId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Resources updated for session: " + sessionId));
	}

	@Test
	void stopSessionEndpoint() throws Exception {
		String sessionId = "test-session-id";
		doNothing().when(sessionManager).stopSession(sessionId);
		mockMvc.perform(delete("/simulation/stop/{sessionId}", sessionId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Session stopped: " + sessionId));
	}
}
