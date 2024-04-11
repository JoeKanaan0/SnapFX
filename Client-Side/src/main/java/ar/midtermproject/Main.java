package ar.midtermproject;

import ar.midtermproject.model.User;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 Main Application. This class handles navigation and user session.
 */
public class Main extends Application {

    private User loggedUser;
    private ApplicationSwitcher application;

    /**
     @param args the command line arguments
     */
    public static void main(String[] args) {
        // Launch the application
        Application.launch(Main.class, (java.lang.String[])null);
    }

    /**
     Starts the application by setting the stage's width and height and calling loadLoginPage method.
     @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        application = new ApplicationSwitcher();
        application.setMain(this);
        primaryStage.setWidth(1066);
        primaryStage.setHeight(600);
        application.setStage(primaryStage);
        application.loadLoginPage();
    }

    public User getUser() {
        return loggedUser;
    }


    public void setUser(User user) {
        loggedUser = user;
    }

    public void setUserFromJSON(String jsonResponse) {
        loggedUser = User.FromJson(jsonResponse);
    }

    /**
     Logs the user out and loads the login page.
     */
    public void userLogout(){
        loggedUser = null;
        application.loadLoginPage();
    }
}