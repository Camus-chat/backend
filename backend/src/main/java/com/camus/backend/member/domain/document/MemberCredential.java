package com.camus.backend.member.domain.document;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "member_credential")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCredential {
	@Id
	private UUID _id;
	private String username;
	private String password;
	private String role;
}