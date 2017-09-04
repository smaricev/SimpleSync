import datagram.datagram;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Datasync extends Thread{


    Thread T;
    static String username1;
    static String home;
    static Socket klijent = null;
    static boolean run = true;
    static boolean pokrenuto = false;
    static ProgressBar progress = null;
    static Label glavni,filename=null;
    static FileOutputStream fos;
    static FileInputStream fis1;


    private static void download(datagram server,ObjectOutputStream oos,DataOutputStream dos,DataInputStream dis) throws IOException {
        dos.writeUTF("daj neke fileove");
        System.out.println("downloading " + server.file.getName() + server.file.getAbsolutePath());
        oos.writeObject(server);
        Long duljinapod = dis.readLong();
        Long dpod = duljinapod;
        System.out.println(duljinapod);
        int n = 0;
        byte[] buf = new byte[4092];
        System.out.println(home);
        String servername =  server.file.getName();
        final long poddulj = duljinapod;
        Platform.runLater(() -> glavni.setText("DLing missing files"));
        Platform.runLater(() -> filename.setText(servername + "-" + poddulj / 1000000 + "MB"));
        Platform.runLater(() -> filename.setVisible(true));
        fos = new FileOutputStream(home + "\\" + server.file.getName());
        progress.setVisible(true);
        while (duljinapod > 0 && (n = dis.read(buf, 0, (int) Math.min(buf.length, duljinapod))) != -1) {
            fos.write(buf, 0, n);
            fos.flush();
            duljinapod -= n;
            double omjer = (double) duljinapod / (double) dpod;
            omjer = -(omjer - 1);
            progress.setProgress(omjer);
        }
        fos.close();
        System.out.println("prosao ovo");

    }






    private static void visak(DataOutputStream izlaz,datagram server) throws IOException {
        String ime = server.file.getName();
        izlaz.writeUTF("visak");
        izlaz.writeUTF(ime);
        System.out.println("Deleting : "+ ime);
        progress.setVisible(false);
        Platform.runLater(() -> glavni.setText("Deleting excess files"));
        Platform.runLater(()->filename.setText(ime));
        filename.setVisible(true);

    }

    private static void initialdl(DataOutputStream izlaz, datagram[] server, File sync, DataInputStream dis,ObjectInputStream ulaz,ObjectOutputStream izlaz2,Path path,boolean fulldl) throws IOException, ClassNotFoundException {
        pokrenuto = true;
        System.out.println("initial dl");
        server = datagram.opisnik(izlaz,ulaz);
        if(fulldl) {
            if (!Files.exists(path)) {
                sync.mkdir();
            }
            izlaz.writeUTF("daj file");
            int duljina = dis.readInt();
            System.out.print("Initialdownload " + "\n" + "broj podataka u datoteci: " + duljina + "\n");
            int n = 0;
            byte[] buf = new byte[4092];
            System.out.println(home);
            for (int i = 0; i < duljina; i++) {
                long duljinapod = dis.readLong();
                long dpod = duljinapod;
                Platform.runLater(() -> glavni.setText("Initial download: "));
                System.out.println(server[i].file.getName() + "-" + server[i].Md5checksum + " - " + duljinapod + " bytes");
                final String servername, MD5;
                servername = server[i].file.getName();
                MD5 = server[i].Md5checksum;
                final long poddulj = duljinapod;
                Platform.runLater(() -> filename.setText(servername + "-" + poddulj / 1000000 + "MB"));
                Platform.runLater(() -> filename.setVisible(true));
                fos = new FileOutputStream(home + "\\" + server[i].file.getName());
                progress.setVisible(true);
                while (duljinapod > 0 && (n = dis.read(buf, 0, (int) Math.min(buf.length, duljinapod))) != -1) {
                    fos.write(buf, 0, n);
                    fos.flush();
                    duljinapod -= n;
                    double omjer = (double) duljinapod / (double) dpod;
                    omjer = -(omjer - 1);
                    progress.setProgress(omjer);
                }
                fos.close();
            }}
        if(!fulldl){
            boolean provjera;
            datagram[] client = datagram.stvoriop(sync.listFiles());
            for(int i = 0; i<server.length;i++){
                provjera = false;
                for(int j = 0; j< client.length;j++){
                    if(server[i].Md5checksum.equals(client[j].Md5checksum)){
                        provjera = true;
                    }
                }
                if(!provjera){
                    try {
                        System.out.println("downloading "  + server[i].file.getName());
                        download(server[i], izlaz2, izlaz, dis);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }




        }
            Platform.runLater(() -> glavni.setText("Download finished"));



    }

    private static void difference(datagram[] client,datagram[] server,DataOutputStream izlaz2, ObjectOutputStream izlaz, ObjectInputStream ulaz, String mode) throws IOException, ClassNotFoundException {
        boolean exist;

        if(mode.equals("server")) {
            for (int i = 0; i < server.length; i++) {
                exist = false;
                for (int j = 0; j < client.length; j++) {

                    if (server[i].Md5checksum.equals(client[j].Md5checksum)) {
                        exist = true;
                    }
                }
                if (!exist) {
                    visak(izlaz2, server[i]);
                }
            }

        }
        if(mode.equals("home")){
            boolean usaojednom = false;
            for(int i = 0; i< client.length;i++){
                exist = false;
                for(int j = 0; j< server.length;j++){
                    if (client[i].Md5checksum.equals(server[j].Md5checksum)) {
                        exist = true;
                    }
                 }
                if (!exist) {
                    usaojednom = true;
                    upload(izlaz2,client[i].file,client[i]);
                }

            }
            if(usaojednom)Platform.runLater(()->glavni.setText("Uploaded"));
        }

    }

    static public void upload(DataOutputStream izlaz,File file,datagram client) throws IOException {
        int n = 0;
        byte[] bufer = new byte[4096];
        izlaz.writeUTF("upload file");
        File upload = file;
        System.out.println("upload file - "+upload.getName()+" - " + client.Md5checksum);
        Platform.runLater(() -> glavni.setText("Uploading"));
        long duljina = upload.length();
        String ime = upload.getName();
        izlaz.writeLong(duljina);
        izlaz.writeUTF(ime);
        final String servername;
        servername = client.file.getName();
        final long poddulj = duljina;
        Platform.runLater(() -> filename.setText(servername + "-" + poddulj/1000000 + " MB"));
        filename.setVisible(true);
        progress.setVisible(true);
        long sum = 0;
        fis1 = new FileInputStream(upload);
        while ((n = fis1.read(bufer)) != -1) {
            izlaz.write(bufer, 0, n);
            izlaz.flush();
            sum += n;
            progress.setProgress((double)sum/(double)duljina);
        }
        fis1.close();
    }



    public void run(){
        DataOutputStream izlaz=null;
        ObjectInputStream ulaz=null;
        DataInputStream ulaz2=null;
        ObjectOutputStream izlaz2=null;
        try{
            klijent = new Socket("moops.ddns.net", 6933);
            izlaz = new DataOutputStream(klijent.getOutputStream());
            ulaz = new ObjectInputStream(klijent.getInputStream());
            ulaz2 = new DataInputStream(klijent.getInputStream());
            izlaz2 = new ObjectOutputStream(klijent.getOutputStream());

        }catch(Exception e){};
        File sync = new File(home);
        File[] podacidir = sync.listFiles();
        datagram[] a1= datagram.stvoriop(podacidir);


        System.out.println("Simplesync v 1.0 ");
        while(username1 == null)System.out.println("waiting");
        try {
            izlaz.writeUTF("pocetak");
            izlaz.writeUTF(username1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true && run) {
            try {
                if(klijent.isClosed())return;
                syndir(izlaz2,izlaz, ulaz, ulaz2,sync);
            } catch (IOException e){
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("tu sam rodo");
            }
        }
        if(run==false) {
            System.out.println("Zaustavio");
            T.interrupt();
            T = null;
        }
    }
    public void start(ProgressBar a,Label b,Label c){
        progress = a;
        glavni = b;
        filename = c;
        if (T == null)
        {
            T = new Thread(this);
            T.start ();
        }
        run=true;


    }




    private static void syndir(ObjectOutputStream izlaz2,DataOutputStream izlaz, ObjectInputStream ulaz,DataInputStream dis,File sync) throws IOException, ClassNotFoundException {

        try {
            
            Path path = Paths.get(home);
            datagram[] server = null;
            datagram[] client = null;
            if (!pokrenuto && !Files.exists(path)) {
               initialdl(izlaz,server,sync,dis,ulaz,izlaz2,path,true);
            }
            if (!pokrenuto && Files.exists(path)){
                initialdl(izlaz,server,sync,dis,ulaz,izlaz2,path,false);
            }


            if(!sync.isDirectory())return;
            File[] home = sync.listFiles();
            client = datagram.stvoriop(home);
            server = datagram.opisnik(izlaz,ulaz);
            difference(client,server,izlaz,izlaz2,ulaz,"server");
            difference(client,server,izlaz,izlaz2,ulaz,"home");



        }catch(Exception e){

        }



    }
}