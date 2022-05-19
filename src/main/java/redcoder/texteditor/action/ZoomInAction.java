package redcoder.texteditor.action;

import redcoder.texteditor.core.font.FontChangeProcessor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

/**
 * 放大字体
 */
public class ZoomInAction extends AbstractAction {

    public ZoomInAction() {
        super("Zoom In");
        Optional.ofNullable(IconResource.getImageIcon("ZoomIn24.gif"))
                .ifPresent(icon -> putValue(Action.SMALL_ICON, icon));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FontChangeProcessor.zoomIn(this);
    }
}