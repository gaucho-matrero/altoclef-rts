package adris.altoclefrts.networking.messages;

public abstract class ServerMessage extends Message{
    public abstract void onReceive();
}
