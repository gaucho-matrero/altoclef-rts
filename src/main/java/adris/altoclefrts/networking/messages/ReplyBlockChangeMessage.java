package adris.altoclefrts.networking.messages;

import adris.altoclef.Debug;
import adris.altoclefrts.AltoClefRts;
import adris.altoclefrts.util.block.BlockChange;

import java.util.Collection;
import java.util.function.Consumer;

public class ReplyBlockChangeMessage extends ReplyMessage {

    private String world;
    private BlockChange[] blockChanges = new BlockChange[0];

    // Deserialization Constructor
    public ReplyBlockChangeMessage() {
        super(false);
    }
    public ReplyBlockChangeMessage(String world, BlockChange[] blockChanges) {
        super(false);
        this.world = world;
        this.blockChanges = blockChanges;
    }

    @Override
    protected void onReceiveRequest(Consumer<ReplyMessage> response) {
        Collection<BlockChange> changes = AltoClefRts.getInstance().getBlockChangeTracker().getChanges(world);
        BlockChange[] results = new BlockChange[changes.size()];
        changes.toArray(results);
        //Debug.logInternal("SENT CHANGES: " + results.length);// + Util.arrayToString(results, blockChange -> blockChange.getBlockPos().toShortString()));
        response.accept(new ReplyBlockChangeMessage(world, results));
        AltoClefRts.getInstance().getBlockChangeTracker().clear(world);
    }

    @Override
    protected void onReceiveResponse(ReplyMessage data) {
        throw new UnsupportedOperationException("Client RECEIVES only!");
    }
}
