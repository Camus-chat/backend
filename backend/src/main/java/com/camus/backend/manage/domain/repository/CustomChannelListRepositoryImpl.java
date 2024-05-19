package com.camus.backend.manage.domain.repository;

import static com.camus.backend.global.util.DBOperationCheckUtil.*;

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
import com.camus.backend.manage.domain.dto.ChannelInfoDto;
import com.mongodb.client.result.UpdateResult;

@Repository
public class CustomChannelListRepositoryImpl implements CustomChannelListRepository {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public CustomChannelListRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void addChannelToMemberChannels(UUID memberId, Channel channel) {
		Query query = new Query(Criteria.where("_id").is(memberId));
		Update update = new Update().push("channels", channel);
		UpdateResult result = mongoTemplate.updateFirst(query, update, ChannelList.class);

		checkDBOperation(result);

		System.out.println(channel.toString());
		if (result.getMatchedCount() == 0) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
	}

	@Override
	public ChannelList getChannelListByMemberId(UUID memberId) {
		Query query = new Query(Criteria.where("_id").is(memberId));
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);

		if (channelList == null) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
		return channelList;
	}

	@Override
	public void disableChannelByLink(UUID link, UUID memberId) {
		Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(memberId),
			Criteria.where("channels.link").is(link)));

		Update update = new Update().set("channels.$.isValid", false);

		UpdateResult result = mongoTemplate.updateFirst(query, update, ChannelList.class);

		checkDBOperation(result);

		if (result.getMatchedCount() == 0) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}
	}

	@Override
	public void editChannelInfo(UUID memberId, ChannelInfoDto channelInfoDto) {
		Query query = new Query(new Criteria().andOperator(Criteria.where("_id").is(memberId),
			Criteria.where("channels.link").is(channelInfoDto.getLink())));

		Update update = new Update().set("channels.$.title", channelInfoDto.getTitle()).set("channels.$.content",
			channelInfoDto.getContent()).set("channels.$.filterLevel", channelInfoDto.getFilterLevel());

		UpdateResult result = mongoTemplate.updateFirst(query, update, ChannelList.class);

		checkDBOperation(result);

		if (result.getMatchedCount() == 0) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}
	}

	public void addRoomIdToChannel(UUID roomId, UUID channelKey) {
		Query query = new Query(Criteria.where("channels._id").is(channelKey));
		Update update = new Update().push("channels.$.chatRoomList", roomId);
		UpdateResult result = mongoTemplate.updateFirst(query, update, ChannelList.class);

		checkDBOperation(result);

		if (result.getMatchedCount() == 0 && result.getUpsertedId() == null) {
			System.out.println("여기임?");
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}
	}

	public UUID getChannelKeyByChannelLink(UUID channelLink) {
		Query query = new Query(Criteria.where("channels.link").is(channelLink));
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);
		if (channelList == null) {
			System.out.println("여기임?");
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}

		return channelList.getChannels().stream().filter(ch -> ch.getLink().equals(channelLink))
			.findFirst().get().getKey();
	}

	public ChannelList getChannelListByChannelLink(UUID channelLink) {
		Query query = new Query(Criteria.where("channels.link").is(channelLink));

		return mongoTemplate.findOne(query, ChannelList.class);
	}

}
