package redcoder.texteditor;

import redcoder.texteditor.action.*;
import redcoder.texteditor.exception.UnSupportedComponentOperationException;
import redcoder.texteditor.utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static redcoder.texteditor.action.ActionName.*;

/**
 * 编辑器主窗格，支持ActionListener机制
 */
public class MainPane extends JTabbedPane {

    private static final Font DEFAULT_FONT = new Font(null, Font.PLAIN, 16);
    private static final int FONT_SIZE_MINIMUM = 10;
    private static final int FONT_SIZE_MAXIMUM = 1000;
    private static final Object[] CLOSE_OPTIONS = {"Save", "Don't Save", "Cancel"};

    private ScrollTextPane selectedScrollTextPane;
    private JFileChooser fileChooser;
    private Map<ActionName, Action> defaultActions;
    // font used by ScrollTextPane's all instance
    private Font stpFont = DEFAULT_FONT;

    public MainPane() {
        this.fileChooser = new JFileChooser();
        this.fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return !f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Just Files";
            }
        });

        // create default action
        defaultActions = createDefaultActions();
        // set font
        setFont(new Font(null, Font.PLAIN, 16));
        addChangeListener(e -> {
            selectedScrollTextPane = (ScrollTextPane) getSelectedComponent();
            if (selectedScrollTextPane != null) {
                selectedScrollTextPane.updateIndex(this.getSelectedIndex());
            }
        });
    }

    private Map<ActionName, Action> createDefaultActions() {
        Map<ActionName, Action> actions = new HashMap<>();
        actions.put(UNDO, new UndoActionWrapper(this));
        actions.put(REDO, new RedoActionWrapper(this));
        actions.put(ZOOM_IN, new ZoomInAction(this));
        actions.put(ZOOM_OUT, new ZoomOutAction(this));
        actions.put(NEW_FILE, new NewFileAction(this));
        actions.put(OPEN_FILE, new OpenFileAction(this));
        actions.put(SAVE_FILE, new SaveFileAction(this));
        actions.put(SAVE_ALL, new SaveAllFileAction(this));
        actions.put(CUT, new CutAction());
        actions.put(COPY, new CopyAction());
        actions.put(PASTE, new PasteAction());
        actions.put(CLOSE, new CloseFileAction(this));
        actions.put(CLOSE_ALL, new CloseAllFileAction(this));
        return actions;
    }

    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        super.insertTab(title, icon, component, tip, index);
        // 设置自定义的TabComponent
        ButtonTabComponent buttonTabComponent = new ButtonTabComponent(this, title);
        this.setTabComponentAt(index, buttonTabComponent);
        if (component instanceof ScrollTextPane) {
            ScrollTextPane scrollTextPane = (ScrollTextPane) component;
            scrollTextPane.setButtonTabComponent(buttonTabComponent);
        }
    }

    // --------- operation about ActionListener
    public synchronized void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public synchronized void remove(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }

    public void fireActionEvent(ActionEvent event) {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    // ------------ operation about font

    /**
     * 放大字体（当前仅放大编辑窗口内的文本字体）
     */
    public void zoomInFont() {
        int newSize = Math.min(stpFont.getSize() + 2, FONT_SIZE_MAXIMUM);
        stpFont = new Font(stpFont.getName(), stpFont.getStyle(), newSize);
        setChildComponentFont();
    }

    /**
     * 缩小字体（当前仅缩小编辑窗口内的文本字体）
     */
    public void zoomOutFont() {
        int newSize = Math.max(stpFont.getSize() - 2, FONT_SIZE_MINIMUM);
        stpFont = new Font(stpFont.getName(), stpFont.getStyle(), newSize);
        setChildComponentFont();
    }

    private void setChildComponentFont() {
        for (Component component : getComponents()) {
            if (component instanceof ScrollTextPane) {
                ScrollTextPane scrollTextPane = (ScrollTextPane) component;
                scrollTextPane.getTextPane().setFont(stpFont);
            }
        }
    }

    // -------------  operation about file

    /**
     * 打开文件
     *
     * @return true：打开成功，false：打开失败
     */
    public boolean openFile() {
        int state = fileChooser.showOpenDialog(this);
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            openFile(file);
            return true;
        }
        return false;
    }

    private void openFile(File file) {
        String filename = file.getName();
        String content = FileUtils.readFile(file);

        ScrollTextPane scrollTextPane = new ScrollTextPane(this, file);
        this.addActionListener(scrollTextPane);
        this.addTab(filename, scrollTextPane);
        this.setSelectedComponent(scrollTextPane);

        scrollTextPane.setModifyAware(false);
        scrollTextPane.getTextPane().setText(content);
        scrollTextPane.setModifyAware(true);

        scrollTextPane.updateIndex(this.getSelectedIndex());
    }

    /**
     * 保存当前tab下的文件
     *
     * @return true：保存成功，false：保存失败
     */
    public boolean saveSelectedFile() {
        return saveFile(getSelectedIndex());
    }

    public boolean saveFile(int index) {
        Component component = this.getComponentAt(index);
        if (!(component instanceof ScrollTextPane)) {
            throw new UnSupportedComponentOperationException("Can't save tab at '" + index + "', it's not of type ScrollTextPane");
        }
        ScrollTextPane scrollTextPane = (ScrollTextPane) component;

        boolean saved = false;
        if (scrollTextPane.isLocal()) {
            saveFile(scrollTextPane.getFile(), scrollTextPane);
            saved = true;
        } else {
            int state = fileChooser.showSaveDialog(this);
            if (state == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.exists()) {
                    String message = String.format("%s already exist, would you like overwriting it?", file.getName());
                    int n = JOptionPane.showConfirmDialog(this, message, EditorFrame.TITLE, JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        saveFile(file, scrollTextPane);
                        saved = true;
                    }
                } else {
                    saveFile(file, scrollTextPane);
                    saved = true;
                }
                if (saved) {
                    scrollTextPane.setFile(file);
                    scrollTextPane.setLocal(true);
                }
            }
        }

        if (saved) {
            scrollTextPane.setModified(false);
        }
        return saved;
    }

    private void saveFile(File file, ScrollTextPane scrollTextPane) {
        JTextPane textPane = scrollTextPane.getTextPane();
        FileUtils.writeFile(textPane.getText(), file);

        // update tab title and filename
        scrollTextPane.getButtonTabComponent().updateTabbedTitle(file.getName());
        scrollTextPane.setFilename(file.getName());
    }

    /**
     * 保存所有tab下的文件。
     *
     * @return 只有所有的文件都保存成功才返回true，否则返回false。
     */
    public boolean saveAllFile() {
        for (int i = this.getTabCount() - 1; i >= 0; i--) {
            // switch to tab i
            this.setSelectedIndex(i);
            if (!saveFile(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 关闭当前tab下的文件
     *
     * @return true：关闭成功，false：关闭失败
     */
    public boolean closeSelectedFile() {
        return closeFile(getSelectedIndex());
    }

    /**
     * 关闭指定位置的tab
     *
     * @param index 位置
     * @return true：关闭成功，false：关闭失败
     */
    public boolean closeFile(int index) {
        Component component = this.getComponentAt(index);
        if (!(component instanceof ScrollTextPane)) {
            throw new UnSupportedComponentOperationException("Can't close tab at '" + index + "', it's not of type ScrollTextPane");
        }
        ScrollTextPane scrollTextPane = (ScrollTextPane) component;
        boolean closed = false;
        if (scrollTextPane.isModified()) {
            String message = String.format("Do you want to save the changes you made to %s?\n"
                    + "Your changes will be lost if you don't save them.", scrollTextPane.getFilename());
            int state = JOptionPane.showOptionDialog(this, message, EditorFrame.TITLE, JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, CLOSE_OPTIONS, CLOSE_OPTIONS[0]);
            if (state == JOptionPane.YES_OPTION) {
                // save file firstly, then close it.
                if (saveFile(index)) {
                    this.removeTabAt(this.getSelectedIndex());
                    closed = true;
                }
            } else if (state == JOptionPane.NO_OPTION) {
                // close file directly
                this.removeTabAt(this.getSelectedIndex());
                closed = true;
            }
            // user cancel operation, don't close it.
        } else {
            this.removeTabAt(this.getSelectedIndex());
            closed = true;
        }

        if (!closed) {
            System.err.println("Failed to close file " + scrollTextPane.getFilename());
        }
        return closed;
    }

    /**
     * 关闭所有tab下的文件。
     *
     * @return 只有所有的文件都关闭成功才返回true，否则返回false。
     */
    public boolean closeAllFile() {
        for (int i = this.getTabCount() - 1; i >= 0; i--) {
            // switch to tab i
            this.setSelectedIndex(i);
            if (!closeFile(i)) {
                return false;
            }
        }
        return true;
    }

    // ----------- getter

    /**
     * 返回当前tab下的文本窗
     */
    public ScrollTextPane getSelectedTextPane() {
        return selectedScrollTextPane;
    }

    /**
     * 返回使用的文件选择器
     */
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * 返回所有默认使用的Action
     */
    public Map<ActionName, Action> getDefaultActions() {
        return defaultActions;
    }

    /**
     * 返回所有tab下的文本窗共享的字体
     */
    public Font getStpFont() {
        return stpFont;
    }
}
