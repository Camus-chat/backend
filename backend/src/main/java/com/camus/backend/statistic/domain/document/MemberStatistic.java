package com.camus.backend.statistic.domain.document;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

@Document(collection = "member_stat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatistic {

	@Id
	private  String _id;

	private int singleAiUsage;
	private int contextAiUsage;
	private int validChannelCnt;
	private int validRoomCnt;
	private int chatUsage;
}
