package com.camus.backend.model.service;

import java.util.List;

import com.camus.backend.chat.domain.document.CommonMessage;

public interface ClovaService {
	public void analysis(List<CommonMessage> messages);
}
