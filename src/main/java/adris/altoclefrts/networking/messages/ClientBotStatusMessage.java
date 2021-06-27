package adris.altoclefrts.networking.messages;

import net.minecraft.util.math.Vec3d;

public class ClientBotStatusMessage extends ClientMessage {
    private final Vec3d position;
    private final Vec3d velocity;
    private final float pitch;
    private final float yaw;
    public final String[] commands;
    public final int health;
    public final int hunger;

    public ClientBotStatusMessage(Vec3d position, Vec3d velocity, float pitch, float yaw, String[] commands, int health, int hunger) {
        this.position = position;
        this.velocity = velocity;
        this.pitch = pitch;
        this.yaw = yaw;
        this.commands = commands;
        this.health = health;
        this.hunger = hunger;
    }
}
