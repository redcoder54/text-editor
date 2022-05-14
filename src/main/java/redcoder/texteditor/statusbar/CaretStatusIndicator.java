package redcoder.texteditor.statusbar;

import redcoder.texteditor.utils.RowColumnUtils;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * 根据插入符号的位置展示一些信息：位于第几行第几列，选中的几个字符。
 */
public class CaretStatusIndicator extends JPanel implements CaretListener {

    private static final String TEMPLATE_TEXT1 = "  row %d, col %d";
    private static final String TEMPLATE_TEXT2 = "  row %d, col %d (%d selected)";

    private final JLabel label = new JLabel();

    public CaretStatusIndicator() {
        super(new GridLayout(1, 1));
        setBorder(StatusBarBorder.BORDER);

        label.setText(getFormattedText(1, 1, 0));
        add(label);
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        Object source = e.getSource();
        if (source instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) source;
            refresh(textComponent, e.getDot(), e.getMark());
        }
    }

    public void refresh(JTextArea textArea) {
        Caret caret = textArea.getCaret();
        refresh(textArea, caret.getDot(), caret.getMark());
    }

    private void refresh(JTextComponent textComponent, int dot, int mark) {
        SwingUtilities.invokeLater(() -> {
            int row = RowColumnUtils.getRow(dot, textComponent);
            int col = RowColumnUtils.getColumn(dot, textComponent);

            if (dot == mark) {
                label.setText(getFormattedText(row, col, 0));
            } else {
                int selectedCharCount = Math.abs(dot - mark);
                label.setText(getFormattedText(row, col, selectedCharCount));
            }
        });
    }

    private String getFormattedText(int row, int col, int selectedCharCount) {
        if (selectedCharCount > 0) {
            return String.format(TEMPLATE_TEXT2, row, col, selectedCharCount);
        }
        return String.format(TEMPLATE_TEXT1, row, col);
    }
}
