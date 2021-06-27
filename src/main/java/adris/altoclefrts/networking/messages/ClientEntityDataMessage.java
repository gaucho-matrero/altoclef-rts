package adris.altoclefrts.networking.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.*;

@Deprecated
public class ClientEntityDataMessage extends ClientMessage {

    // TODO: This should work like chunk data, requested by server.

    private String[] entityTypes;
    private EntityData[] data;

    public static class EntityData {
        public int entityId;
        public Vec3d position;
        public Vec3d velocity;
        public float yaw;
        public float pitch;

        private EntityData(Entity entity) {
            this.entityId = entity.getEntityId();
            this.position = entity.getPos();
            this.velocity = entity.getVelocity();
            this.yaw = entity.yaw;
            this.pitch = entity.pitch;
        }
    }

    public ClientEntityDataMessage() {
        ArrayList<EntityData> resultData = new ArrayList<>(MinecraftClient.getInstance().world.getRegularEntityCount());
        ArrayList<String> resultEntityTypes = new ArrayList<>();
        Map<String, Integer> entityTypesFound = new HashMap<>();
        for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            String type = entity.getType().getTranslationKey();
            if (!entityTypesFound.containsKey(type)) {
                entityTypesFound.put(type, entityTypesFound.size());
                resultEntityTypes.add(type);
            }
            int index = entityTypesFound.get(type);
            resultData.add(new EntityData(entity));
        }

        entityTypes = new String[resultEntityTypes.size()];
        resultEntityTypes.toArray(entityTypes);
        data = new EntityData[resultData.size()];
        resultData.toArray(data);
    }
}
