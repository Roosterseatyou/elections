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
    public static void storeHashMap(HashMap<UUID, Integer> map, Database db){
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

    public static void updateHashMap(HashMap<UUID, Integer> map, Database db, int id){
        try {
            String json = hashmapToJson(map);
            PreparedStatement ps = db.getConnection().prepareStatement("UPDATE elections SET hashmap = ? WHERE id = ?");
            ps.setObject(1, json);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to store hashmap: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou");
            e.printStackTrace();
        }
    }

    public static HashMap<UUID, Integer> getHashMap(Database db, int id) {
        HashMap<UUID, Integer> map;
        try {
            Statement statement = db.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM elections WHERE id = " + id);
            map = jsonToHashMap(rs.getString("hashmap"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static String hashmapToJson(HashMap<UUID, Integer> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static HashMap<UUID, Integer> jsonToHashMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, HashMap.class);
    }
}
