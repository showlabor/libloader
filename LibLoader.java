/**
 *  <p>This is a tiny helper class for loading versioned native libraries in Android.</p>
 *
 *  <p>Android can't properly handle versioned libraries like libfoo.so.1.2. That's not a big deal for a stand alone lib:
 *  You can just strip the version from the lib name, i.e. rename it to libfoo.so and use it as if it was unversioned.</p>
 *
 *  <p>But if you have to satisfy another lib's dependency with you can't get away that easily.</p>
 *
 *  <p>LibLoader works around this limitation. You provide your lib with the version stripped from the file name.
 *  In this way you can still utilize Android mechanism for choosing the right lib for the right architecture. You
 *  then load the lib via LibLoader by passing it the naked libname and the version, e.g. LibLoader.load("foo", "1").</p>
 *
 *  <p>Keep in mind: A true solution can only be brought with future Android versions.</p>
 *
 *  @author Felix Homann <linuxaudio@showlabor.de>
 */

package de.showlabor.nativetools;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class LibLoader {
    private Context mContext;
    private static final String DEFAULT_LIBLOADER_DIR = "lib";
    private String mLibLoaderDir;

    public LibLoader(Context context) {
        mContext = context;
        mLibLoaderDir = DEFAULT_LIBLOADER_DIR;
    }

    public LibLoader(Context context, String libLoaderDir) {
        mContext = context;
        mLibLoaderDir = libLoaderDir;
    }


    /**
     * Loads a versioned native library.
     *
     * @param libname The name of the library you want to load. For libfoo.so this would be foo
     * @param version The version of libfoo. If you need to satisfy a dependency on libfoo.so.1 the version string is just '1'.
     * @throws UnsatisfiedLinkError if the lib can't be loaded.
     */
    public void loadLibWithVersion(String libname, String version) {
        // See if we can find the lib
        String nativeLibDir = mContext.getApplicationInfo().nativeLibraryDir;
        File srcLib = new File(nativeLibDir + File.separator + "lib" + libname + ".so");
        if (srcLib.exists()) {
            File libDir = new File(mContext.getFilesDir().getAbsolutePath() + File.separator + mLibLoaderDir);
            if (!libDir.exists()) {
                libDir.mkdir();
            }

            if (libDir.isDirectory()) {
                File versLib = new File(libDir, "lib" + libname + ".so." + version);
                try {
                    copyFile(srcLib, versLib);
                } catch (IOException e) {
                    throw new UnsatisfiedLinkError(libDir.toString()
                        + "Could not load lib" + libname + ".so." + version
                        + " due to IOException:\n" + e.getMessage());
                }
                System.load(versLib.getAbsolutePath());
            } else throw new UnsatisfiedLinkError(libDir.toString()
                    + " is not a directory! Could not load lib" + libname + ".so." + version);
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        InputStream srcStream = null;
        OutputStream dstStream = null;
        try {
            srcStream = new FileInputStream(src);
            dstStream = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = srcStream.read(buffer)) > 0) {
                dstStream.write(buffer, 0, length);
            }
        } finally {
            srcStream.close();
            dstStream.close();
        }
    }
}
