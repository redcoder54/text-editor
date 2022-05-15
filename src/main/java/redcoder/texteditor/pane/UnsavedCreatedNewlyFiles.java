package redcoder.texteditor.pane;

import org.apache.commons.lang3.SystemUtils;
import redcoder.texteditor.pane.textpane.ScrollTextPane;
import redcoder.texteditor.utils.FileUtils;
import redcoder.texteditor.utils.ScheduledUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 管理新创建的且未保存的文件
 */
public class UnsavedCreatedNewlyFiles {

    private static final String DIR_NAME = "ucnf";
    private final Map<String, ScrollTextPane> textPanes = new HashMap<>();
    private final File targetDir;
    private boolean loaded;

    public UnsavedCreatedNewlyFiles() {
        targetDir = new File(SystemUtils.getUserDir(), DIR_NAME);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        ScheduledUtils.scheduleAtFixedRate(() -> {
            try {
                for (Map.Entry<String, ScrollTextPane> entry : textPanes.entrySet()) {
                    String filename = entry.getKey();
                    File f = new File(targetDir, filename);
                    FileUtils.writeFile(entry.getValue().getTextArea().getText(), f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 3, TimeUnit.MINUTES);
    }

    public void addTextPanes(ScrollTextPane textPane) {
        this.textPanes.putIfAbsent(textPane.getFilename(), textPane);
    }

    public void removeTextPane(ScrollTextPane textPane) {
        String filename = textPane.getFilename();
        this.textPanes.remove(filename);
        // delete file
        File f = new File(targetDir, filename);
        f.delete();
    }

    /**
     * 加载未保存的新建text pane
     *
     * @param mainTabPane 主窗格
     * @return 加载的text pane数量
     */
    public int load(MainTabPane mainTabPane) {
        if (loaded) {
            return 0;
        }
        loaded = true;
        File[] files = targetDir.listFiles(pathname -> !pathname.isDirectory());
        if (files == null || files.length == 0) {
            return 0;
        }
        for (File file : files) {
            Framework.INSTANCE.getFileProcessor().openFile(mainTabPane, file, true);
        }
        return files.length;
    }
}
