//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package datagram;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class datagram implements Serializable {
    public File file;
    public String Md5checksum;

    public datagram() {
    }

    public static datagram stvoriopisnik(File opisnik) throws NoSuchAlgorithmException, IOException {
        if(!opisnik.exists()) {
            return null;
        } else if(opisnik.isDirectory()) {
            return null;
        } else {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            datagram info = new datagram();
            String Md5 = getFileChecksum(md5Digest, opisnik);
            info.file = opisnik;
            info.Md5checksum = Md5;
            return info;
        }
    }

    public static datagram[] stvoriop(File[] podacidir) {
        if(podacidir == null) {
            return null;
        } else {
            datagram[] lista = new datagram[podacidir.length];

            for(int i = 0; i < lista.length; ++i) {
                try {
                    lista[i] = stvoriopisnik(podacidir[i]);
                } catch (NoSuchAlgorithmException var4) {
                    var4.printStackTrace();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }
            }

            return lista;
        }
    }

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        if(!file.exists()) {
            return null;
        } else {
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            boolean bytesCount = false;

            int var10;
            while((var10 = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, var10);
            }

            fis.close();
            byte[] fileime = file.getName().getBytes("UTF-8");
            byte[] digested = digest.digest(fileime);
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();

            int i;
            for(i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toString((bytes[i] & 255) + 256, 16).substring(1));
            }

            for(i = 0; i < digested.length; ++i) {
                sb.append(Integer.toString((digested[i] & 255) + 256, 16).substring(1));
            }

            return sb.toString();
        }
    }

    public static datagram[] opisnik(DataOutputStream izlaz, ObjectInputStream ulaz) throws IOException, ClassNotFoundException {
        izlaz.writeUTF("pull requestkek");
        datagram[] opisnik = (datagram[])((datagram[])ulaz.readObject());
        izlaz.flush();
        return opisnik;
    }
}
