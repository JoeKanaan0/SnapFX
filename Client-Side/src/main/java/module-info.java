module ar.midtermproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires opencv;
    requires mysql.connector.java;

    opens ar.midtermproject to javafx.fxml;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.databind;
    exports ar.midtermproject;
    exports ar.midtermproject.model to com.fasterxml.jackson.databind;
    opens ar.midtermproject.model to com.fasterxml.jackson.databind;
    exports ar.midtermproject.dto to com.fasterxml.jackson.databind;

    opens ar.midtermproject.controllers to javafx.fxml;
    exports ar.midtermproject.controllers;

}