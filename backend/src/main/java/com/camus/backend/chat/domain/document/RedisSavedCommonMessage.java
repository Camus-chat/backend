package com.camus.backend.chat.domain.document;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisSavedCommonMessage extends RedisSavedMessageBasic {
	private String filteredType;
	private UUID senderId;

	public RedisSavedCommonMessage() {
		super();
	}

	public RedisSavedCommonMessage(CommonMessage commonMessage) {
		super(commonMessage);
		this.filteredType = commonMessage.getFilteredType();
		this.senderId = commonMessage.getSenderId();
	}

	@Override
	public String toString() {
		return super.toString() + "filteredType : " + filteredType + "\n";
	}
}
