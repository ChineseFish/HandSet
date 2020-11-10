package com.tongda.ccb_direct_bank.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.tongda.ccb_direct_bank.constant.Global;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wutw on 2018/1/11 0011.
 * 对文件处理的工具类
 */
public class FileUtils {
    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public static boolean checkSDCard() {
        final String status = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(status)) {
            return true;
        }
        return false;
    }

    /**
     * 创建app主目录
     *
     * @return boolean
     */
    public static boolean checkFileDirectory(Context context) {
        final File resDir = FileUtils.getDiskCacheDir(context);
        if (!resDir.exists()) {
            return resDir.mkdirs();
        }
        return true;
    }

    /**
     * 获取可以使用的缓存目录(默认目录名/itlanbaoApp/)
     * @param context
     * @return
     */
    public static File getDiskCacheDir(Context context) {
        return getDiskCacheDir(context, Global.RESOURCE_DIRECTORY);
    }

    /**
     * 获取可以使用的缓存目录
     * @param context
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = checkSDCard() ? getExternalCacheDir(context).getPath() : getAppCacheDir(context);

        File cacheDirFile = new File(cachePath);
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdirs();
        }

        return cacheDirFile;
    }

    /**
     * 获取程序外部的缓存目录
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        // 这个sd卡中文件路径下的内容会随着，程序卸载或者设置中清除缓存后一起清空
        final String cacheDir = context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() +File.separator+ cacheDir);
    }

    /**
     * 获取文件路径空间大小
     * @param path
     * @return
     */
    public static long getUsableSpace(File path) {
        try{
            final StatFs stats = new StatFs(path.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }



    /**
     * 获取安装在用户手机上的com.itlanbao.app下的files目录
     *
     * @return files path
     */
    public static String getAppFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取安装在用户手机上的appid下的cache目录
     *
     * @return cache path
     */
    public static String getAppCacheDir(Context context) {
        return context.getCacheDir().getPath();
    }

    /**
     * 从assets 文件夹中获取文件并读取数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName ) {
        String result = "";
        try {
            final InputStream in = context.getResources().getAssets()
                    .open(fileName);
            // 获取文件的字节数
            final int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);

            result = EncodingUtils.getString(buffer, "UTF-8");
            in.close();
            buffer = null;
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
          /*  if (LogUtils.DEBUG) {
                LogUtils.e(e);
            }*/
        }
        return result;
    }

    /**
     * 获取可以使用的缓存目录(默认目录名/itlanbaoApp/)
     * @param context
     * @return
     */
    public static File getTempFile(Context context,String fileName) {
        File dir = getDiskCacheDir(context);
        String filePath = dir.getAbsolutePath()+File.separator+fileName;
        File file = new File(filePath);
       /* if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return file;
    }
    /**
     * 获取后缀名
     *
     * @param path
     *            全路径
     * @return
     */
    public static String getFileExtName(String path) {
        String ext = "";
        if ((path != null) && (path.length() > 0)) {
            int dot = path.lastIndexOf('.');
            if ((dot > -1) && (dot < (path.length() - 1))) {
                ext = path.substring(dot + 1);
            }
        }
        return ext;
    }

    /**
     * 获取文件名
     *
     * @param path
     *            全路径
     * @return
     */
    public static String getFileName(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.substring(path.lastIndexOf(File.separator) + 1);
        }
        return "";
    }

    /**
     * 获取文件所在的文件路径
     *
     * @param path
     * @return
     */
    public static String getFilePath(String path) {
        return path.substring(0, path.lastIndexOf(File.separator) + 1);
    }
    /**
     * 获取文件所在的文件路径
     * @return
     */
    public static String getFilePath(File file) {
        String path = file.getAbsolutePath();
        return path.substring(0, path.lastIndexOf(File.separator) + 1);
    }
    /**
     * 获取目录文件个数
     *
     * @param f
     * @return
     */
    public static long getlist(File f) {
        long size = 0;
        try {
            File flist[] = f.listFiles();
            size = flist.length;
            for (int i = 0; i < flist.length; i++) {
                final File file = flist[i];
                if (file == null) {
                    continue;
                }
                if (file.isDirectory()) {
                    size = size + getlist(file);
                    size--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取文件夹下所有文件大小
     *
     * @param f
     * @return
     */
    public static long getFileSize(File f) {
        long size = 0;
        try {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                final File file = flist[i];
                if (file == null) {
                    continue;
                }
                if (file.isDirectory()) {
                    size = size + getFileSize(file);
                } else {
                    size = size + file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param file 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(File file) {
        if (file == null) {
            return "0B";
        }

        final long blockSize = getFileSize(file);

       /* if (LogUtils.DEBUG) {
            LogUtils.d("getAutoFileOrFilesSize 文件大小：" + blockSize);
        }*/

        return FormetFileSize(blockSize);
    }
    /**
     * 创建缓存文件夹
     */
   /* public static void initCacheFile(Context context) {

        if (LogUtils.DEBUG) {
            LogUtils.v("initCacheFile");
        }

        final String cacheDir = FileUtils.getDiskCacheDir(context).getAbsolutePath();

        final String imageDirPath = cacheDir + FileConstants.CACHE_IMAGE_DIR;
        final File imageFileDir = new File(imageDirPath);
        if (!imageFileDir.exists()) {
            boolean isOk = imageFileDir.mkdirs();
            if (LogUtils.DEBUG) {
                LogUtils.v(imageDirPath + " 文件夹创建isOk" + isOk);
            }
        }

        final String audioDirPath = cacheDir + FileConstants.CACHE_AUDIO_DIR;
        final File audioFileDir = new File(audioDirPath);
        if (!audioFileDir.exists()) {
            boolean isOk = audioFileDir.mkdirs();
            if (LogUtils.DEBUG) {
                LogUtils.v(audioDirPath + " 文件夹创建isOk" + isOk);
            }
        }

        final String messageDirPath = cacheDir + FileConstants.CACHE_MESSAGE_DIR;
        final File messageFileDir = new File(messageDirPath);
        if (!messageFileDir.exists()) {
            boolean isOk = messageFileDir.mkdirs();
            if (LogUtils.DEBUG) {
                LogUtils.v(imageDirPath + " 文件夹创建isOk" + isOk);
            }
        }
    }*/


    /**
     * 读取文件
     *
     * @param sFileName
     * @return
     */
    public static String readFile(String sFileName) {
        if (TextUtils.isEmpty(sFileName)) {
            return null;
        }

        final StringBuffer sDest = new StringBuffer();
        final File f = new File(sFileName);
        if (!f.exists()) {
            return null;
        }
        try {
            FileInputStream is = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
                String data = null;
                while ((data = br.readLine()) != null) {
                    sDest.append(data);
                }
            } catch (IOException ioex) {
               /* if (LogUtils.DEBUG) {
                    LogUtils.e(ioex);
                }*/
            } finally {
                is.close();
                is = null;
                br.close();
                br = null;
            }
        } catch (Exception ex) {
          /*  if (LogUtils.DEBUG) {
                LogUtils.e(ex);
            }*/
        } catch (OutOfMemoryError ex) {
           /* if (LogUtils.DEBUG) {
                ex.printStackTrace();
            }*/
        }
        return sDest.toString().trim();
    }


    /**
     * 保存文件
     *
     * @param content
     * @param fileName
     * @param isAppend
     * @return
     */
    public static boolean writeStringToFile(String content, String fileName, boolean isAppend) {
        return writeStringToFile(content, "", fileName, isAppend);
    }

    public static boolean writeStringToFile(String content,
                                            String directoryPath, String fileName, boolean isAppend) {
        if (!TextUtils.isEmpty(content)) {
            if (!TextUtils.isEmpty(directoryPath)) {// 是否需要创建新的目录
                final File threadListFile = new File(directoryPath);
                if (!threadListFile.exists()) {
                    threadListFile.mkdirs();
                }
            }
            boolean bFlag = false;
            final int iLen = content.length();
            final File file = new File(fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                final FileOutputStream fos = new FileOutputStream(file,
                        isAppend);
                byte[] buffer = new byte[iLen];
                try {
                    buffer = content.getBytes();
                    fos.write(buffer);
                    if (isAppend) {
                        fos.write("||".getBytes());
                    }
                    fos.flush();
                    bFlag = true;
                } catch (IOException ioex) {
                 /*   if (LogUtils.DEBUG) {
                        LogUtils.e(ioex);
                    }*/
                } finally {
                    fos.close();
                    buffer = null;
                }
            } catch (Exception ex) {
               /* if (LogUtils.DEBUG) {
                    LogUtils.e(ex);
                }*/
            } catch (OutOfMemoryError o) {
               /* if (LogUtils.DEBUG) {
                    o.printStackTrace();
                }*/
            }
            return bFlag;
        }
        return false;
    }

    /**
     * 将字符串写入到文本文件中
     *
     * @param strcontent
     */
    public static void writeFileSdcard(String strcontent) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        // 每次写入时，都换行写
        String strContent = "-------当前时间===" + str + "\r\n" + strcontent + "\r\n";
        try {
            String strFilePath = Environment.getExternalStorageDirectory() + "/lunxun.text";

            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File.");
        }
    }

    //把图片存入本地
    public static String writeImage2Cache(Context context, byte[] img, String imgName) {
        // 保存jpg图片
        String storageFolder = getAppCacheDir(context) + File.separator;


        String filePath =storageFolder + imgName + ".jpg";
        FileOutputStream fos = null;
        deleteFile(filePath);
        try {
            fos = new FileOutputStream(filePath);
            fos.write(img);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    //把图片存入本地
    public static String writeImage2Disk(Context context, byte[] img, String imgName) {
        // 保存jpg图片
        String storageFolder =FileUtils.getDiskCacheDir(context)
                + File.separator
                + "liveness"
                + File.separator;
        File livenessFolder = new File(storageFolder);
        if (!livenessFolder.exists()) {
            livenessFolder.mkdirs();
        }

        String filePath =storageFolder + imgName + ".jpg";
        FileOutputStream fos = null;
        deleteFile(filePath);
        try {
            fos = new FileOutputStream(filePath);
            fos.write(img);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    //把图片存入本地
    public static boolean writeBitmap2DiskCache(Context context, Bitmap bitmap, String path, String fileName) {
        boolean result = false;
        try {
            String storageFolder = getDiskCacheDir(context) + File.separator + path + File.separator ;
            File livenessFolder = new File(storageFolder);
            if (!livenessFolder.exists()) {
                livenessFolder.mkdirs();
            }
            String pathAndName = storageFolder+fileName+".jpg";

            deleteFile(pathAndName);

//            File file = new File(pathAndName);
//            file.createNewFile();

            FileOutputStream fos = context.openFileOutput(pathAndName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    //设置文件的超时时间
    public static int CACHE_TIME_PIC = 3 * 60 * 1000;
    //从本地获取图片
    public static Bitmap readBitmap2Cache(Context context, String fileName,boolean cacheTime) {
        Bitmap bitmap = null;
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) {
                return bitmap;
            } else if (cacheTime && System.currentTimeMillis() - file.lastModified() > CACHE_TIME_PIC) {
                return bitmap;
            }
            FileInputStream fis = context.openFileInput(fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;// 允许可清除
            options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
            bitmap = BitmapFactory.decodeStream(fis, null, options);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //根据id获取图片
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }
    /**
     * 重命名
     *
     * @param filePath
     * @return
     */
    public static boolean rename(String filePath, String newFilePath) {
       /* if (LogUtils.DEBUG) {
            LogUtils.e("filePath " + filePath);
            LogUtils.e("newFilePath " + newFilePath);
        }*/

        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            final File newFile = new File(newFilePath);
            if (file.exists()) {
                return file.renameTo(newFile);
            }
        }
        return false;
    }


    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
     /*   if (LogUtils.DEBUG) {
            LogUtils.e("deleteFile path " + filePath);
        }*/

        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
           /* if (LogUtils.DEBUG) {
                LogUtils.e("deleteFile path exists " + file.exists());
            }*/
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 删除文件夹下所有文件
     *
     * @return
     */
    public static void deleteDirectoryAllFile(String directoryPath) {
        final File file = new File(directoryPath);
        deleteDirectoryAllFile(file);
    }

    public static void deleteDirectoryAllFile(File file) {
        if (!file.exists()) {
            return;
        }

        boolean rslt = true;// 保存中间结果
        if (!(rslt = file.delete())) {// 先尝试直接删除
            // 若文件夹非空。枚举、递归删除里面内容
            final File subs[] = file.listFiles();
            final int size = subs.length - 1;
            for (int i = 0; i <= size; i++) {
                if (subs[i].isDirectory())
                    deleteDirectoryAllFile(subs[i]);// 递归删除子文件夹内容
                rslt = subs[i].delete();// 删除子文件夹本身
            }
            // rslt = file.delete();// 删除此文件夹本身
        }

        if (!rslt) {
         /*   if (LogUtils.DEBUG) {
                LogUtils.w("无法删除:" + file.getName());
            }*/
            return;
        }
    }

    /**
     * 根据后缀名删除文件
     *
     * @param delPath
     *            path of file
     * @param delEndName
     *            end name of file
     * @return boolean the result
     */
    public static boolean deleteEndFile(String delPath, String delEndName) {
        // param is null
        if (delPath == null || delEndName == null) {
            return false;
        }
        try {
            // create file
            final File file = new File(delPath);
            if (file != null) {
                if (file.isDirectory()) {
                    // file list
                    String[] fileList = file.list();
                    File delFile = null;

                    // digui
                    final int size = fileList.length;
                    for (int i = 0; i < size; i++) {
                        // create new file
                        delFile = new File(delPath + "/" + fileList[i]);
                        if (delFile != null && delFile.isFile()) {// 删除该文件夹下所有文件以delEndName为后缀的文件（不包含子文件夹里的文件）
                            // if (delFile != null) {//
                            // 删除该文件夹下所有文件以delEndName为后缀的文件（包含子文件夹里的文件）
                            deleteEndFile(delFile.toString(), delEndName);
                        } else {
                            // nothing
                        }
                    }
                } else if (file.isFile()) {

                    // check the end name
                    if (file.toString().contains(".")
                            && file.toString()
                            .substring(
                                    (file.toString().lastIndexOf(".") + 1))
                            .equals(delEndName)) {
                        // file delete
                        file.delete();
                    }
                }
            }
        } catch (Exception ex) {
          /*  if (LogUtils.DEBUG) {
                LogUtils.e(ex);
            }*/
            return false;
        }
        return true;
    }

    /**
     * 删除文件夹内所有文件
     *
     * @param delpath
     *            delpath path of file
     * @return boolean the result
     */
    public static boolean deleteAllFile(String delpath) {
        try {
            // create file
            final File file = new File(delpath);

            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {

                final String[] filelist = file.list();
                final int size = filelist.length;
                for (int i = 0; i < size; i++) {

                    // create new file
                    final File delfile = new File(delpath + "/" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        // digui
                        deleteFile(delpath + "/" + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (Exception ex) {
           /* if (LogUtils.DEBUG) {
                LogUtils.e(ex);
            }*/
            return false;
        }
        return true;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath
     *            被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {

        if (TextUtils.isEmpty(sPath)) {
            return false;
        }

        boolean flag;
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        final File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        final File[] files = dirFile.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                } // 删除子目录
                else {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                }
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 复制文件
     *
     * @param srcPath : 源文件全路径
     * @param destPath : 目标文件全路径
     * @return
     */
    public static long copyFile(String srcPath, String destPath) {
        try {
            int position = destPath.lastIndexOf(File.separator);
            String dir = destPath.substring(0, position);
            String newFileName = destPath.substring(position+1);
            final File cacheDir = new File(dir);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            return copyFile(new File(srcPath), new File(dir), newFileName);
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 复制文件(以超快的速度复制文件)
     *
     * @param srcFile
     *            源文件File
     * @param destDir
     *            目标目录File
     * @param newFileName
     *            新文件名
     * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
     */
    @SuppressWarnings("resource")
    public static long copyFile(final File srcFile, final File destDir, String newFileName) {
        long copySizes = 0;
        if (!srcFile.exists()) {
          /*  if (LogUtils.DEBUG) {
                LogUtils.d("源文件不存在");
            }*/
            copySizes = -1;
        } else if (!destDir.exists()) {
          /*  if (LogUtils.DEBUG) {
                LogUtils.d("目标目录不存在");
            }*/
            copySizes = -1;
        } else if (newFileName == null) {
        /*    if (LogUtils.DEBUG) {
                LogUtils.d("文件名为null");
            }*/
            copySizes = -1;
        } else {
            FileChannel fcin = null;
            FileChannel fcout = null;
            try {
                fcin = new FileInputStream(srcFile).getChannel();
                fcout = new FileOutputStream(new File(destDir, newFileName)).getChannel();
                long size = fcin.size();
                fcin.transferTo(0, fcin.size(), fcout);
                copySizes = size;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fcin != null) {
                        fcin.close();
                    }
                    if (fcout != null) {
                        fcout.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return copySizes;
    }


    /**
     * 判断asset下是否存在某个文件
     *
     * @param context
     * @param fileName
     *            如:aa.txt或image/aa.jpg
     * @return
     */
    public static boolean existInAsset(Context context, String fileName) {
        boolean exist = false;
        try {
            String[] u = context.getAssets().list(getFilePath(fileName));
            for (String item : u) {
                if (item.equals(getFileName(fileName))) {
                    exist = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exist;
    }


    /**
     * 空间大小单位格式化
     * @param size
     * @return
     */
    public static String formatSize(long size) {

// Formatter.formatFileSize()
        String suffix = null;
        float fSize=0;

        if (size >= 1024) {
            suffix = "KB";
            fSize=size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }

        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        final DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }



    /**
     * 质量压缩方法
     */
    public static byte[] compressBitmap(byte[] imageBytes,int maxSize){
        Bitmap inBitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
       return compressBitmap(inBitmap,maxSize);
    }

    /**
     * 质量压缩方法
     */
    public static byte[] compressBitmap( Bitmap inBitmap,int maxSize){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        inBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxSize) {

            int temopt = baos.toByteArray().length / 1024/maxSize;

            if (options <= 0) {
                break;
            }else if(temopt==1){
                options -= 5;
            }else
                options =2*100/temopt;
            //重置baos即清空baos
            baos.reset();
            inBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
      /*  // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);*/

        return baos.toByteArray();
    }

    /**
     * TODO 除法运算，保留小数
     * @author 袁忠明
     * @date 2018-4-17下午2:24:48
     * @param a 被除数
     * @param b 除数
     * @return 商
     */
    public static float txfloat(int a,int b) {
        // TODO 自动生成的方法存根

        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数

        return Float.parseFloat(df.format((float) a / b));
    }

    /**
     * Bitmap 转 字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.d("TAG:","angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
