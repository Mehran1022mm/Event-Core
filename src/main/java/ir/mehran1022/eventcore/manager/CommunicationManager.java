package ir.mehran1022.eventcore.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import ir.mehran1022.eventcore.Main;
import ir.mehran1022.eventcore.util.Common;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class CommunicationManager implements PluginMessageListener {

    private static final String PLUGIN_CHANNEL = "Event-Core";
    private static final String SYNCED_SUBCHANNEL = "Synced";

    private static boolean synced = false;

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        if (!s.equals(PLUGIN_CHANNEL)) return;

        final ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        final String subChannel = in.readUTF();

        if (subChannel.equals(SYNCED_SUBCHANNEL)) {
            synced = true;
        }
    }

    public static void sendSyncRequest() {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Sync");
    }

    public static void sendBroadcastToBungee(String message) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Broadcast");
        out.writeUTF(message);
    }

    public static void sendToAnotherServer(Player player, String serverName) {
        try (final ByteArrayOutputStream b = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(Main.getInstance(), "Event-Core", b.toByteArray());
        } catch (Exception e) {
            Common.sendMessage(player, "&cAn error occurred when trying to send you to *" + serverName + "*");
            e.printStackTrace();
        }
    }

    public static boolean isSynced() {
        return synced;
    }
}
