package org.gamefolk.roomfullofcats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.eclipsesource.json.JsonObject;

import android.content.Context;

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
	static Context context;
	
	static Level loadLevel() {
		Level level = new Level();
		
		level.number = curLevel;
		
		if (curLevel == 1) {
			InputStream input = context.getResources().openRawResource(R.raw.scoretest);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				try {
					Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					input.close();
				}
			}
			catch (IOException e) {
				System.out.println("error loading level");
			}
			
			System.out.println("json: " + writer.toString());
			JsonObject mainObject = JsonObject.readFrom(writer.toString());
			
			System.out.println("level title: " + mainObject.get("levelTitle").asString());
			
			level.mapWidth = mainObject.get("columns").asInt();
			level.mapHeight = mainObject.get("rows").asInt();
			level.levelTime = mainObject.get("timeLimit").asInt();
			level.fallTime = 1;
			level.catsLimit = 3;
		} else if (curLevel == 2) {
			level.mapWidth = 2;
			level.mapHeight = 5;
			level.levelTime = 10;
			level.fallTime = 1;
			level.catsLimit = 3;
		}
		return level;
	}
}
