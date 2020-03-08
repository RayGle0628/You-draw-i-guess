module reading {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    opens client to javafx.fxml;
    exports client;
}