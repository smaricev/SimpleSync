

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class gui_registercontrol implements Initializable {
    @FXML
    TextField usr = new TextField();
    @FXML
    PasswordField pass = new PasswordField();
    @FXML
    TextField email = new TextField();
    @FXML
    Label greetings = new Label();
    @FXML
    Label warning = new Label();
    @FXML
    Label uspjesno = new Label();
    public static Connection Veza;

    public gui_registercontrol() {
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

    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater( () -> greetings.requestFocus() );
        this.warning.setVisible(false);
        this.uspjesno.setVisible(false);
        gui_registercontrol.Bazalink a = new gui_registercontrol.Bazalink();
        a.start();
    }

    public void ontype() {
        this.warning.setVisible(false);
        this.uspjesno.setVisible(false);
    }

    public void btn1onclick() throws SQLException {
        String username = this.usr.getText();
        String password = this.pass.getText();
        String mail = this.email.getText();
        if(password.length() > 5) {
            String query = "select username from korisnici where username = (?)";
            PreparedStatement prvi = Veza.prepareStatement(query);
            prvi.setString(1, username);
            ResultSet rezultati = prvi.executeQuery();
            boolean ok = false;

            while(rezultati.next()) {
                String buser = rezultati.getString(1);
                if(buser.equals(username)) {
                    ok = true;
                }
            }

            if(!ok) {
                query = "insert into korisnici(username,password) values(?,?) ";
                PreparedStatement drugi = Veza.prepareStatement(query);
                drugi.setString(1, username);
                drugi.setString(2, password);
                drugi.execute();
                System.out.println("Unos u bazu je k");
                this.uspjesno.setVisible(true);
            }

            if(ok) {
                this.warning.setText("Username is taken");
                this.warning.setVisible(true);
            }
        } else {
            this.warning.setText("password needs to be 6 or more chars");
            this.warning.setVisible(true);
        }

    }

    public class Bazalink extends Thread {
        private Thread A;

        public Bazalink() {
        }

        public void run() {
            System.out.println("Spajam se na bazu");

            try {
                gui_registercontrol.Veza = gui_registercontrol.spojinabazu();
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
