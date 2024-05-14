package com.camus.backend.manage.domain.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.camus.backend.manage.domain.document.ChannelList;

public interface ChannelListRepository extends MongoRepository<ChannelList, UUID>, CustomChannelListRepository {

}
