package redcoder.texteditor.action;

import redcoder.texteditor.core.tabpane.MainTabPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * 缩小字体
 */
public class ZoomOutAction extends AbstractAction {

    private MainTabPane mainTabPane;

    public ZoomOutAction(MainTabPane mainTabPane) {
        super("Zoom Out");
        this.mainTabPane = mainTabPane;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        mainTabPane.zoomOutFont();
    }
}
