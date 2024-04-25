package com.camus.backend.statistic.domain.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.camus.backend.statistic.domain.document.MemberStatistic;

public interface StatisticRepository extends MongoRepository<MemberStatistic, String> {

	@Query("{ 'member_id': ?0 }")
	Optional<MemberStatistic> findByMemberIdField(String id);

	// TODO : ChannelStatistic -> api 따라서 시간대확인하고 요청 or key값으로 전체 요청 중 고르기
	// TODO : key와 time은 복합 인덱스 이고 key가 우선이므로 key로는 탐색 가능
// Optional<ChannelStatistic> find~~

}

