package xyz.unpunished;

import java.io.IOException;
import javafx.application.Application;
import org.dom4j.DocumentException;
import org.jdom2.JDOMException;
import xyz.unpunished.util.JavaFXApp;

public class Omelette {

    public static void main(String[] args) throws DocumentException, JDOMException, IOException {
        Application.launch(JavaFXApp.class, args);
    }
}
