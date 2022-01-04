package me.adneths.advdragonfight.capability;

public class DragonDifficulty implements IDragonDifficulty
{

	private int difficulty = 1;
	
	@Override
	public int getDifficulty()
	{
		return this.difficulty;
	}

	@Override
	public void setDifficulty(int i)
	{
		this.difficulty = i;
	}

	@Override
	public void addDifficulty(int i)
	{
		this.difficulty += i;
	}
	
	
	
}
