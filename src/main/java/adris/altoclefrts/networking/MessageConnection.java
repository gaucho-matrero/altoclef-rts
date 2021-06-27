package adris.altoclefrts.networking;

import adris.altoclef.Debug;
import adris.altoclef.util.serialization.*;
import adris.altoclefrts.networking.messages.Message;
import adris.altoclefrts.networking.messages.ReplyMessage;
import adris.altoclefrts.networking.messages.ServerMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MessageConnection {

    private final ServerConnection _connection;

    private final HashMap<Integer, ReplyMessage> _waitingMessages = new HashMap<>();
    private int _replyCounter = 0;
    private final Object _replyMutex = new Object();

    public MessageConnection(String serverAddress, int serverPort) {
        _connection = new ServerConnection(serverAddress, serverPort);
    }

    public void run(Runnable onDisconnect) {
        //_waitingMessages.clear();
        _connection.run(this::onRawMessageReceived, onDisconnect);
    }

    public boolean hasConnection() {
        return _connection.hasConnection();
    }

    public void sendMessage(Message message) {

        // Look out for replies if this is a reply message.
        if (message instanceof ReplyMessage && ((ReplyMessage)message).isAsking()) {
            ReplyMessage replyMessage = (ReplyMessage) message;
            synchronized (_replyMutex) {
                int replyId = _replyCounter++;
                replyMessage.setupReply(true, replyId);
                _waitingMessages.put(replyId, replyMessage);
            }
        }

        // TODO: Don't re-initialize these every time?
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BlockPos.class, new BlockPosSerializer());
        module.addSerializer(Vec3d.class, new Vec3dSerializer());
        module.addSerializer(ChunkPos.class, new ChunkPosSerializer());
        mapper.registerModule(module);

        try {
            String result = mapper.writer().writeValueAsString(message);
            _connection.sendMessageRaw(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void onRawMessageReceived(String rawMessage) {

        //Debug.logInternal("RECEIVED: " + rawMessage);

        // TODO: Don't re-initialize these every time?
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BlockPos.class, new BlockPosDeserializer());
        module.addDeserializer(Vec3d.class, new Vec3dDeserializer());
        module.addDeserializer(ChunkPos.class, new ChunkPosDeserializer());
        mapper.registerModule(module);

        try {
            Message message = mapper.readValue(rawMessage, Message.class);
            if (message instanceof ServerMessage) {
                ((ServerMessage) message).onReceive();
            } else if (message instanceof ReplyMessage) {
                ReplyMessage reply = (ReplyMessage) message;

                if (reply.isAsking()) {
                    // Information is requested.
                    reply.onReceiveRequestOuter((newReply) -> {
                        // Send back when we're done.
                        newReply.setupReply(false, reply.getReplyId());
                        sendMessage(newReply);
                    });
                } else {
                    // Information is received.
                    int id = reply.getReplyId();
                    synchronized (_replyMutex) {
                        ReplyMessage toReply = null;
                        if (_waitingMessages.containsKey(id)) {
                            toReply = _waitingMessages.get(id);
                            _waitingMessages.remove(id);
                        } else {
                            // Throw away the reply.
                            Debug.logError("Reply thrown away since it's not replying \"to\" anything (id=" + id + ", type=" + reply.getClass().getSimpleName() + ")");
                        }
                        if (toReply != null) {
                            toReply.onReceiveResponseOuter(reply);
                        }
                    }
                }
            } else {
                Debug.logWarning("Received non-server message from server???: " + message + " : " + message.getClass().getSimpleName());
            }
        } catch (JsonProcessingException e) {
            Debug.logError("Message Receive failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
