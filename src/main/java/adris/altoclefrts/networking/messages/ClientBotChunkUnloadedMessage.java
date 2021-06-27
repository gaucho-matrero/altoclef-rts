package adris.altoclefrts.networking.messages;

import net.minecraft.util.math.ChunkPos;

public class ClientBotChunkUnloadedMessage extends ClientMessage {
    private ChunkPos chunk;

    public ClientBotChunkUnloadedMessage(ChunkPos chunk) {
        this.chunk = chunk;
    }
}
