package adris.altoclefrts.networking.messages;

import adris.altoclefrts.AltoClefRts;

public class ServerCommandMessage extends ServerMessage{

    private String command;
    @Override
    public void onReceive() {
        AltoClefRts.getInstance().getExecutor().execute(()
                -> AltoClefRts.getInstance().executeCommand(command));
    }
}
