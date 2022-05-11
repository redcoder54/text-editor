package redcoder.texteditor.shortcut;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

public class OpenFileHandler extends AbstractShortcutKeyHandler {

    public OpenFileHandler(Action action) {
        super(action);
    }

    @Override
    public boolean handle(KeyEvent e, Set<Integer> pressedKeys) {
        if (pressedKeys.contains(KeyEvent.VK_CONTROL)
                && pressedKeys.contains(KeyEvent.VK_O)) {
            ActionEvent event = new ActionEvent(e.getSource(), 0, "Open File",
                    System.currentTimeMillis(), 0);
            action.actionPerformed(event);

            return false;
        }
        return true;
    }
}