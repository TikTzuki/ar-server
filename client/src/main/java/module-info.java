module org.override {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.apache.logging.log4j;
    requires lombok;
    requires com.google.gson;

    opens org.override to javafx.fxml;
    exports org.override;

    exports org.override.models;
    opens org.override.models to com.google.gson;

    exports org.override.controllers;
    opens org.override.controllers to javafx.fxml;

    exports org.override.core.models;
}