package org.gamefolk.roomfullofcats;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import org.gamefolk.roomfullofcats.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameView;
//import com.arcadeoftheabsurd.absurdengine.MobFoxNativeRequest;
import com.arcadeoftheabsurd.absurdengine.Sprite;
import com.arcadeoftheabsurd.absurdengine.Timer;
import com.arcadeoftheabsurd.absurdengine.WebUtils;
import com.arcadeoftheabsurd.j_utils.Delegate;
import com.arcadeoftheabsurd.j_utils.Pair;
import com.arcadeoftheabsurd.j_utils.Vector2d;
import com.mobfox.adsdk.nativeads.NativeAd;
import com.mobfox.adsdk.nativeads.NativeAd.ImageAsset;
import com.mobfox.adsdk.nativeads.NativeAdListener;
import com.mobfox.adsdk.nativeads.NativeAdManager;

public class CatsGame extends GameView implements NativeAdListener
{
	private final Vector2d mapLoc = new Vector2d(50, 50); // in pixels, the top left corner of the top left column of things on the screen
	private final Vector2d mapSize = new Vector2d(6, 10); // in columns, rows
	private final int bucketSpace = 60; // vertical pixels between the bottom of the columns and the buckets
	private final int incSize = 10; // the amount by which to increase the size of things as they collect
	private final Thing[][] map = new Thing[mapSize.x][mapSize.y];
	
	private Vector2d thingSize; // in pixels, set according to the size of the screen in onSizeChanged()
	
	private final int fallTime = 1; // interval after which things fall, in seconds
	private final int thingsLimit = 3; // the target number of things of the same type to collect
	private int score = 0;
	private Timer fallTimer;
	
	private final Random rGen = new Random();
	
	private NativeAdManager adManager = new NativeAdManager(getContext(), "http://my.mobfox.com/request.php", "80187188f458cfde788d961b6882fd53", this, null);
	
	@Override
	public void adLoaded(NativeAd ad) {
		// TODO Auto-generated method stub
		System.out.println("got ad: " + ad.getClickUrl());
		downloaded1 = makeSprite(loadBitmapHolder(((ImageAsset)ad.getImageAsset("icon")).bitmapHolder), 50, 50);
	}

	@Override
	public void adFailedToLoad() {
		// TODO Auto-generated method stub
		System.out.println("ad failed");
	}

	@Override
	public void impression() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adClicked() {
		// TODO Auto-generated method stub
		
	}
	
	public CatsGame(Context context, GameLoadListener loadListener) {
		super(context, loadListener);
		
		fallTimer = new Timer(fallTime, this, new Delegate() {
			public void function(Object... args) {		
				// move bottommost row into buckets  
				for (int x = 0; x < mapSize.x; x++) {
					Thing candidate = map[x][mapSize.y-2];

					if (candidate != null) {
						Thing current = map[x][mapSize.y-1];
						
						if (current != null) {	
							if (candidate.type == current.type) {								
								current.sprite.resize(current.sprite.getWidth() + incSize, current.sprite.getHeight() + incSize);
								current.things++;
								
								if (current.things == thingsLimit) {
									score++;
									map[x][mapSize.y-1] = null;
								}
							} else {
								current = null;
								candidate.sprite.translate(0, thingSize.y + bucketSpace);
								map[x][mapSize.y-1] = candidate;
							}
						} else {
							candidate.sprite.translate(0, thingSize.y + bucketSpace);
							map[x][mapSize.y-1] = candidate;
						}
					}
				}
				// descend things in all other rows 
				for (int y = mapSize.y-2; y > 0; y--) {
					for (int x = 0; x < mapSize.x; x++) {
						if (map[x][y-1] != null) {
							map[x][y-1].sprite.translate(0, thingSize.y);
						}
						map[x][y] = map[x][y-1];
					}
				}
				// fill the top row with new things
				for (int x = 0; x < mapSize.x; x++) {
					ThingType type = ThingType.values()[rGen.nextInt(4)];					
					map[x][0] = new Thing(type, makeSprite(type.bitmapId, mapLoc.x + (x * thingSize.x), mapLoc.y));
				}
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for (int x = 0; x < mapSize.x; x++) {
			for (int y = 0; y < mapSize.y-1; y++) {
				if (map[x][y] != null) {
					if (map[x][y].sprite.getBounds().contains((int)event.getX(), (int)event.getY())) {
						map[x][y] = null;
					}
				}
			}
		}
		return true;
    }
	
	@Override
	public void onDraw(Canvas canvas) {				
		for (int x = 0; x < mapSize.x; x++) {
			for (int y = 0; y < mapSize.y; y++) {
				if (map[x][y] != null) {
					drawSprite(canvas, map[x][y].sprite);
				}
			}
		}
		
		if (downloaded1 != null) {
			drawSprite(canvas, downloaded1);
			//drawSprite(canvas, downloaded2);
		}
	}

	@Override
	protected void setupGame(int width, int height) {
		thingSize = new Vector2d((width / mapSize.x) - 20, (height / mapSize.y) - 20);
		
		ThingType.GEAR.setBitmap   (loadBitmapResource(ThingType.GEAR.resourceId,    thingSize));
		ThingType.SHROOM.setBitmap (loadBitmapResource(ThingType.SHROOM.resourceId,  thingSize));
		ThingType.CRYSTAL.setBitmap(loadBitmapResource(ThingType.CRYSTAL.resourceId, thingSize));
		ThingType.ROCK.setBitmap   (loadBitmapResource(ThingType.ROCK.resourceId,    thingSize));
	}
	
	@Override
	protected void startGame() {
		fallTimer.start();	
		adManager.requestAd();
	}
	
	boolean test = false;
	Sprite downloaded1;
	Sprite downloaded2;
	
	@Override
	protected void updateGame() {
		if (!test) {
			test = true;
			try {
				/*
				 * IMAGE TEST
				 */
				
				/*System.out.println("ip address: " + WebUtils.getLocalIpAddress());
				
				System.out.println("downloading images");
				
				String fileName1 = "image.png";
				String filePath = WebUtils.downloadFile("http://pbs.twimg.com/profile_images/459885147328749568/iDElBRxE.jpeg", fileName1, getContext());
				downloaded1 = makeSprite(loadTempBitmapFile(filePath, fileName1, new Vector2d(50, 50)), 0, 0);
				
				String fileName2 = "image2.png";
				filePath = WebUtils.downloadFile("http://3.bp.blogspot.com/-Xo0EuTNYNQg/UEI1zqGDUTI/AAAAAAAAAYE/PLYx5H4J4-k/s1600/smiley+face+super+happy.jpg", fileName2, getContext());
				downloaded2 = makeSprite(loadTempBitmapFile(filePath, fileName2, new Vector2d(50, 50)), 50, 50);*/
				
				/*
				 * FIRST AD TEST
				 */
				
				/*System.out.println("getting ip...");
				String ip = DeviceUtility.getLocalIp();
				System.out.println(ip);
				
				System.out.println("getting ad id...");
				String adId = DeviceUtility.getAdId();
				System.out.println(adId);
				
				MobFoxNativeRequest adRequest = new MobFoxNativeRequest();
				
				adRequest.publisherId = "80187188f458cfde788d961b6882fd53"; // test id
				adRequest.userAgent = URLEncoder.encode(DeviceUtility.getUserAgent(), "UTF-8");
				adRequest.ipAddress = ip;
				adRequest.adId = adId;
				
				adRequest.imageTypes = new String[]{"icon"};
				adRequest.textTypes = new String[]{"headline"};
				
				System.out.println("compiling request...");
				String request = adRequest.compileRequest();
				System.out.println(request);
				
				System.out.println("sending request...");
				String result = WebUtils.restRequest(request);
				System.out.println(result);*/
				
				/*
				 * InetAddress test
				 */
				
				/*System.out.println("by name:");
				
				InetAddress addr = InetAddress.getByName("arcadeoftheabsurd.com");
				System.out.println("Printing IP:");
				for(byte b : addr.getAddress()){
					System.out.println(b + ".");
				}
				System.out.println("hostname: " + addr.getHostName());
				System.out.println("canonical name: " + addr.getCanonicalHostName());
				System.out.println();
				
				System.out.println("by address:");
				
				InetAddress addr2 = InetAddress.getByAddress(new byte[]{(byte)72,(byte)167,(byte)3,(byte)128});
				System.out.println("Printing IP:");
				for(byte b : addr2.getAddress()){
					System.out.println(b + ".");
				}
				System.out.println("hostname: " + addr2.getHostName());
				System.out.println("canonical name: " + addr2.getCanonicalHostName());
				System.out.println();*/
				
				/*
				 * Socket test
				 */
				
			    /*InetAddress addr = InetAddress.getByName("arcadeoftheabsurd.com");
				
				String address = addr.getHostAddress();
				String host = addr.getHostName();
				
				System.out.println("address: " + address);
				System.out.println("host: " + host);
				
				Socket s = new Socket(address, 80);
				
				System.out.println("connected?: " + s.isConnected());
				
				PrintWriter pw = new PrintWriter(s.getOutputStream());
				pw.println("GET / HTTP/1.1");
				pw.println("Host: arcadeoftheabsurd.com");
				pw.println("");
				pw.flush();
				
				System.out.println("connected?: " + s.isConnected());
				
				InputStreamReader ir = new InputStreamReader(s.getInputStream());
				
				char[] chars = new char[32];
				
				while(ir.read(chars) != -1) {
					System.out.println("connected?: " + s.isConnected());
					System.out.println(String.valueOf(chars));
					chars = new char[32];
				}
				
				ir.close();*/
				
				/*
				 * URL TEST
				 */
								
				/*URL url = new URL("http://arcadeoftheabsurd.com");
				
				URLConnection conn = url.openConnection();
								
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

				String line = "";
				while((line = in.readLine()) != null) {
					System.out.println(line);
				}
				
				in.close();*/
	        } catch (Exception e) {}
		}
	}

	private class Thing
	{
		public int things = 0;
		public ThingType type;
		public Sprite sprite;
		
		public Thing (ThingType type, Sprite sprite) {
			this.type = type;
			this.sprite = sprite;
		}
	}

	private enum ThingType
	{
		GEAR(R.drawable.gear), SHROOM(R.drawable.shroom), CRYSTAL(R.drawable.crystal), ROCK(R.drawable.rock);
		
		private int resourceId;
		private int bitmapId = -1;
		
		private ThingType(int resourceId) {
			this.resourceId = resourceId;
		}
		
		public void setBitmap(int bitmapId) {
			this.bitmapId = bitmapId;
		}
	}
}