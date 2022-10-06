package xyz.roosterseatyou.elections.database;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database{
    String dbname;

    public SQLite(Plugin plugin){
        super(plugin);

        this.dbname = plugin.getConfig().getString("database.name");
    }


    public Connection getConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    private String CREATION_TABLE = "CREATE TABLE IF NOT EXISTS `elections` (\n" +
            "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  `hashmap` VARCHAR(10000) NOT NULL\n" +
            ");";

    private void setCreationTable(String query){
        CREATION_TABLE = query;
    }

    public void load() {
        connection = getConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(CREATION_TABLE);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        init("elections");
    }
}
