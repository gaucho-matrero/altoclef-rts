package adris.altoclefrts.networking.messages;

import adris.altoclefrts.AltoClefRts;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

/**
 * Format is:
 *
 * byte array starting at 0
 */
public class ReplyChunkBlockDataMessage extends ReplyMessage {

    // Request
    public ChunkPos chunkPos;
    // Response
    public String[] blockTypeMap;
    public String data;

    public ReplyChunkBlockDataMessage() {super(false);} // Deserialization constructor
    public ReplyChunkBlockDataMessage(String[] blockTypeMap, String data) {
        super(false);
        this.blockTypeMap = blockTypeMap;
        this.data = data;
    }

    @Override
    protected void onReceiveRequest(Consumer<ReplyMessage> response) {
        //System.out.println("REQUESTED TO SEND CHUNK DATA");
        ChunkPos toReadChunk = chunkPos;

        String[] resultTypeMap;
        String resultData;

        // Read chunk blocks
        boolean loaded = AltoClefRts.getInstance().getAltoClef().getChunkTracker().isChunkLoaded(toReadChunk);
        if (loaded) {
            ClientWorld world = MinecraftClient.getInstance().world;
            List<String> translationCollection = new ArrayList<>();
            Map<String, Integer> translations = new HashMap<>();
            ByteBuffer resultCharArray = ByteBuffer.allocate(16*16*256);//new char[16*16*256];
            // Read x, y, z.
            for (int xx = toReadChunk.getStartX(); xx <= toReadChunk.getEndX(); ++xx) {
                for (int yy = 0; yy <= 255; ++yy) {
                    for (int zz = toReadChunk.getStartZ(); zz <= toReadChunk.getEndZ(); ++zz) {
                        BlockPos check = new BlockPos(xx, yy, zz);
                        BlockState state = world.getBlockState(check);
                        String name = state.getBlock().getTranslationKey();
                        if (!translations.containsKey(name)) {
                            int index = translationCollection.size();
                            translationCollection.add(name);
                            translations.put(name, index);
                        }
                        int localIndexInt = translations.get(name);
                        byte localIndex = (byte)localIndexInt;
                        resultCharArray.put(localIndex);
                        /*
                        if (yy == 64 && xx == toReadChunk.getStartX()) {
                            System.out.println("DEBUG Block Line: " + check.toShortString() + ": " + name + " -> " + localIndexInt);
                        }
                         */
                    }
                }
            }

            resultData = Base64.getEncoder().encodeToString(resultCharArray.array());
            resultCharArray.clear();
            resultTypeMap = new String[translationCollection.size()];
            translationCollection.toArray(resultTypeMap);
        } else {
            System.out.println("Server requested chunk out of our range, sending back failure");
            resultData = null;
            resultTypeMap = new String[0];
        }

        ReplyChunkBlockDataMessage result = new ReplyChunkBlockDataMessage(resultTypeMap, resultData);
        response.accept(result);
    }

    @Override
    protected void onReceiveResponse(ReplyMessage data) {
        throw new UnsupportedOperationException("Client will never request for chunk blocks.");
    }
}
