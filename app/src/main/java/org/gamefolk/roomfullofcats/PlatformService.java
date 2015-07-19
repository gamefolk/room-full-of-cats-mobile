package org.gamefolk.roomfullofcats;

import java.io.*;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class PlatformService {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static PlatformService instance;
    private final ServiceLoader<PlatformProvider> serviceLoader;
    private PlatformProvider provider;

    private PlatformService() {
        serviceLoader = ServiceLoader.load(PlatformProvider.class);

        Iterator<PlatformProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            if (provider == null) {
                provider = iterator.next();
                Log.info("Using PlatformProvider: " + provider.getClass().getName());
            } else {
                Log.info("This PlatformProvider is ignored: " + iterator.next().getClass().getName());
            }
        }

        if (provider == null) {
            Log.severe("No PlatformProvider implementation could be found!");
        }
    }

    public static synchronized PlatformService getInstance() {
        if (instance == null) {
            instance = new PlatformService();
        }
        return instance;
    }

    /**
     * This method allows loading a JAR resource as a File.
     * <p>
     * First, a brief warning:
     * <pre>
     *                                                 ,  ,
     *                                                / \/ \
     *                                               (/ //_ \_
     *      .-._                                      \||  .  \
     *       \  '-._                            _,:__.-"/---\_ \
     *  ______/___  '.    .--------------------'~-'--.)__( , )\ \
     * `'--.___  _\  /    |                         ,'    \)|\ `\|
     *      /_.-' _\ \ _:,_       Here be dragons!        " ||   (
     *    .'__ _.' \'-/,`-~`                                |/
     *        '. ___.> /=,|                                 |
     *         / .-'/_ )  '---------------------------------'
     *    snd  )'  ( /(/
     *              \\ "
     *               '=='
     * </pre>
     * <p>
     * Why use this deep magic?
     * <p>
     * On both iOS and Android, many APIs either require the R object (Android) or native file paths (both). Using the R
     * object would require us to copy resources in both main/resources and android/resources, and accessing
     * resources as native Files is simply impossible. On Android, we must use methods that load either a
     * path or a URI, such as {@link android.media.SoundPool#load(String, int) SoundPool.load(String, int)} or
     * {@link android.media.MediaPlayer#create(Context, Uri) MediaPlayer.create(Context, Uri)}, to load resource
     * files. On iOS, we use methods that lost a NSURL or NSString.
     * <p>
     * However, these methods can't read resources that have been packed into the JAR. Therefore, we read the resource
     * in as a stream, and write the stream to a temporary file. This file can then be read in by any native method to
     * load the resource.
     * <p>
     * In short, this hack lets us keep resources packed into main/resources, and still have native platforms load them
     * successfully.
     *
     * @param jarResource A String that represents a path in the JAR resources folder.
     * @returns A File containing the absolute path of a file containing that resource's data.
     */
    public File loadJarResourceStreamAsFile(String jarResource) {
        File file;
        try {
            file = convertInputStreamToFile(getClass().getResourceAsStream(jarResource), "resource", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private File convertInputStreamToFile(InputStream inputStream, String name, String extension) throws IOException {
        File tempFile;
        OutputStream outputStream = null;
        try {
            tempFile = File.createTempFile(name, extension, getCacheDir());
            outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[10 * 1024];   // 10KB
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            Log.info("Wrote temporary file " + tempFile.getName() + " with size " + tempFile.length());
            return tempFile;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public File getCacheDir() {
        return provider.getCacheDir();
    }
}
