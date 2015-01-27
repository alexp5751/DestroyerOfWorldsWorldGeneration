package Hax.Bukkit.DestroyerOfWorlds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class DestroyerWorldGenerator extends ChunkGenerator {
	
	private JavaPlugin plugin;
	private final int NUM_X_CHUNKS = 16;
	private final int NUM_Z_CHUNKS = 16;
	private int[][] heightMap;
	
	public DestroyerWorldGenerator(JavaPlugin plugin) {
		this.plugin = plugin;
		this.heightMap = generateHeightMap();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
		byte[] result = new byte[32768];
		if (chunkX >= 0 && chunkX < NUM_X_CHUNKS && chunkZ >= 0 && chunkZ < NUM_Z_CHUNKS) {
			for (int x = 0; x < 16; x++){
				for (int z = 0; z < 16; z++) {
					int startY = heightMap[16 * chunkX + x][16 * chunkZ + z];
					for (int y = startY; y < startY + 5; y++) {
						result[byteFormat(x, y, z)] = (byte) Material.GRASS.getId();
					}
				}
			}
		}
		return result;
	}

	
	@Override
	public boolean canSpawn(World world, int x, int z) {
        return true;
    }
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
    }
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 0, 0);
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
}
