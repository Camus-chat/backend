package com.camus.backend.manage.domain.dto;

import com.camus.backend.chat.domain.dto.RedisSavedCommonMessageDto;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import com.camus.backend.chat.domain.dto.RedisSavedNoticeMessageDto;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageInfo {
    private String _class;
    private UUID userId;
    private String content;


    public LastMessageInfo(RedisSavedMessageBasicDto messageInfo) {
            this._class = messageInfo.get_class();
            this.content = messageInfo.getContent();

            if (messageInfo instanceof RedisSavedNoticeMessageDto noticeMessage) {
                this.userId = noticeMessage.getTarget();
            } else if(messageInfo instanceof RedisSavedCommonMessageDto commonMessage) {
                this.userId = commonMessage.getSenderId();
            }else {
                throw new IllegalArgumentException("Invalid message type");
            }
        }

}
