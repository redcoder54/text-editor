package redcoder.texteditor.action;

import redcoder.texteditor.core.Framework;
import redcoder.texteditor.core.file.FileProcessor;
import redcoder.texteditor.core.tabpane.TabPane;
import redcoder.texteditor.resources.IconResource;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenAction extends AbstractAction {

    public OpenAction() {
        super("Open File");
        putValue(Action.SMALL_ICON, IconResource.getImageIcon("open24.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            TabPane tabPane = Framework.getActivatedFrame().getTabPane();
            FileProcessor.openFile(tabPane);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
