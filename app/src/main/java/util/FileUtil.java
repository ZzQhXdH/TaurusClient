package util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/15.
 */

public class FileUtil {

    public static class Mp4Filter implements FileFilter {

        @Override
        public boolean accept(File pathname) {

            if (pathname.isDirectory()) {
                return true;
            } else {
                String name = pathname.getName();

                if (name.endsWith("mp4") || name.endsWith("MP4")) {
                    return true;
                } else {
                    return false;
                }
            }

        }
    }

    public static void scanFile(final String path, List<File> fileList, FileFilter filter) {

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (!file.isDirectory()) {
            return ;
        }
        File[] files = file.listFiles(filter);
        for (int i = 0; i < files.length; i ++) {
            if (files[i].isDirectory()) {
                scanFile(files[i].getPath(), fileList, filter);
            } else {
                fileList.add(files[i]);
            }
        }
    }

}
