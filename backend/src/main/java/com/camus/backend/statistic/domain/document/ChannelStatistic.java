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

@Document(collection = "ChannelStat")
@CompoundIndex(name = "key_time_idx", def = "{'key' : 1, 'time' : 1}", unique = true)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelStatistic {

	@Id
	private String _id;

	private String key;
	private Date time;

	private long messageCount;
	private Map<String, Long> filteredCount;
	private ArrayList<String> userList;
	private Map<String, Long> sentimentCount;
}
