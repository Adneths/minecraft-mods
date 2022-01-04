package me.adneths.advdragonfight.entity;

public interface DifficultyScaled
{
	public int getSumDifficulty();
	public float getAverageDifficulty();
	public void setSumDifficulty(int sumDiff);
	public void setAverageDifficulty(float avgDiff);
	public void updateDifficulty();
}
