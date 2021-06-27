package adris.altoclefrts.networking.messages;

import net.minecraft.util.math.ChunkPos;

public class ClientBotEntityLoadedMessage extends ClientMessage {
    private int id;
    private String type;

    public ClientBotEntityLoadedMessage(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
