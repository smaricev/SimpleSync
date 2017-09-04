

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class gui_logincontrol implements Initializable {
    @FXML
    PasswordField psw = new PasswordField();
    @FXML
    TextField usr = new TextField();
    @FXML
    Label krivpass = new Label();
    public static Connection Veza;

    public gui_logincontrol() {
    }

    public void initialize(URL location, ResourceBundle resources) {
        gui_logincontrol.Bazalink a = new gui_logincontrol.Bazalink();
        a.start();
        this.krivpass.setVisible(false);
    }

    public static Connection spojinabazu() throws SQLException {
        String url = "jdbc:mysql://moops.ddns.net:3306/Synit";
        String username = "moops";
        String password = "ge54ck32o1";
        System.out.println("Connecting database...");
        Connection con = DriverManager.getConnection(url, username, password);
        System.out.println("Database connected!");
        return con;
    }

    public void textentered() {
        this.krivpass.setVisible(false);
    }

    public void loginButtonClicked(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(this.usr != null) {
            String username = this.usr.getText();
            String password = this.psw.getText();
            String query = "select username,password from korisnici where username = (?) and password = (?)";
            PreparedStatement prvi = Veza.prepareStatement(query);
            prvi.setString(1, username);
            prvi.setString(2, password);
            ResultSet rezultati = prvi.executeQuery();
            boolean ok = false;

            while(rezultati.next()) {
                String buser = rezultati.getString(1);
                String bpass = rezultati.getString(2);
                if(buser.equals(username) && password.equals(bpass)) {
                    ok = true;
                }
            }

            if(ok) {
                Stage app_stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                FXMLLoader a = new FXMLLoader();
                Pane b2 = (Pane)a.load(this.getClass().getResource("main.fxml").openStream());
                gui_maincontrol plz = (gui_maincontrol)a.getController();
                plz.setusername(username);
                Scene novi = new Scene(b2, 302.0D, 370.0D);
                app_stage.setScene(novi);
            }

            if(!ok) {
                this.krivpass.setVisible(true);
            }
        }

    }

    public void registerButtonClicked(ActionEvent event) throws IOException {
        Parent b1 = (Parent)FXMLLoader.load(this.getClass().getResource("register.fxml"));
        Scene one = new Scene(b1, 250.0D, 300.0D);
        Stage monitor = new Stage();
        monitor.setScene(one);
        monitor.show();
    }

    public class Bazalink extends Thread {
        private Thread A;

        public Bazalink() {
        }

        public void run() {
            System.out.println("Spajam se na bazu");

            try {
                gui_logincontrol.Veza = gui_logincontrol.spojinabazu();
            } catch (SQLException var3) {
                var3.printStackTrace();
            }

            try {
                sleep(600000L);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

        }

        public void start() {
            if(this.A == null) {
                this.A = new Thread(this);
                this.A.start();
            }

        }
    }
}
