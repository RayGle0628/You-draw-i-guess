module reading {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;
    requires java.xml.crypto;
    opens client to javafx.fxml;
    exports client;
}