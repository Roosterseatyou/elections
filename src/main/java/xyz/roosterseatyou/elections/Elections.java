package xyz.roosterseatyou.elections;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.roosterseatyou.elections.database.Database;
import xyz.roosterseatyou.elections.database.DatabaseTestCommand;
import xyz.roosterseatyou.elections.database.SQLite;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;

public final class Elections extends JavaPlugin {
    private static SQLite sqlite;
    private static Elections instance;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        sqlite = new SQLite(this);
        sqlite.load();
        instance = this;
        getCommand("databasetest").setExecutor(new DatabaseTestCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Elections getInstance(){
        return instance;
    }

    public static Connection getSqliteConn(){
        return sqlite.getConnection();
    }

    public static Database getDatabase(){
        return sqlite;
    }
}
