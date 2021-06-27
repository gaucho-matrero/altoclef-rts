package adris.altoclefrts.mixins;

import adris.altoclef.StaticMixinHookups;
import adris.altoclefrts.AltoClefRts;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientChunkManager.class)
public class LoadChunkMixin {

    @Inject(
            method = "loadChunkFromPacket",
            at = @At("RETURN")
    )
    private void onLoadChunk(int x, int z, @Nullable BiomeArray biomes, PacketByteBuf buf, CompoundTag tag, int verticalStripBitmask, boolean complete, CallbackInfoReturnable<WorldChunk> ci) {
        AltoClefRts.getInstance().onChunkLoad(new ChunkPos(x, z));
    }

    @Inject(
            method = "unload",
            at = @At("HEAD")
    )
    private void onChunkUnload(int x, int z, CallbackInfo ci) {
        AltoClefRts.getInstance().onChunkUnload(new ChunkPos(x, z));
    }
}
