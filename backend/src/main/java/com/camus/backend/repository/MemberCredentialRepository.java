package com.camus.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.camus.backend.document.MemberCredential;

public interface MemberCredentialRepository extends MongoRepository<MemberCredential, String> {
	Boolean existsByUsername(String username);
	MemberCredential findByUsername(String username);
}
