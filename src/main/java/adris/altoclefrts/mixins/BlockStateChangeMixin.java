package adris.altoclefrts.mixins;

import adris.altoclefrts.AltoClefRts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class BlockStateChangeMixin {

    @Inject(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("HEAD")
    )
    private void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> ci) {
        World world = MinecraftClient.getInstance().world;
        Block prevBlock = world.getBlockState(pos).getBlock();
        Block newBlock = state.getBlock();
        if (!prevBlock.equals(newBlock)) {
            AltoClefRts.getInstance().onBlockChange(pos, prevBlock, newBlock);
        }
    }
}
