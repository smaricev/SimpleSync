

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class gui_maincontrol implements Initializable {
    @FXML
    Label a = new Label();
    @FXML
    TextField dirpath = new TextField();
    @FXML
    Label warning = new Label();
    @FXML
    Label usern = new Label();
    @FXML
    ProgressBar bar1 = new ProgressBar(0.0D);
    @FXML
    Label filename = new Label();
    @FXML
    ImageView logo = new ImageView();
    Datasync prog;
    DirectoryChooser izbordir;
    String dirpathstring;
    static String username;
    Image av;

    public gui_maincontrol() {
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.av = new Image("logocrv.png");
        this.logo.setImage(this.av);
        this.warning.setVisible(false);
        this.prog = new Datasync();
        Datasync.run = false;
        this.a.setText("Not running");
        this.izbordir = new DirectoryChooser();
        this.dirpathstring = "C:\\Simplesync";
        this.dirpath.setPromptText(this.dirpathstring);
        this.filename.setVisible(false);
        this.bar1.setVisible(false);
    }

    public void setusername(String us1) {
        username = us1;
        this.usern.setText(us1);
    }

    public void pokreniclick(ActionEvent event) throws IOException, ClassNotFoundException {
        Datasync.username1 = username;
        Datasync.home = this.dirpathstring;
        Datasync.run = true;
        this.a.setText("Running");
        a.setContentDisplay(ContentDisplay.TOP);
        this.av = new Image("logo.png");
        this.logo.setImage(this.av);
        this.prog.start(this.bar1, this.a, this.filename);
    }

    public void openfilepath() throws IOException {
        File file = new File (dirpathstring+"\\");
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);


    }

    public void zaustaviclick() throws InterruptedException, IOException {
        this.a.setText("Aplikacija se izvodi");
        this.av = new Image("logocrv.png");
        this.logo.setImage(this.av);
        this.warning.setVisible(false);
        this.bar1.setVisible(false);
        this.filename.setVisible(false);
        Datasync.pokrenuto = false;
        Datasync.run = false;
        if(Datasync.klijent!= null){
                Datasync.klijent.close();
        }
        this.prog.join();
        try {
            prog.fos.close();
            prog.fis1.close();
        }catch(Exception e){
            System.out.println("User stoped the file download/upload");
        }
        this.a.setText("Aplikacija se ne izvodi");
    }

    public void logoutclick(ActionEvent event) throws IOException, InterruptedException {
        this.zaustaviclick();
        Stage glavni = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent a = FXMLLoader.load(this.getClass().getResource("login.fxml"));
        Scene novi = new Scene(a, 213,296);
        glavni.setScene(novi);
        if(Datasync.klijent != null){
            Datasync.klijent.close();}
    }

    public void choosedir() {
        if(!Datasync.run) {
            Stage novi = new Stage();
            File file = this.izbordir.showDialog(novi);
            if(file != null) {
                this.dirpathstring = file.getAbsolutePath();
                this.dirpath.setPromptText(this.dirpathstring);
                Datasync.pokrenuto = false;
            }
        }

        if(Datasync.run) {
            this.warning.setVisible(true);
        }

    }
}
