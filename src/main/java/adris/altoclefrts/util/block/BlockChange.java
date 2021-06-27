package adris.altoclefrts.util.block;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

@JsonSerialize
public class BlockChange {
    private BlockPos blockPos;
    private String result;

    // Deserialize constructor
    BlockChange() { }
    BlockChange(BlockPos blockPos, Block result) {
        this.blockPos = blockPos;
        this.result = result.getTranslationKey();
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
