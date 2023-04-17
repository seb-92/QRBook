module com.example.libraryclienttcp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires lombok;


    opens com.example.libraryclienttcp to javafx.fxml;
    exports com.example.libraryclienttcp;
    exports com.example.libraryclienttcp.dto;
}