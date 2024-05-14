package com.camus.backend.model.service;

import java.util.List;

import com.camus.backend.chat.domain.document.CommonMessage;

public interface ModelService {
	public void predict(List<CommonMessage> messages);
	public void predict(CommonMessage message);
}
