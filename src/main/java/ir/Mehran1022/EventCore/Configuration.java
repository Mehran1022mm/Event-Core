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

package ir.Mehran1022.EventCore;

import ir.Mehran1022.FlameAPI.Core;
import org.bukkit.configuration.file.FileConfiguration;

public final class Configuration {

    public static String PREFIX;
    public static String ALREADY_STARTED;
    public static String NO_PERMISSION;
    public static String NO_DESC;
    public static String END;
    public static String NO_EVENT;
    public static String SERVER_NAME;
    public static int DURATION;
    public static void loadConfig() {
        Main instance = Main.getInstance();
        instance.reloadConfig();
        FileConfiguration config = instance.getConfig();

        PREFIX = Core.Color(config.getString("Prefix") + " ");
        ALREADY_STARTED = Core.Color(config.getString("Messages.AlreadyStarted"));
        NO_PERMISSION = Core.Color(config.getString("Messages.NoPermission"));
        NO_DESC = Core.Color(config.getString("Messages.NoDescription"));
        END = Core.Color(config.getString("Messages.EventEnd"));
        NO_EVENT = Core.Color(config.getString("Messages.NoActiveEvent"));
        SERVER_NAME = config.getString("EventServer");
        DURATION = config.getInt("Duration");
    }
}