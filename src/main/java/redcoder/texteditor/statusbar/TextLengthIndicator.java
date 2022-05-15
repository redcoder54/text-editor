package redcoder.texteditor.statusbar;

import redcoder.texteditor.pane.textpane.ScrollTextPane;
import redcoder.texteditor.pane.textpane.TextPaneChangeListener;

import javax.swing.*;
import java.awt.*;

/**
 * 显示当前文本窗格的字符长度和行数。
 */
public class TextLengthIndicator extends JPanel implements Indicator,TextPaneChangeListener {

    private static final String TEMPLATE_TEXT = "  length: %d, lines: %d";

    private final JLabel label = new JLabel();

    public TextLengthIndicator(){
        super(new GridLayout(1, 1));
        setBorder(StatusBarBorder.BORDER);

        label.setText(getFormattedText(0, 0));
        add(label);
    }

    @Override
    public void hidden() {
        setVisible(false);
    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void onChange(ScrollTextPane textPane) {
        JTextArea textArea = textPane.getTextArea();
        int length = textArea.getText().length();
        int lineCount = textArea.getLineCount();
        label.setText(getFormattedText(length, lineCount));
    }

    private String getFormattedText(int length, int lines) {
        return String.format(TEMPLATE_TEXT, length, lines);
    }
}
