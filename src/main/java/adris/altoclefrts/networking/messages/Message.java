package adris.altoclefrts.networking.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "TYPE")
@JsonSubTypes({
        @Type(value = TestTimerReplyMessage.class, name="TestTimerReplyMessage"),
        @Type(value = ClientHelloMessage.class, name="ClientHelloMessage"),
        @Type(value = ClientBotStatusMessage.class, name="ClientBotStatusMessage"),
        @Type(value = ClientBotChunkLoadedMessage.class, name="ClientBotChunkLoadedMessage"),
        @Type(value = ClientBotChunkUnloadedMessage.class, name="ClientBotChunkUnloadedMessage"),
        @Type(value = ClientBotEntityLoadedMessage.class, name="ClientBotEntityLoadedMessage"),
        @Type(value = ClientBotEntityUnloadedMessage.class, name="ClientBotEntityUnloadedMessage"),
        @Type(value = ServerConnectMessage.class, name="ServerConnectMessage"),
        @Type(value = ServerCommandMessage.class, name="ServerCommandMessage"),
        @Type(value = ReplyChunkBlockDataMessage.class, name="ReplyChunkBlockDataMessage"),
        @Type(value = ReplyBlockChangeMessage.class, name="ReplyBlockChangeMessage"),
        @Type(value = ReplyEntityDataMessage.class, name="ReplyEntityDataMessage")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class Message {

}
