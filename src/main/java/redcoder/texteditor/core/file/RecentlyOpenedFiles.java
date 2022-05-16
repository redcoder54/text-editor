package redcoder.texteditor.core.file;

import org.apache.commons.lang3.SystemUtils;
import redcoder.texteditor.utils.FileUtils;
import redcoder.texteditor.utils.ScheduledUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentlyOpenedFiles {

    private static final String FILENAME = "rof.rc";
    private final List<File> recentlyFiles = new ArrayList<>();
    private final File target;

    public RecentlyOpenedFiles() {
        target = new File(SystemUtils.getUserDir(), FILENAME);
        init();
    }

    /**
     * add the recently opened file
     *
     * @param file the recently opened file
     */
    public synchronized void addFile(File file) {
        if (recentlyFiles.contains(file)) {
            // exist, move it to the head
            recentlyFiles.remove(file);
            recentlyFiles.add(0, file);
        } else {
            // insert it to the head
            recentlyFiles.add(0, file);
        }
    }

    /**
     * get all recently opened files.
     */
    public List<File> getRecentlyFile() {
        return recentlyFiles;
    }

    private void init() {
        loadRecentFilesFromLocal();

        ScheduledUtils.scheduleAtFixedRate(() -> {
            try {
                if (!recentlyFiles.isEmpty()) {
                    ArrayList<File> copyList = new ArrayList<>(recentlyFiles);
                    recentlyFiles.clear();
                    String content = extract(copyList);
                    FileUtils.writeFile(content, target, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    private void loadRecentFilesFromLocal() {
        try (BufferedReader reader = new BufferedReader(new FileReader(target))) {
            String filepath = reader.readLine();
            while (filepath != null) {
                recentlyFiles.add(new File(filepath));
                filepath = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extract(List<File> files) {
        StringBuilder tmp = new StringBuilder();
        for (File file : files) {
            tmp.append(file.getAbsolutePath()).append("\n");
        }
        return tmp.toString();
    }
}