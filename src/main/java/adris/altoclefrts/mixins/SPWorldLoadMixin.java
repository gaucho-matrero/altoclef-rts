package adris.altoclefrts.mixins;

import adris.altoclefrts.AltoClefHookup;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(LevelStorage.class)
public class SPWorldLoadMixin {

    @Inject(
            method = "createSession",
            at = @At("HEAD")
    )
    public void createSession(String directoryName, CallbackInfoReturnable<LevelStorage.Session> ci) throws IOException {
        AltoClefHookup.setCurrentLevelName(directoryName);
    }
}
