package adris.altoclefrts.networking;

import adris.altoclef.Debug;
import org.lwjgl.system.CallbackI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ServerConnection {

    private Socket _socket;

    public ServerConnection(String serverAddress, int serverPort) {
        try {
            _socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            _socket = null;
        }
    }

    public boolean hasConnection() {
        return _socket != null;
    }

    public void sendMessageRaw(String rawMessage) {
        if (!hasConnection()) {
            Debug.logError("NO MORE CONNECTION! Somehow it was reset??");
        }
        if (_socket == null) {
            return;
        }
        try {
            synchronized (_socket) {
                PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
                out.write(rawMessage.length() + " " + rawMessage);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(Consumer<String> onMessageReceivedRaw, Runnable onDisconnect) {
        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(_socket.getInputStream()));
                receiveForever(in, onMessageReceivedRaw, onDisconnect);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void receiveForever(BufferedReader in, Consumer<String> onMessageReceivedRaw, Runnable onDisconnect) {
        StringBuilder overflow = new StringBuilder();
        // This was copied from the C# project, it's pretty much identical.
        try {
            while (_socket != null && !_socket.isClosed()) {
                // First receive count
                StringBuilder countString = new StringBuilder(overflow.toString());
                overflow.setLength(0);
                boolean countDone = false;
                // We may already have the count.
                if (countString.length() != 0) {
                    String countSoFar = countString.toString();
                    int spaceFound = countSoFar.indexOf(' ');
                    if (spaceFound != -1) {
                        countDone = true;
                        String realCountString = countSoFar.substring(0, spaceFound);
                        String countStringRemainder = countSoFar.substring(spaceFound + 1);
                        countString.setLength(0);
                        countString.append(realCountString);
                        overflow.setLength(0);
                        overflow.append(countStringRemainder);
                    }
                }

                // Read count from network.
                while (!countDone) {
                    int has = _socket.getReceiveBufferSize();
                    char[] buffer = new char[has];
                    int size = in.read(buffer);
                    for( int i = 0; i < size; ++i)
                    {
                        char c = buffer[i];
                        if (countDone) {
                            overflow.append(c);
                        } else {
                            if (c == ' ') {
                                countDone = true;
                                continue;
                            }
                            countString.append(c);
                        }
                    }
                }

                int count = Integer.parseInt(countString.toString());

                // Now receive the data of size {count}.
                StringBuilder dataString = new StringBuilder(overflow.toString());
                overflow.setLength(0);
                int remainder = count - dataString.length();
                if (remainder > 0) {
                    // Receive however much data we need to fulfill our {count} size.
                    char[] buffer = new char[remainder];
                    in.read(buffer);
                    for (char c : buffer)
                    {
                        dataString.append(c);
                    }
                }

                // Overflow any remaining data string.
                String data = dataString.toString();
                if (data.length() > count) {
                    String overflowFromData = data.substring(count);
                    data = data.substring(0, count);
                    overflow.append(overflowFromData);
                }

                try {
                    onMessageReceivedRaw.accept(data);
                } catch (Exception e) {
                    Debug.logError("MESSAGE RECEIVE FAILED: " + e.getMessage());
                    Debug.logInternal("MESSAGE: " + data);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Finish (close, and send disconnect message)
            try {
                _socket.close();
            } catch (IOException e) {
                Debug.logWarning("CONNECTION FAILED! See stack");
                e.printStackTrace();
            } finally {
                Debug.logMessage("Connection closed.");
                onDisconnect.run();
                _socket = null;
            }
        }
    }
}
