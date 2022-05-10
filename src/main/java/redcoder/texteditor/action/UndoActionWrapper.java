package redcoder.texteditor.action;

import redcoder.texteditor.MainPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UndoActionWrapper extends AbstractAction {

    private MainPane mainPane;

    public UndoActionWrapper(MainPane mainPane) {
        super("Undo");
        this.mainPane = mainPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainPane.getSelectedTextPane().getUndoAction().actionPerformed(e);
    }
}
