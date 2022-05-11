package redcoder.texteditor.action;

import redcoder.texteditor.MainPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenFileAction extends AbstractAction {

    private MainPane mainPane;

    public OpenFileAction(MainPane mainPane) {
        super("Open File");
        this.mainPane = mainPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            mainPane.openFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
