package xyz.roosterseatyou.elections.database;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class Database {
    Plugin plugin;
    Connection connection;

    public Database(Plugin plugin){
        this.plugin = plugin;
    }


    public abstract Connection getConnection();

    public abstract void load();

    public void init(String table){
        connection = getConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * from " + table);
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
            plugin.getLogger().log(Level.INFO, "Successfully connected to database");
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void close(PreparedStatement ps, ResultSet rs){
        try{
            if (ps != null){
                ps.close();
            }
            if (rs != null){
                rs.close();
            }
        } catch (SQLException ex){
            plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection", ex);
        }
    }

    public void closeConnection() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                getConnection().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
