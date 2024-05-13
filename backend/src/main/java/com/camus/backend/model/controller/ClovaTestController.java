package com.camus.backend.model.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.model.service.ClovaService;

@RestController
@RequestMapping("clova")
public class ClovaTestController {

	private final ClovaService clovaService;
	public ClovaTestController(ClovaService clovaService){
		this.clovaService = clovaService;
	}
	@GetMapping("test")
	public void test(){
		List<CommonMessage> messages = new ArrayList<>();
		CommonMessage message1 = CommonMessage.builder()
				.senderId(UUID.randomUUID())
				.content("내용1").build();
		messages.add(message1);

		CommonMessage message2 = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("욕설1").build();
		messages.add(message2);

		clovaService.analysis(messages);
	}
}
