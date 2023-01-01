package xyz.roosterseatyou.elections.database;

import com.google.gson.Gson;
import xyz.roosterseatyou.elections.Elections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class DataUtils {
    public static void storeHashMap(HashMap map, Database db){
        try {
            String json = hashmapToJson(map);
            PreparedStatement ps = db.getConnection().prepareStatement("INSERT INTO elections (hashmap) VALUES (?)");
            ps.setObject(1, json);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to store hashmap: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou");
            e.printStackTrace();
        }
    }

    public static void updateHashMap(HashMap map, Database db, UUID id, String tableName, String columnName){
        try {
            String json = hashmapToJson(map);
            PreparedStatement ps = db.getConnection().prepareStatement("UPDATE " + tableName + " SET " + columnName + " = ? WHERE id = ?");
            ps.setObject(1, json);
            ps.setString(2, id.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to store hashmap: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou");
            e.printStackTrace();
        }
    }

    public static HashMap getHashMap(Database db, UUID id, String name, String col) {
        HashMap map;
        try {
            Statement statement = db.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT col FROM elections WHERE id = " + id + " AND name = '" + name + "'");
            map = jsonToHashMap(rs.getString(col));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static String hashmapToJson(HashMap<UUID, Integer> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static HashMap jsonToHashMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, HashMap.class);
    }

    public static void executeSQLFile(Database db, String file){
        try {
            Statement statement = db.getConnection().createStatement();
            statement.execute(file);
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to execute SQL file: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou");
            e.printStackTrace();
        }
    }
}
