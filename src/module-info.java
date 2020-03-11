module reading {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;
    requires org.postgresql.jdbc;
    opens client to javafx.fxml;
    exports client;
}