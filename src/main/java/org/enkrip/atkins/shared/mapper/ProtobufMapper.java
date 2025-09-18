package org.enkrip.atkins.shared.mapper;

import org.enkrip.atkins.proto.ChatMessage;
import org.enkrip.atkins.proto.ChatUser;
import org.enkrip.atkins.proto.Conversation;
import org.enkrip.atkins.shared.websocket.dto.ChatMessageDto;
import org.enkrip.atkins.shared.websocket.dto.ChatUserDto;
import org.enkrip.atkins.shared.websocket.dto.ConversationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for converting between Protobuf objects and DTOs
 */
@Mapper
public interface ProtobufMapper {
    
    ProtobufMapper INSTANCE = Mappers.getMapper(ProtobufMapper.class);

    /**
     * Convert protobuf ChatUser to ChatUserDto
     */
    @Mapping(target = "displayName", source = "displayName", conditionExpression = "java(chatUser.hasDisplayName())")
    @Mapping(target = "avatarUrl", source = "avatarUrl", conditionExpression = "java(chatUser.hasAvatarUrl())")
    ChatUserDto toDto(ChatUser chatUser);

    /**
     * Convert protobuf ChatMessage to ChatMessageDto
     */
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "timestampToInstant")
    @Mapping(target = "messageType", expression = "java(chatMessage.getMessageType().name())")
    @Mapping(target = "conversationId", source = "conversationId", conditionExpression = "java(chatMessage.hasConversationId())")
    @Mapping(target = "myMessage")
    @Mapping(target = "read", ignore = true) // This needs to be set based on read status
    @Mapping(target = "version", ignore = true) // This needs to be set based on business logic
    ChatMessageDto toDto(ChatMessage chatMessage);

    /**
     * Convert protobuf Conversation to ConversationDto
     */
    @Mapping(target = "lastMessage", source = "lastMessage")
    @Mapping(target = "participants", source = "participantsList")
    @Mapping(target = "lastUpdatedTimestamp", source = "lastUpdatedTimestamp", qualifiedByName = "timestampToInstant")
    ConversationDto toDto(Conversation conversation);

    /**
     * Convert list of protobuf ChatUsers to list of ChatUserDtos
     */
    List<ChatUserDto> toDtoList(List<ChatUser> chatUsers);

    /**
     * Custom mapping method to convert Protobuf Timestamp to Java Instant
     */
    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
