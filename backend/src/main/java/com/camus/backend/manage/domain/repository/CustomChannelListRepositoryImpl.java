package com.camus.backend.manage.domain.repository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;

@Repository
public class CustomChannelListRepositoryImpl implements CustomChannelListRepository {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public CustomChannelListRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void addChannelToMemberChannels(UUID _id, Channel channel) {
		// _id : member의 _id와 동일
		Query query = new Query(Criteria.where("_id").is(_id));
		Update update = new Update().push("channels", channel);
		mongoTemplate.updateFirst(query, update, ChannelList.class);
	}

	@Override
	public ChannelList getChannelListByMemberId(UUID _id) {
		System.out.println("test");
		Query query = new Query(Criteria.where("_id").is(_id));
		System.out.println("query : " + query.toString());
		ChannelList channels = mongoTemplate.findOne(query, ChannelList.class);
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);
		if (channelList == null) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
		return channels;
	}

	// CHECK : 특정 채널에 대한 값만 수정하기 위한 template
	@Override
	public void disableChannelByLink(String channelLink, UUID userUuid) {
		Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(userUuid),
			Criteria.where("channels.link").is(channelLink)));

		Update update = new Update().set("channels.$.isValid", false);

		mongoTemplate.updateFirst(query, update, ChannelList.class);
	}
}
