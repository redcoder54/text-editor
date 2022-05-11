package redcoder.texteditor.action;

import redcoder.texteditor.MainPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveFileAction extends AbstractAction {

    private MainPane mainPane;

    public SaveFileAction(MainPane mainPane) {
        super("Save File");
        this.mainPane = mainPane;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            mainPane.saveSelectedFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
