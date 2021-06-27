package adris.altoclefrts.networking.messages;

import adris.altoclefrts.AltoClefRts;
import adris.altoclefrts.util.entity.EntityIndexTracker;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ReplyEntityDataMessage extends ReplyMessage {

    private int[] idsToGrab = new int[0];
    private EntityData[] data = new EntityData[0];

    public static class EntityData {
        public int entityId;
        public String world;
        public Vec3d position;
        public Vec3d velocity;
        public float yaw;
        public float pitch;
        public EntityData(Entity entity, String world) {
            entityId = entity.getEntityId();
            this.world = world;
            position = entity.getPos();
            velocity = entity.getVelocity();
            yaw = entity.yaw;
            pitch = entity.pitch;
        }
    }

    // Deserialization constructor
    public ReplyEntityDataMessage(Collection<EntityData> entityData) {
        super(false);
        data = new EntityData[entityData.size()];
        entityData.toArray(data);
    }
    public ReplyEntityDataMessage() {
        super(false);
    }

    @Override
    protected void onReceiveRequest(Consumer<ReplyMessage> response) {
        // TODO: Send reply message with entity data
        ArrayList<EntityData> datas = new ArrayList<>();
        EntityIndexTracker tracker = AltoClefRts.getInstance().getEntityIndexTracker();
        for (int toGrab : idsToGrab) {
            EntityData data = null;
            if (tracker.hasEntity(toGrab)) {
                data = new EntityData(tracker.getEntityById(toGrab), AltoClefRts.getInstance().getCurrentWorld());
            }
            datas.add(data);
        }
        ReplyEntityDataMessage result = new ReplyEntityDataMessage(datas);
        response.accept(result);
    }

    @Override
    protected void onReceiveResponse(ReplyMessage data) {
        throw new UnsupportedOperationException("Client never receives entity data");
    }
}
