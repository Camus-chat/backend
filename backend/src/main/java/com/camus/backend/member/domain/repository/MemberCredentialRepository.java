package com.camus.backend.member.domain.repository;
import com.camus.backend.member.domain.document.MemberCredential;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MemberCredentialRepository extends MongoRepository<MemberCredential, String> {
	// 필요한 쿼리 메소드를 여기에 정의할 수 있습니다.
}

