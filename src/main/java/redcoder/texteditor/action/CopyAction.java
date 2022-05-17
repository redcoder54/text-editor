package redcoder.texteditor.action;

import redcoder.texteditor.core.toolbar.ToolBarIconResource;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class CopyAction extends TextAction {

    public CopyAction() {
        super("Copy");
        Optional.ofNullable(ToolBarIconResource.getImageIcon("Copy24.gif"))
                .ifPresent(icon -> putValue(Action.SMALL_ICON, icon));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);
        if (target != null) {
            target.copy();
        }
    }
}
