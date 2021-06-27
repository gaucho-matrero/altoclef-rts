package adris.altoclefrts.networking.messages;

import adris.altoclefrts.AltoClefRts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerInfo;

public class ServerConnectMessage extends ServerMessage {

    private String address;

    @Override
    public void onReceive() {
        AltoClefRts.getInstance().getExecutor().execute(() -> {
            ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
            if (serverInfo == null || serverInfo.address.equals(address)) {
                ServerInfo toConnectTo = new ServerInfo("ALTO_CLEF_TARGET_CONNECTION", address, false);
                MinecraftClient.getInstance().openScreen(new ConnectScreen(null, MinecraftClient.getInstance(), toConnectTo));
            }
        });
    }
}
