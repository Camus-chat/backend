package com.camus.backend.statistic.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.statistic.domain.document.MemberStatistic;
import com.camus.backend.statistic.service.StatisticService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/statistic")
public class StatisticController {

	private final StatisticService service;

	@Autowired
	public StatisticController(StatisticService service){
		this.service = service;
	}

	// FeatureID 509-1
	@Operation(
		summary = "member별 서비스 사용량 통계",
		description = "회원가입한 Memeber의 ai 사용량, 활성 채널 수, 활성 채팅방 수, 채팅 사용량을 반환한다.")
	@GetMapping("/member")
	public ResponseEntity<MemberStatistic> getMemberStatistic(@RequestParam String id) {
		// CHECK : Optional 사용하는지 제크!
		Optional<MemberStatistic> statistic = service.find(id);

		return statistic.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// FeatureID 508-1
	// TODO : CHANNEL별 통계 정보 제공 api
	@Operation(
		summary = "channel별 서비스 사용량 통계",
		description = "시간대별 통계에 대한 api - 추후 논의 필요")
	@GetMapping("/channel")
	public void getChannelStatistic(@RequestParam String id) {
		// CHECK : 시간대별 통계를 제공할때, 어떻게 나눠서 GET을 만들어야할까요? 시간대를 입력받는 방식은 어떤가요?
	}


}
