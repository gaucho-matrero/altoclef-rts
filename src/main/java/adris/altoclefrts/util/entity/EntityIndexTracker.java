package adris.altoclefrts.util.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.util.HashMap;

public class EntityIndexTracker {

    private final HashMap<Integer, Entity> _entitiesById = new HashMap<>();

    private boolean _dirty = false;

    public void onPreTick() {
        _dirty = true;
    }

    public Entity getEntityById(int id) {
        ensureUpdated();
        if (_entitiesById.containsKey(id)) return _entitiesById.get(id);
        return null;
    }

    public boolean hasEntity(int id) {
        ensureUpdated();
        return _entitiesById.containsKey(id);
    }

    private void ensureUpdated() {
        if (_dirty) {
            load();
            _dirty = false;
        }
    }
    private void load() {
        _entitiesById.clear();
        if (MinecraftClient.getInstance().world != null) {
            for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                int id = entity.getEntityId();
                _entitiesById.put(id, entity);
            }
        }
    }
}
