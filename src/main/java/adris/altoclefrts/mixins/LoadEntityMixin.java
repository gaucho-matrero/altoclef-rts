package adris.altoclefrts.mixins;

import adris.altoclefrts.AltoClefRts;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class LoadEntityMixin {

    @Inject(
            method="addEntityPrivate",
            at = @At("HEAD")
    )
    private void onAddEntity(int id, Entity entity, CallbackInfo ci) {
        AltoClefRts.getInstance().onEntityLoad(entity);
    }

    @Inject(
            method="finishRemovingEntity",
            at = @At("HEAD")
    )
    private void onRemoveEntity(Entity entity, CallbackInfo ci) {
        AltoClefRts.getInstance().onEntityUnload(entity);
    }
}
