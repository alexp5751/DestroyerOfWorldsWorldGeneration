package Hax.Bukkit.DestroyerOfWorldsWorldGeneration;
 
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
 
public final class DestroyerOfWorldsWorldGeneration extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");
    }
 
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new DestroyerWorldGenerator(this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("basic")) { // If the player typed /basic then do the following...
    		// doSomething
    		return true;
    	} //If this has happened the function will return true. 
            // If this hasn't happened the value of false will be returned.
    	return false; 
    }
}