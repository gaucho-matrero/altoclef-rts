package adris.altoclefrts.networking.messages;

import net.minecraft.util.math.ChunkPos;

public class ClientBotChunkLoadedMessage extends ClientMessage {
    private ChunkPos chunk;

    public ClientBotChunkLoadedMessage(ChunkPos chunk) {
        this.chunk = chunk;
    }
}
