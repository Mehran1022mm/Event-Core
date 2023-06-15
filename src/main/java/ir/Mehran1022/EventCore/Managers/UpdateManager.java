/*
                                MIT License

                        Copyright (c) 2023 Mehran1022

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
               furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
                    copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                                        SOFTWARE.
*/

package ir.Mehran1022.EventCore.Managers;

import ir.Mehran1022.EventCore.Main;
import ir.Mehran1022.EventCore.Utils.Common;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {

    private static final String API = "https://api.github.com/repos/Mehran1022mm/Event-Core/releases/latest";
    private String LatestVersion;
    private final File NEWJAR = new File(Main.getInstance().getDataFolder().getParent(), Main.getInstance().getName() + "-" + LastVersion() + ".jar");
    private final File OLDJAR = new File(Main.getInstance().getDataFolder().getParent(), Main.getInstance().getName() + "-" + Main.getInstance().getDescription().getVersion() + ".jar");
    private final String CurrentVersion = Main.getInstance().getDescription().getVersion();

    public void Start () {
        LatestVersion = LastVersion();
        if (ConfigManager.AUTOUPDATE) {
            if (NewVerAvailable()) {
                if (Download()) {
                    Common.Log("Auto-Update Successful. Updated To " + LatestVersion);
                    Install();
                } else {
                    Common.Log("Failed To Update.");
                }
            }
        } else if (ConfigManager.CHECKUPDATE) {
            NewVerAvailable();
        }
    }
    private String LastVersion () {
        try {
            URL url = new URL(API);
            HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
            Connection.setRequestMethod("GET");
            Connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            Connection.setConnectTimeout(5000);
            Connection.connect();

            int Status = Connection.getResponseCode();
            if (Status == 403) { Common.Log("GitHub API Rate Limit Reached. Cannot Check Update Right Now"); return null; }

            BufferedReader Reader = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
            String Json = Reader.readLine();
            JSONObject Data = new JSONObject(Json);
            return Data.getString("tag_name");
        } catch (Exception e) {
            Common.Log("Failed To Update: " + e.getMessage());
            return null;
        }
    }
    private boolean NewVerAvailable () {
        if (LatestVersion != null && !LatestVersion.equalsIgnoreCase(Main.getInstance().getDescription().getVersion())) {
            Common.Log("Found An Update: " + LatestVersion);
            return true;
        } else {
            Common.Log("No Updates Currently Available");
            return false;
        }
    }
    private boolean Download () {
        try {
            URL url = new URL(String.format("https://github.com/Mehran1022mm/Event-Core/releases/latest/download/Event-Core-%s.jar", LatestVersion));
            HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
            Connection.setRequestMethod("GET");
            Connection.setConnectTimeout(5000);
            Connection.connect();

            InputStream InputStream = Connection.getInputStream();
            byte[] JarByte = InputStream.readAllBytes();
            FileUtils.writeByteArrayToFile(NEWJAR, JarByte);

            InputStream.close();

            return true;
        } catch (Exception e) {
            Common.Log("Failed To Auto-Update: " + e.getMessage());
            return false;
        }
    }
    private void Install () {
        try {

            File OldConfig = new File(Main.getInstance().getDataFolder(), "config.yml");
            if (OldConfig.exists()) {
                OldConfig.renameTo(new File(Main.getInstance().getDataFolder(), CurrentVersion + "-config.yml"));
            }

            Thread.sleep(2000);
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Common.Log("<-------------------------------------------->");
                    Common.Log(" ");
                    Common.Log("     In Order To Load New Version, Please Restart/Reload The Server!     ");
                    Common.Log("[OPTIONAL] You Can Delete Old Plugin Jar File After Stopping The Server.");
                    Common.Log(" ");
                    Common.Log("<-------------------------------------------->");
                }
            }, 0L, 6000L);

        } catch (Exception e) {
            Common.Log("Failed To Auto-Update: " + e.getMessage());
        }
    }
}
