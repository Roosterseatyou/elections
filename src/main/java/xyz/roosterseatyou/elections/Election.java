package xyz.roosterseatyou.elections;

import org.bukkit.entity.Player;
import xyz.roosterseatyou.elections.database.DataUtils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class Election {
    private UUID id;
    private String name;
    private String description;
    private HashMap<UUID, Integer> votes;

    public Election(UUID id, String name, String description, HashMap<UUID, Integer> votes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.votes = votes;
    }

    public Election(String name, String description) {
        this(UUID.randomUUID(), name, description, new HashMap<>());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<UUID, Integer> getVotes() {
        return votes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVotes(HashMap<UUID, Integer> votes) {
        this.votes = votes;
    }

    public void addVote(Player candidate) {
        votes.put(candidate.getUniqueId(), votes.getOrDefault(candidate.getUniqueId(), 0) + 1);
    }

    public void removeVote(Player candidate) {
        votes.remove(candidate.getUniqueId());
    }

    public int getVote(Player candidate) {
        return votes.get(candidate.getUniqueId());
    }

    public boolean save() {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            //CHECK IF ELECTION EXISTS
            if (statement.executeQuery("SELECT * FROM elections WHERE id = " + id).next()) {
                //UPDATE
                DataUtils.updateHashMap(votes, Elections.getDatabase(), id);
                statement.executeUpdate("UPDATE elections SET name = '" + name + "', description = '" + description + "' WHERE id = " + id);
                return true;
            } else {
                //INSERT
                statement.executeUpdate("INSERT INTO elections (id, name, description) VALUES ('" + id + "', '" + name + "', '" + description + "')");
                DataUtils.storeHashMap(votes, Elections.getDatabase());
                return true;
            }
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to save election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
            return false;
        }

    }

    public void delete() {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            statement.executeUpdate("DELETE FROM elections WHERE id = " + id);
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to delete election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
        }
    }

    public static Election getFromUUID(UUID id) {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            if (statement.executeQuery("SELECT * FROM elections WHERE id = " + id).next()) {
                return new Election(id, statement.executeQuery("SELECT name FROM elections WHERE id = " + id).getString("name"), statement.executeQuery("SELECT description FROM elections WHERE id = " + id).getString("description"), DataUtils.getHashMap(Elections.getDatabase(), id));
            } else {
                return null;
            }
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to get election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
            return null;
        }
    }

    public static Election getFromName(String name) {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            if (statement.executeQuery("SELECT * FROM elections WHERE name = '" + name + "'").next()) {
                return new Election(UUID.fromString(statement.executeQuery("SELECT id FROM elections WHERE name = '" + name + "'").getString("id")), name, statement.executeQuery("SELECT description FROM elections WHERE name = '" + name + "'").getString("description"), DataUtils.getHashMap(Elections.getDatabase(), UUID.fromString(statement.executeQuery("SELECT id FROM elections WHERE name = '" + name + "'").getString("id"))));
            } else {
                return null;
            }
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to get election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
            return null;
        }
    }
}
