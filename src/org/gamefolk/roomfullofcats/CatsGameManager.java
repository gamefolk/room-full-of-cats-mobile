package org.gamefolk.roomfullofcats;

class Level
{
	int number;
	int mapWidth;
	int mapHeight;
	int levelTime;
	int fallTime;  // interval after which cats fall, in seconds
	int catsLimit; // the target number of cats of the same type to collect
}

public class CatsGameManager 
{
	static int curLevel = 1;
	
	static Level loadLevel() {
		Level level = new Level();
		
		if (curLevel == 1) {
			level.number = 1;
			level.mapWidth = 2;
			level.mapHeight = 5;
			level.levelTime = 5;
			level.fallTime = 1;
			level.catsLimit = 3;
		}
		return level;
	}
}
