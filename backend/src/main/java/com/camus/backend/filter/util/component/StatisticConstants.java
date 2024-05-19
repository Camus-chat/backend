package com.camus.backend.filter.util.component;

import org.springframework.stereotype.Component;

@Component
public class StatisticConstants {
	public String LAMBDA_COUNT_ROOM(String roomId) { return "lambda:room:" + roomId + ":count"; }
	public String CLOVA_TOKEN_ROOM(String roomId) { return "clova:room:" + roomId + ":token"; }
}
