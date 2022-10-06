package xyz.roosterseatyou.elections.database;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.roosterseatyou.elections.Elections;

import java.util.HashMap;
import java.util.UUID;

public class DatabaseTestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof org.bukkit.entity.Player) {
            sender.sendMessage("no.");
            return true;
        } else {
            HashMap<UUID, Integer> test = testHashMap();

            DataUtils.storeHashMap(test, Elections.getDatabase());
            sender.sendMessage("stored hashmap");

            HashMap<UUID, Integer> test2 = DataUtils.getHashMap(Elections.getDatabase(), 1);
            sender.sendMessage("got hashmap: " + test2);
        }
        return true;
    }

    public HashMap<UUID, Integer> testHashMap() {
        HashMap<UUID, Integer> test = new HashMap<>();
        test.put(UUID.randomUUID(), 1);
        test.put(UUID.randomUUID(), 2);
        test.put(UUID.randomUUID(), 3);
        test.put(UUID.randomUUID(), 4);
        return test;
    }
}
