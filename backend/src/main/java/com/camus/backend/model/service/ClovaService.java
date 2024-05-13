package com.camus.backend.model.service;

import java.util.List;

import com.camus.backend.chat.domain.document.CommonMessage;

public interface ClovaService {
	public List<String> analysis(List<CommonMessage> messages);
}
