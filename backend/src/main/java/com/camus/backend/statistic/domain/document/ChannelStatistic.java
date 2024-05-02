package com.camus.backend.statistic.domain.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

@Document(collection = "channel_stat")
// CHECK :  복합 인덱스 설정 -> key와 time으로 탐색할 예정이라면... 필요하지 않나?
@CompoundIndex(name = "key_time_idx", def = "{'key' : 1, 'time' : 1}", unique = true)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelStatistic {

	// CHECK : _id 필드를 어떻게 할 것인가?
	@Id
	private String _id;

	private String key;
	private Date time;

	private long messageCount;
	private Map<String, Long> filteredCount;
	private ArrayList<String> userList;
	private Map<String, Long> sentimentCount;
}
