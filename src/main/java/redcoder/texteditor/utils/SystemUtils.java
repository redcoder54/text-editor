package redcoder.texteditor.utils;

import sun.awt.OSInfo;

import java.security.AccessController;

public class SystemUtils {

    public static boolean isWindowsOS() {
        OSInfo.OSType osType = AccessController.doPrivileged(OSInfo.getOSTypeAction());
        return osType == OSInfo.OSType.WINDOWS;
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
}