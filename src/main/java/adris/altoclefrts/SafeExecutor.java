package adris.altoclefrts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SafeExecutor {
    private final Queue<Runnable> _executeQueue = new ArrayDeque<>();

    public void execute(Runnable action) {
        synchronized (_executeQueue) {
            _executeQueue.add(action);
        }
    }

    public void onSafeTick() {
        Queue<Runnable> toRun = new ArrayDeque<>();
        synchronized (_executeQueue) {
            while (!_executeQueue.isEmpty()) {
                toRun.add(_executeQueue.poll());
            }
        }
        while (!toRun.isEmpty()) {
            toRun.poll().run();
        }
    }
}
