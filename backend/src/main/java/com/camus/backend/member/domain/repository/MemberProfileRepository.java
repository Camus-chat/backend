package com.camus.backend.member.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.camus.backend.member.domain.document.MemberProfile.MemberProfile;

public interface MemberProfileRepository extends MongoRepository<MemberProfile, UUID> {

	default <T> Optional<T> findProfileById(UUID _id, Class<T> tClass) {
		Optional<MemberProfile> memberProfile = findById(_id);
		return memberProfile.flatMap(value -> {
			if (tClass.isInstance(value)) {
				return Optional.of(tClass.cast(value));
			} else {
				return Optional.empty();

			}
		});
	}



}
