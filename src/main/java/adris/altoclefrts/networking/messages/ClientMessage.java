package adris.altoclefrts.networking.messages;

import adris.altoclefrts.AltoClefRts;
import net.minecraft.client.MinecraftClient;

// Not much to add here ngl
public class ClientMessage extends Message {

    private String username;
    private String world = "";

    public ClientMessage() {
        username = MinecraftClient.getInstance().getSession().getUsername();
        world = AltoClefRts.getInstance().getCurrentWorld();
    }
}
