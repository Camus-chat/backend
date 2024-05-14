package com.camus.backend.statistic.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.camus.backend.statistic.domain.document.MemberStatistic;
import com.camus.backend.statistic.domain.repository.StatisticRepository;

@Service
public class StatisticService {

	private final StatisticRepository repository;

	public StatisticService(StatisticRepository repository) {
		this.repository = repository;
	}

	;

	// CHECK : Optional을 백엔드에서 사용하나요?
	public Optional<MemberStatistic> find(String id) {
		return repository.findByMemberIdField(id);
	}

}
