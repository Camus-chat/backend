package com.camus.backend.member.domain.document;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "member_credential")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCredential {

	@Id
	private UUID _id;

	@Indexed
	private String userName;
	private String password;

}

