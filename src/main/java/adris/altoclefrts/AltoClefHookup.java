package adris.altoclefrts;

import adris.altoclef.AltoClef;
import adris.altoclef.util.csharpisbetter.ActionListener;

import java.util.function.Consumer;

/**
 * Connects with altoclef
 */
public class AltoClefHookup {

    // Static functions/mixin hookups
    private static String _currentLevelName = null;
    public static void setCurrentLevelName(String name) {
        _currentLevelName = name;
    }
    public static String getCurrentLevelName() {
        return _currentLevelName;
    }

    public static void hookupWithAltoClef(Consumer<AltoClef> onInit, Consumer<AltoClef> onTick) {
        AltoClef.onInitialize.addListener(new ActionListener<AltoClef>() {
            @Override
            public void invoke(AltoClef altoClef) {
                onInit.accept(altoClef);
            }
        });
        AltoClef.onPostTick.addListener(new ActionListener<AltoClef>() {
            @Override
            public void invoke(AltoClef altoClef) {
                onTick.accept(altoClef);
            }
        });
    }
}
