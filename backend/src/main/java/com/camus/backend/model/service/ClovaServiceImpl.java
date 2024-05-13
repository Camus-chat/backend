package com.camus.backend.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.model.util.ClovaSetting;
import com.camus.backend.model.util.CompletionExecutor;
import com.camus.backend.model.util.CompletionRequest;

@Service
public class ClovaServiceImpl implements ClovaService {
	private final CompletionExecutor completionExecutor;

	public ClovaServiceImpl(ClovaSetting clovaSetting){
		// System.out.println("host: "+host);
		// System.out.println(apiKey);
		// System.out.println(apiKeyPrimaryVal);
		completionExecutor = new CompletionExecutor(clovaSetting.getHost(),
			clovaSetting.getApiKey(),
			clovaSetting.getApiKeyPrimaryVal());
		// System.out.println(clovaSetting.getHost());
	}

	@Override
	public List<String> analysis(List<CommonMessage> messages){
		CompletionRequest completionRequest = new CompletionRequest(messages);
		completionExecutor.token(completionRequest);
		completionExecutor.execute(completionRequest);
		return null;
	}

	// private void tokenCount(List<CommonMessage> messages) {
	// 	CompletionRequest completionRequest = new CompletionRequest(messages);
	// 	completionExecutor.token(completionRequest.getMessages());
	//
	// }


}
