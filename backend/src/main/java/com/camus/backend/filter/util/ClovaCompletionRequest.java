package com.camus.backend.filter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.camus.backend.chat.domain.document.CommonMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClovaCompletionRequest {
	List<Map<String,String>> messages;
	double topP;
	int topK;
	int maxToken;
	double temperature;
	double repeatPenalty;
	List<String> stopBefore;
	boolean includeAiFilters;
	int seed;

	public ClovaCompletionRequest(List<CommonMessage> messageList){
		messages = new ArrayList<>();
		Map<String,String> messageMap = new HashMap<>();
		List<UUID> userList = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("이것은 각 유저가 채팅을 입력한 맥락이 악성인지 판단하는 분류기이다.\n")
			.append("유저의 각 채팅은 맥락에 따라 분류된다.\n")
			.append("분류는 (욕설/모욕/위협, 성희롱/혐오, 도배/홍보, 중립) 이다.\n")
			.append("각 분류에 대한 우선순위는 (욕설/모욕/위협 > 성희롱/혐오 > 도배/스팸 > 중립) 으로 중립이 가장 낮다.\n")
			.append("분류기에 의하여 각 채팅에 대한 분류를 출력한다.\n");

		sb.append("\"");
		for (int i=0; i<messageList.size(); i++){
			if (!userList.contains(messageList.get(i).getSenderId())){
				userList.add(messageList.get(i).getSenderId());
			}

			sb.append("유저").append(userList.indexOf(messageList.get(i).getSenderId()))
				.append(": ").append(messageList.get(i).getContent());

			if (i==messageList.size()-1) sb.append('\"');
			else sb.append('\n');
		}
		messageMap.put("role", "user");
		messageMap.put("content", sb.toString());


		messages.add(messageMap);

		topP = 0.8;
		topK = 0;
		maxToken = 100;
		temperature = 0.5;
		repeatPenalty = 5.0;
		stopBefore = new ArrayList<>();
		includeAiFilters = true;
		seed = 0;
	}

}
