
import datagram.datagram;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ss_server {
    static String home = "/home/stjepan/Synit/";

    public ss_server() {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Simplesync verzija 2.4\n");
        short port = 6933;
        ServerSocket glavni = new ServerSocket(port);
        System.out.println("Cekam klijenta");

        while(true) {
            Socket klijent = glavni.accept();
            ss_server.komunikacija dretva = new ss_server.komunikacija(klijent);
            dretva.start();
        }
    }


    private static class komunikacija extends Thread {
        static String username;
        private Thread T;
        private Socket carapa;
        private static Path putanja;
        private String putserv;
        private static File dir;

        komunikacija(Socket c) {
            StringBuffer sbuf = new StringBuffer();
            sbuf.append(ss_server.home);
            sbuf.append(username);
            this.putserv = sbuf.toString();
            putanja = Paths.get(sbuf.toString(), new String[0]);
            this.carapa = c;
            dir = new File(this.putserv);
        }

        public void download(DataInputStream dis, DataOutputStream izl, ObjectOutputStream izlaz, BufferedOutputStream bos, ObjectInputStream ulaz) throws IOException, InterruptedException, ClassNotFoundException {

            String ulazi = dis.readUTF();
            File[] n;
            if(ulazi.equals("rename")) {
                System.out.println("renaming file");
                datagram buf = (datagram)ulaz.readObject();
                n = sadrzaj_datoteke();
                datagram[] duljinapod = datagram.stvoriop(n);

                for(int i = 0; i < duljinapod.length; ++i) {
                    if(duljinapod[i].Md5checksum.equals(buf.Md5checksum)) {
                        System.out.println(duljinapod[i].file.getName());
                        System.out.println(buf.file.getName());
                        Path imepod = Paths.get(duljinapod[i].file.getAbsolutePath(), new String[0]);
                        Files.move(imepod, imepod.resolveSibling(buf.file.getName()), new CopyOption[0]);
                    }
                }
            }

            if(ulazi.equals("daj neke fileove")){
                n = sadrzaj_datoteke();
                datagram[] server = datagram.stvoriop(n);
                System.out.println("daj neke failove");
                datagram download = (datagram) ulaz.readObject();
                for(int i = 0 ;i< server.length;i++){
                    if(server[i].Md5checksum.equals(download.Md5checksum)){
                        izl.writeLong(server[i].file.length());
                        FileInputStream fis = new FileInputStream(server[i].file);
                        int var10;
                        byte[] buf = new byte[4092];
                        while((var10 = fis.read(buf)) != -1) {
                            izl.write(buf, 0, var10);
                            izl.flush();
                        }
                    }
                }






            }



            if(ulazi.equals("pocetak")) {
                username = dis.readUTF();
                StringBuffer var13 = new StringBuffer();
                var13.append(ss_server.home);
                var13.append(username);
                putanja = Paths.get(var13.toString(), new String[0]);
                this.putserv = var13.toString();
                dir = new File(this.putserv);
                System.out.println("Welcome " + username);
                if(!Files.exists(putanja, new LinkOption[0])) {
                    File var16 = new File(this.putserv);
                    var16.mkdir();
                }
            }

            if(ulazi.equals("visak")) {
                String var14 = dis.readUTF();
                System.out.println("visak -" + var14);
                Path var18 = Paths.get(this.putserv + "/" + var14, new String[0]);
                Files.delete(var18);
            }

            if(ulazi.equals("pull requestkek")) {
                datagram[] var15 = datagram.stvoriop(sadrzaj_datoteke());
                izlaz.writeObject(var15);
            }

            if(ulazi.equals("daj file")) {
                System.out.print("daj file");
                podaci_datoteke(bos, izl, izlaz);
            }

            if(ulazi.equals("upload file")) {
                byte[] var17 = new byte[4092];
                System.out.println("upload file");
                long var20 = dis.readLong();
                String var21 = dis.readUTF();
                System.out.println(var21 + " - " + var20);
                int var19;
                for(FileOutputStream fos = new FileOutputStream(this.putserv + "/" + var21); var20 > 0L && (var19 = dis.read(var17, 0, (int)Math.min((long)var17.length, var20))) != -1; var20 -= (long)var19) {
                    fos.write(var17, 0, var19);
                    fos.flush();
                }
            }
        }

        public void run() {
            try {
                System.out.println("Socket povezan na - " + this.carapa.getInetAddress().getHostAddress());
                DataInputStream dis = new DataInputStream(this.carapa.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(this.carapa.getOutputStream());
                DataOutputStream out2 = new DataOutputStream(this.carapa.getOutputStream());
                BufferedOutputStream bos = new BufferedOutputStream(this.carapa.getOutputStream());
                ObjectInputStream ulaz = new ObjectInputStream(this.carapa.getInputStream());

                while(true) {
                    this.download(dis, out2, out, bos, ulaz);
                }
            } catch (Exception var6) {
                ;
            }
        }

        public void start() {
            if(this.T == null) {
                this.T = new Thread(this);
                this.T.start();
            }

        }

        private static File[] sadrzaj_datoteke(boolean opcija,datagram[] client) {

            File[] sadrzaj = dir.listFiles();
            return sadrzaj;

        }
        private static File[] sadrzaj_datoteke() {

            File[] sadrzaj = dir.listFiles();
            return sadrzaj;

        }

        private static void podaci_datoteke(BufferedOutputStream bos, DataOutputStream izlaz, ObjectOutputStream oizlaz) throws IOException {
            izlaz.writeInt(sadrzaj_datoteke().length);
            File[] listapod = sadrzaj_datoteke();
            boolean n = false;
            byte[] buf = new byte[4092];

            for(int i = 0; i < listapod.length; ++i) {
                long lenght = listapod[i].length();
                izlaz.writeLong(lenght);
                FileInputStream fis = new FileInputStream(listapod[i]);

                int var10;
                while((var10 = fis.read(buf)) != -1) {
                    izlaz.write(buf, 0, var10);
                    izlaz.flush();
                }
            }
            System.out.println("podaci poslani");
        }
    }
}
