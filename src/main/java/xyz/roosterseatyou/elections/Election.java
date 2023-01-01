package xyz.roosterseatyou.elections;

import org.bukkit.OfflinePlayer;
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
    private HashMap<UUID, UUID> voted;
    private boolean isRunning;

    public Election(UUID id, String name, String description, HashMap<UUID, Integer> votes, HashMap<UUID, UUID> voted, boolean isRunning) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.votes = votes;
        this.voted = voted;
        this.isRunning = isRunning;
    }

    public Election(String name, String description) {
        this(UUID.randomUUID(), name, description, new HashMap<>(), new HashMap<>(), false);
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

    public HashMap<UUID, UUID> getVoted() {
        return voted;
    }

    public boolean isRunning() {
        return isRunning;
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

    public void setVoted(HashMap<UUID, UUID> voted) {
        this.voted = voted;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
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

    public OfflinePlayer getVotedFor(Player voter) {
        OfflinePlayer player = null;
        if (voted.containsKey(voter.getUniqueId())) {
            player = Elections.getInstance().getServer().getOfflinePlayer(voted.get(voter.getUniqueId()));
        }
        return player;
    }

    public boolean save(String tableName) {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            //CHECK IF ELECTION EXISTS
            if (statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + id).next()) {
                //UPDATE
                DataUtils.updateHashMap(votes, Elections.getDatabase(), id, tableName, "votes");
                DataUtils.updateHashMap(voted, Elections.getDatabase(), id, tableName, "voted");
            } else {
                //INSERT
                statement.executeUpdate("INSERT INTO elections (id, name, description) VALUES ('" + id + "', '" + name + "', '" + description + "')");
                DataUtils.storeHashMap(votes, Elections.getDatabase());
            }
            return true;
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to save election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
            return false;
        }

    }

    public void delete(String tableName) {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            statement.executeUpdate("DELETE FROM tableName WHERE id = " + id);
        } catch (SQLException e) {
            Elections.getInstance().getLogger().severe("Failed to delete election: " + e.getMessage());
            Elections.getInstance().getLogger().severe("Please report to https://github.com/Roosterseatyou/elections");
            e.printStackTrace();
        }
    }

    public static Election getFromUUID(UUID id, boolean active) {
        String tableName = active ? "elections" : "elections_archive";
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            if (statement.executeQuery("SELECT * FROM tableName WHERE id = " + id).next()) {
                String name = statement.executeQuery("SELECT name FROM tableName WHERE id = " + id).getString("name");
                String description = statement.executeQuery("SELECT description FROM tableName WHERE id = " + id).getString("description");
                HashMap<UUID, Integer> votes = DataUtils.getHashMap(Elections.getDatabase(), id, tableName, "votes");
                HashMap<UUID, UUID> voted = DataUtils.getHashMap(Elections.getDatabase(), id, tableName, "voted");
                return new Election(id, name, description, votes, voted, active);
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

    public static Election getFromName(String name, boolean active) {
        try {
            Statement statement = Elections.getDatabase().getConnection().createStatement();
            if (statement.executeQuery("SELECT * FROM elections WHERE name = '" + name + "'").next()) {
                UUID id = UUID.fromString(statement.executeQuery("SELECT id FROM elections WHERE name = '" + name + "'").getString("id"));
                String description = statement.executeQuery("SELECT description FROM elections WHERE name = '" + name + "'").getString("description");
                HashMap<UUID, Integer> votes = DataUtils.getHashMap(Elections.getDatabase(), id, "elections", "votes");
                HashMap<UUID, UUID> voted = DataUtils.getHashMap(Elections.getDatabase(), id, "elections", "voted");
                return new Election(id, name, description, votes, voted, active);
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
