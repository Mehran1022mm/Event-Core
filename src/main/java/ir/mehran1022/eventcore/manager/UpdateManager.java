package ir.mehran1022.eventcore.manager;

import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.util.Common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class UpdateManager {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String RESOURCE_URL = "https://api.spigotmc.org/legacy/update.php?resource=%s";
    private static final String resourceId = "110088";

    public static void start() {
        final Runnable updater = UpdateManager::checkForUpdates;
        scheduler.scheduleAtFixedRate(updater, 0, 1, TimeUnit.HOURS);
    }

    private static void checkForUpdates() {
        Common.debug("[Debug] Checking for updates.");
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(RESOURCE_URL, resourceId)).openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "SpigotPluginUpdater");
            String latestVersion = new Scanner(connection.getInputStream()).nextLine();
            String currentVersion = Main.getInstance().getDescription().getVersion();
            if (!currentVersion.equals(latestVersion)) {
                Common.log("Found a newer version! Please consider update the plugin to fix current issues and use new features.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}