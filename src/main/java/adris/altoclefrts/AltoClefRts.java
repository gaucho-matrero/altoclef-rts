package adris.altoclefrts;

import adris.altoclef.AltoClef;
import adris.altoclef.Debug;
import adris.altoclef.commandsystem.CommandException;
import adris.altoclef.util.Dimension;
import adris.altoclef.util.csharpisbetter.TimerGame;
import adris.altoclefrts.util.block.BlockChangeTracker;
import adris.altoclefrts.networking.MessageConnection;
import adris.altoclefrts.networking.messages.ClientHelloMessage;
import adris.altoclefrts.util.entity.EntityIndexTracker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class AltoClefRts implements ModInitializer {

    // Singleton antipattern
    private static AltoClefRts _instance;
    public static AltoClefRts getInstance() {return _instance;}

    private AltoClef _altoClef;
    private MessageConnection _connection;

    private final TimerGame _statusSendTimer = new TimerGame(0.25);
    private BotReporter _reporter;
    private final BlockChangeTracker _blockChangeTracker = new BlockChangeTracker();
    private final EntityIndexTracker _entityIndexTracker = new EntityIndexTracker();
    private final SafeExecutor _safeExecutor = new SafeExecutor();

    private boolean _wasInGame;

    public AltoClef getAltoClef() {
        return _altoClef;
    }
    public BlockChangeTracker getBlockChangeTracker() {
        return _blockChangeTracker;
    }
    public EntityIndexTracker getEntityIndexTracker() {
        return _entityIndexTracker;
    }

    public SafeExecutor getExecutor() {
        return _safeExecutor;
    }

    public String getCurrentWorld() {
        Dimension dim = AltoClefRts.getInstance().getAltoClef().getCurrentDimension();
        ServerInfo current = MinecraftClient.getInstance().getCurrentServerEntry();
        String world = "";
        if (AltoClef.inGame()) {
            if (current == null || current.isLocal()) {
                // Local worlds, use the world directory
                world = "SINGLEPLAYER:/" + AltoClefHookup.getCurrentLevelName();
            } else {
                // Servers, use the IP
                world = current.address;
            }
            world += ":" + dim;
        }
        return world;
    }

    @Override
    public void onInitialize() {
        _instance = this;

        AltoClefHookup.hookupWithAltoClef(this::onAltoClefInit, this::onAltoClefTick);
    }

    private void onAltoClefInit(AltoClef altoclef) {
        _altoClef = altoclef;
        _connection = new MessageConnection("localhost", 9080);
        _reporter = new BotReporter(_connection);

        if (_connection.hasConnection()) {
            _connection.run(() -> {
                Debug.logWarning("DISCONNECTED. TODO: Try reconnecting every so often.");
            });
            // Establish our identity.
            _connection.sendMessage(new ClientHelloMessage());
        } else {
            Debug.logMessage("NO CONNECTION DETECTED: TODO: Try reconnecting every so often.");
        }
    }
    private void onAltoClefTick(AltoClef altoclef) {
        _entityIndexTracker.onPreTick();
        // Send status frequently but not at tick speed (that's a bit unnecessary)
        if (AltoClef.inGame() && _statusSendTimer.elapsed()) {
            _statusSendTimer.reset();
            _reporter.reportStatus(altoclef);
        }

        // On world/server disconnect
        if (!AltoClef.inGame() && _wasInGame) {
            Debug.logInternal("Left World.");
            _reporter.reportWorldDisconnect();
            // Be quicker on reload.
            _statusSendTimer.forceElapse();
        }

        _safeExecutor.onSafeTick();

        _wasInGame = AltoClef.inGame();
    }

    public void onChunkLoad(ChunkPos pos) {
        _reporter.reportChunkLoad(pos);
    }
    public void onChunkUnload(ChunkPos pos) {
        if (_altoClef.getChunkTracker().isChunkLoaded(pos)) {
            _reporter.reportChunkUnload(pos);
        }
    }
    public void onEntityLoad(Entity entity) {
        _reporter.reportEntityLoad(entity);
    }
    public void onEntityUnload(Entity entity) {
        _reporter.reportEntityUnload(entity);
    }

    public void onBlockChange(BlockPos pos, Block prevBlock, Block newBlock) {
        getBlockChangeTracker().trackChange(getCurrentWorld(), pos, prevBlock, newBlock);
    }

    public void executeCommand(String command) {
        command = "@" + command;
        try {
            _altoClef.getCommandExecutor().Execute(command);
        } catch (CommandException e) {
            String failure = e.getMessage();
        }
    }

    public void onClientChat(String message) {
        if (message.equals("test")) {
            Debug.logMessage("Sending test reply thing.");
            _reporter.testTimerMessage(5, reply -> {
                Debug.logMessage("RECEIVED Server Reply:" + reply);
            });
        }
    }
}
