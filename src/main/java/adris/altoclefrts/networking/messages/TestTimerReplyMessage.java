package adris.altoclefrts.networking.messages;

import adris.altoclef.Debug;

import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TestTimerReplyMessage extends ReplyMessage {

    public float timeToWait;
    public String message;
    private Consumer<String> _onMessageReceived;

    public TestTimerReplyMessage() {super(false);} // Deserialization constructor
    public TestTimerReplyMessage(String message) {
        super(false);
        this.message = message;
    }

    public TestTimerReplyMessage(float timeToWait, Consumer<String> onMessageReceived) {
        super(true);
        this.timeToWait = timeToWait;
        _onMessageReceived = onMessageReceived;
    }

    @Override
    protected void onReceiveRequest(Consumer<ReplyMessage> reply) {
        Debug.logMessage("Timeout PRE");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep((int)(timeToWait * 1000));
                Debug.logMessage("(After timeout, message Sent!)");
                reply.accept(new TestTimerReplyMessage("Hello from Client after " + timeToWait + " seconds!"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onReceiveResponse(ReplyMessage o) {
        _onMessageReceived.accept(((TestTimerReplyMessage)o).message);
    }
}
