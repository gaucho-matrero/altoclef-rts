package adris.altoclefrts.networking.messages;

import java.util.function.Consumer;

/**
 * A special kind of message that asks for and receives a "reply".
 *
 * You can think about it like a promise, over the network.
 *
 * Redesigned from scratch, and worse. Because I'm a good programmer.
 */
public abstract class ReplyMessage extends Message {

    private int replyId;
    private boolean asking = false;

    public ReplyMessage(boolean asking) {
        this.asking = asking;
    }

    public void setupReply(boolean asking, int replyId) {
        this.asking = asking;
        this.replyId = replyId;
    }

    public void onReceiveRequestOuter(Consumer<ReplyMessage> onReply) {
        Consumer<ReplyMessage> onLowerReply = (data) -> {
            onReply.accept(data);
        };
        onReceiveRequest(onLowerReply);
    }

    public boolean isAsking() {
        return asking;
    }
    public int getReplyId() {
        return replyId;
    }

    public void onReceiveResponseOuter(ReplyMessage data) {
        onReceiveResponse(data);
    }

    protected abstract void onReceiveRequest(Consumer<ReplyMessage> response);

    protected abstract void onReceiveResponse(ReplyMessage data);
}
