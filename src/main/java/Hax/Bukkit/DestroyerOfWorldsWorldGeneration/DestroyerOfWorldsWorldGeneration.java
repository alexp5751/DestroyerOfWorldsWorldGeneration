package Hax.Bukkit.DestroyerOfWorldsWorldGeneration;
 
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
 
public final class DestroyerOfWorldsWorldGeneration extends JavaPlugin {
	
	private DestroyerWorldGenerator generator;
	
    @Override
    public void onEnable() {
    	generator = new DestroyerWorldGenerator(this);
    	getServer().getPluginManager().registerEvents(generator, this);
    }
 
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return generator;
    }
}