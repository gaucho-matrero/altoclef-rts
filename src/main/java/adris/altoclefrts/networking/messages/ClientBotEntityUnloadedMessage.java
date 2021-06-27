package adris.altoclefrts.networking.messages;

public class ClientBotEntityUnloadedMessage extends ClientMessage {
    private int id;

    public ClientBotEntityUnloadedMessage(int id) {
        this.id = id;
    }
}
