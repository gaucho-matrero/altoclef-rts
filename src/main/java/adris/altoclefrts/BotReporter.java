package adris.altoclefrts;

import adris.altoclef.AltoClef;
import adris.altoclef.Debug;
import adris.altoclef.tasksystem.Task;
import adris.altoclefrts.networking.MessageConnection;
import adris.altoclefrts.networking.messages.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Reports stuff to the server from the bot.
 *
 * Use this as a wrapper for almost all message sending.
 */
public class BotReporter {

    private final MessageConnection _connection;

    public BotReporter(MessageConnection connection) {
        _connection = connection;
    }

    private boolean isDisconnected() {
        return !_connection.hasConnection();
    }

    public void reportStatus(AltoClef altoclef) {
        if (isDisconnected()) return;
        ClientPlayerEntity player = altoclef.getPlayer();
        Vec3d vel = player.getVelocity();
        if (player.isOnGround()) {
            vel = vel.multiply(1, 0, 1);
        }
        int health = (int)player.getHealth();
        int hunger = player.getHungerManager().getFoodLevel();
        List<String> tasksList = altoclef.getUserTaskChain().getTasks().stream().map(task -> task != null? task.toString() : "(null)").collect(Collectors.toList());
        String[] tasks = new String[tasksList.size()];
        tasksList.toArray(tasks);
        _connection.sendMessage(new ClientBotStatusMessage(player.getPos(), vel, player.pitch, player.yaw, tasks, health, hunger));
    }

    public void reportWorldDisconnect() {
        if (isDisconnected()) return;
        _connection.sendMessage(new ClientWorldDisconnectMessage());
    }

    public void reportChunkLoad(ChunkPos pos) {
        if (isDisconnected()) return;
        _connection.sendMessage(new ClientBotChunkLoadedMessage(pos));
    }
    public void reportChunkUnload(ChunkPos pos) {
        if (isDisconnected()) return;
        _connection.sendMessage(new ClientBotChunkUnloadedMessage(pos));
    }

    public void testTimerMessage(float time, Consumer<String> onMessageReceived) {
        if (isDisconnected()) return;
        _connection.sendMessage(new TestTimerReplyMessage(time, onMessageReceived));
    }

    public void reportEntityLoad(Entity entity) {
        if (isDisconnected()) return;
        if (!shouldSendEntity(entity)) return;
        int id = getId(entity);
        String type = getType(entity);
        _connection.sendMessage(new ClientBotEntityLoadedMessage(id, type));
    }
    public void reportEntityUnload(Entity entity) {
        if (isDisconnected()) return;
        if (!shouldSendEntity(entity)) return;
        int id = getId(entity);
        _connection.sendMessage(new ClientBotEntityUnloadedMessage(id));
    }

    private boolean shouldSendEntity(Entity entity) {
        // Ignore our own player.
        if (entity.equals(MinecraftClient.getInstance().player)) return false;
        // Ignore arrows as they stick.
        if (entity instanceof ArrowEntity) return false;
        return true;
    }
    private int getId(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return -1;
        }
        return entity.getEntityId();
    }
    private String getType(Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            String name = player.getName().getString();
            return name;
        }
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) entity;
            return "ITEM:" + itemEntity.getStack().getItem().getTranslationKey();
        }
        return entity.getType().getTranslationKey();
    }
}
