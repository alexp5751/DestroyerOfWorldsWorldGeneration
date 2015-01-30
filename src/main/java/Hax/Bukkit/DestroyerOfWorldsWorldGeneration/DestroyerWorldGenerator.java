package Hax.Bukkit.DestroyerOfWorldsWorldGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class DestroyerWorldGenerator extends ChunkGenerator implements Listener {
	
	private JavaPlugin plugin;
	private final int NUM_X_CHUNKS = 8;
	private final int NUM_Z_CHUNKS = 8;
	private int[][] heightMap;
	private List<Material> weaponMaterials;
	
	public DestroyerWorldGenerator(JavaPlugin plugin) {
		this.plugin = plugin;
		this.heightMap = generateHeightMap();
		this.weaponMaterials = Arrays.asList(new Material[] { Material.SAND, Material.SANDSTONE, Material.BOOKSHELF });
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
		byte[] result = new byte[32768];
		if (chunkX >= 0 && chunkX < NUM_X_CHUNKS && chunkZ >= 0 && chunkZ < NUM_Z_CHUNKS) {
			for (int x = 0; x < 16; x++){
				for (int z = 0; z < 16; z++) {
					int endY = heightMap[16 * chunkX + x][16 * chunkZ + z];
					for (int y = 0; y < endY; y++) {
						result[byteFormat(x, y, z)] = (byte) Material.GRASS.getId();
					}
				}
			}
		}
		return result;
	}

	
	@Override
	public boolean canSpawn(World world, int x, int z) {
        return false;
    }
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
    }
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
        return getRandomLocation(world);
    }
	
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		final WorldLoadEvent loadEvent = event;
		for (int i = 0; i < 50; i++) {
			final int j = i;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					Location spot = getRandomLocation(loadEvent.getWorld());
					Block block = loadEvent.getWorld().getBlockAt(spot);
					changeSurroundingBlocks(block);
					spot.setY(spot.getY() + 1);
					loadEvent.getWorld().dropItem(spot, new ItemStack(getRandomWeaponMaterial()));
					Firework fw = (Firework) loadEvent.getWorld().spawnEntity(spot, EntityType.FIREWORK);
					FireworkMeta fwm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder().withColor(Color.PURPLE).with(Type.BALL_LARGE).build();
					fwm.addEffect(effect);
					fwm.setPower(1);
					fw.setFireworkMeta(fwm);
					plugin.getServer().broadcastMessage("Hail Sithis " + j);
				}
			}, 36 * i);
		}
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				Location spot = getRandomLocation(loadEvent.getWorld());
				Block block = loadEvent.getWorld().getBlockAt(spot);
				changeSurroundingBlocks(block);
				spot.setY(spot.getY() + 1);
				loadEvent.getWorld().dropItem(spot, new ItemStack(getRandomWeaponMaterial()));
				Firework fw = (Firework) loadEvent.getWorld().spawnEntity(spot, EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				FireworkEffect effect = FireworkEffect.builder().withColor(Color.PURPLE).with(Type.BALL_LARGE).build();
				fwm.addEffect(effect);
				fwm.setPower(1);
				fw.setFireworkMeta(fwm);
				plugin.getServer().broadcastMessage("Hail Sithis");
			}
		}, 1800, 100);
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		if (event.getEntity().getItemStack().getType().equals(Material.SAND)) {
			plugin.getServer().broadcastMessage("Hail Sithis!");
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		if (!weaponMaterials.contains(event.getEntity().getItemStack().getType())) {
			event.setCancelled(true);
		}
	}
	
    private int byteFormat(int x, int y, int z) {
    	return (x * 16 + z) * 128 + y;
    }
    
    private int[][] generateHeightMap() {
    	Noise p = new Noise();
    	float[][] smooth = p.GeneratePerlinNoise(p.genWhiteNoise(NUM_X_CHUNKS * 16, NUM_Z_CHUNKS * 16), 5, (float) 0.05);
    	int[][] heightMap = new int[NUM_X_CHUNKS * 16][NUM_Z_CHUNKS * 16];
    	int multiplier = 32;
    	for (int x = 0; x < heightMap.length; x++) {
    		for (int z = 0; z < heightMap[x].length; z++) {
    			heightMap[x][z] = (int) (multiplier * smooth[x][z]);
    		}
    	}
    	return heightMap;
    }
    
    private Location getRandomLocation(World world) {
    	int x = (int) (Math.random() * 16 * NUM_X_CHUNKS);
        int z = (int) (Math.random() * 16 * NUM_Z_CHUNKS);
        return new Location(world, x, heightMap[x][z], z);
    }
    
    private void changeSurroundingBlocks(Block block) {
    	Location loc = block.getLocation();
    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			for (int k = 0; k < 3; k++) {
    				Block nearbyBlock = block.getWorld().getBlockAt(loc.getBlockX() - 1 + i, 
    						loc.getBlockY() - 1 + j, loc.getBlockZ() - 1 + k);
    				if (!nearbyBlock.isEmpty()) {
    					nearbyBlock.setType(Material.GOLD_BLOCK);
    				}
    			}
    		}
    	}
    }
    
    private Material getRandomWeaponMaterial() {
    	int index = (int)(Math.random() * weaponMaterials.size());
    	return weaponMaterials.get(index);
    }
}
