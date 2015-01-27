package Hax.Bukkit.DestroyerOfWorldsWorldGeneration;

import java.util.Random;

public class Noise {
	Random r;

	public Noise() {
		r = new Random();
	}

	public void setSeed(long seed) {
		r.setSeed(seed);
	}

	public void printOutArray(float[][] arr) {
		for(int i = 0; i < arr.length; i++) {
			for(int n = 0; n < arr[0].length; n++) {
				System.out.print(arr[i][n] + ", "); 
			}
			System.out.print("\n");
		}
	}

	public void printOutTerrain(float[][] arr) {
		for(int i = 0; i < arr.length; i++) {
			for(int n = 0; n < arr[0].length; n++) {
				float a = arr[i][n];
				if(a < 0.4) {
					System.out.print("W"); 
				} else {
					System.out.print("L"); 
				}
			}
			System.out.print("\n");
		}
	}

	public float[][] genWhiteNoise(int width, int height) {
		float[][] noise = new float[height][width];

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				noise[y][x] = r.nextFloat();
			}
		}

		return noise;
	}

	public float[][] genSmoothNoise(float[][] baseNoise, int octave) {
		int height = baseNoise.length;
		int width = baseNoise[0].length;

		float[][] smoothNoise = new float[height][width];

		int samplePeriod = 1 << octave; //calculates 2^k
		float sampleFrequency = (float) (1.0/samplePeriod);

		for(int i = 0; i < height; i++) {
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % height; //wrap around
			float horizontal_blend = (i - sample_i0) * sampleFrequency;

			for(int n = 0; n < width; n++) {
				int sample_n0 = (n / samplePeriod) * samplePeriod;
				int sample_n1 = (sample_n0 + samplePeriod) % width; //wrap around
				float vertical_blend = (n - sample_n0) * sampleFrequency;

				//blend the top two corners
				float top = Interpolate(baseNoise[sample_i0][sample_n0],
						baseNoise[sample_i1][sample_n0], horizontal_blend);

				//blend the bottom two corners
				float bottom = Interpolate(baseNoise[sample_i0][sample_n1],
						baseNoise[sample_i1][sample_n1], horizontal_blend);

				//final blend
				smoothNoise[i][n] = Interpolate(top, bottom, vertical_blend);
			}
		}

		return smoothNoise;
	}

	public float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount, float persistance)
	{
		int height = baseNoise.length;
		int width = baseNoise[0].length;

		float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing

		//generate smooth noise
		for (int i = 0; i < octaveCount; i++)
		{
			smoothNoise[i] = genSmoothNoise(baseNoise, i);
		}

		float[][] perlinNoise = new float[height][width];
		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;

		//blend noise together
		for (int octave = octaveCount - 1; octave >= 0; octave--)
		{
			amplitude *= persistance;
			totalAmplitude += amplitude;

			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
				}
			}
		}

		//normalisation
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				perlinNoise[i][j] /= totalAmplitude;
			}
		}

		return perlinNoise;
	}

	//linear average between two points
	private float Interpolate(float x0, float x1, float alpha)
	{
		return Cosine_Interpolate(x0, x1, alpha);
	}

	//Linear Interpolation
	private float Linear_Interpolate(float x0, float x1, float alpha)
	{
		return x0 * (1 - alpha) + alpha * x1;
	}

	//Cosine interpolation (much smoother)
	private float Cosine_Interpolate(float x0, float x1, float alpha)
	{
		float ft = (float) (alpha * 3.141592653589);
		float f = (float) ((1 - Math.cos(ft)) * 0.5);

		return x0*(1-f) + x1*f;
	}
}