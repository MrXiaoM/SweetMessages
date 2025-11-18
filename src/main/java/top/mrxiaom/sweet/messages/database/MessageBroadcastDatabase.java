package top.mrxiaom.sweet.messages.database;

import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.api.IRunTask;
import top.mrxiaom.pluginbase.database.IDatabase;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.func.AbstractPluginHolder;
import top.mrxiaom.sweet.messages.func.BroadcastManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MessageBroadcastDatabase extends AbstractPluginHolder implements IDatabase {
    private final Set<String> sentMessages = new HashSet<>();
    private String TABLE_NAME;
    private int lastSequence;
    private IRunTask pollTask;
    public MessageBroadcastDatabase(SweetMessages plugin) {
        super(plugin);
    }

    @Override
    public void onDisable() {
        if (pollTask != null) {
            pollTask.cancel();
            pollTask = null;
        }
    }

    @Override
    public void reload(Connection conn, String tablePrefix) throws SQLException {
        TABLE_NAME = tablePrefix + "broadcast";
        try (PreparedStatement ps = conn.prepareStatement(
                "CREATE TABLE if NOT EXISTS `" + TABLE_NAME + "`(" +
                        "`sequence` INT NOT NULL AUTO_INCREMENT," +
                        "`uuid` VARCHAR(64)," +
                        "`message` LONGTEXT" +
                ");"
        )) {
            ps.execute();
        }
        Integer lastSequence = getLastSequence(conn);
        if (lastSequence != null) {
            this.lastSequence = lastSequence;
        }
        if (pollTask != null) {
            pollTask.cancel();
        }
        pollTask = plugin.getScheduler().runTaskTimerAsync(this::poll, 20L, 20L);
    }

    @Nullable
    private Integer getLastSequence(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT count(*) FROM `" + TABLE_NAME + "`;"
        )) {
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return null;
    }

    private void handleMessage(byte[] data) {
        BroadcastManager manager = BroadcastManager.inst();
        try (DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(data))) {
            manager.onReceiveMessage(msgIn);
        } catch (Throwable t) {
            BukkitPlugin.getInstance().warn("接收处理数据库轮询消息时出现错误", t);
        }
    }

    public void insert(byte[] data) {
        String uuid = UUID.randomUUID().toString();
        sentMessages.add(uuid);
        try (Connection conn = plugin.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO `" + TABLE_NAME + "` (`uuid`,`message`) VALUES (?,?);"
            )) {
            ps.setString(1, uuid);
            ps.setString(2, Base64.getEncoder().encodeToString(data));
            ps.execute();
        } catch (SQLException e) {
            warn(e);
        }
    }

    public void poll() {
        List<byte[]> list = new ArrayList<>();
        try (Connection conn = plugin.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM `" + TABLE_NAME + "` WHERE `sequence`>?"
            )) {
            ps.setInt(1, this.lastSequence);
            try (ResultSet result = ps.executeQuery()) {
                while(result.next()) {
                    String uuid = result.getString("uuid");
                    if (sentMessages.contains(uuid)) continue;
                    String message = result.getString("message");
                    list.add(Base64.getDecoder().decode(message));
                }
            }
            Integer lastSequence = getLastSequence(conn);
            if (lastSequence != null) {
                this.lastSequence = lastSequence;
            }
        } catch (SQLException e) {
            warn(e);
        }
        for (byte[] data : list) {
            handleMessage(data);
        }
    }
}
