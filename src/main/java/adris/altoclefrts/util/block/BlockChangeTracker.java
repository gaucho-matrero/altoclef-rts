package adris.altoclefrts.util.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BlockChangeTracker {

    private final Map<String, Queue<BlockChange>> _blockChanges = new HashMap<>();

    public void trackChange(String world, BlockPos block, Block previous, Block result) {
        synchronized (_blockChanges) {
            if (!_blockChanges.containsKey(world)) {
                _blockChanges.put(world, new ArrayDeque<>());
            }
            //Debug.logInternal("CHANGE: " + previous.getTranslationKey() + " -> " + result.getTranslationKey());
            _blockChanges.get(world).add(new BlockChange(block, result));
        }
    }

    public Collection<BlockChange> getChanges(String world) {
        synchronized (_blockChanges) {
            if (_blockChanges.containsKey(world)) {
                return _blockChanges.get(world);
            }
            return Collections.emptyList();
        }
    }
    public void clear(String world) {
        synchronized (_blockChanges) {
            if (_blockChanges.containsKey(world)) {
                _blockChanges.get(world).clear();
                _blockChanges.remove(world);
            }
        }
    }

    /*
     * Server:
     *      (every 1/4th) of a second, ask one chunk client for block break data.
     *
     * Client:
     *      every block change, store that change in a queue.
     *      After sending the queue to the server, clear it.
     */
}
