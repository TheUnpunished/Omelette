package xyz.unpunished.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import org.jdom2.Document;
import xyz.unpunished.util.DocumentManager;


public class OmeletteController implements Initializable {

    @FXML
    private AnchorPane objectBackground;
    @FXML
    private Slider scaleSlider;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private AnchorPane variablePane;
    @FXML
    private Accordion variableAccordion;
    
    private DocumentManager manager;
    private Document doc;
    private double sliderLastVal = 1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manager = new DocumentManager(objectBackground,
                variableAccordion, scaleSlider, variablePane);
        doc = manager.initFromFile(new File("qt_ch01_0010_00.xml"));
        scaleSlider.valueProperty().addListener((ov, t, t1) -> {
            double newVal;
                newVal = Math.round((double) t1 * 100) / 5 * 5 / 100.0;
                List<Node> nodes = objectBackground.getChildren();
                double val = 0.9 * newVal + 0.1;
                double prevVal = 0.9 * sliderLastVal + 0.1;
                objectBackground.setTranslateX(0 - (objectBackground.getWidth() 
                            - objectBackground.getWidth() * val) / 2);
                objectBackground.setTranslateY(0 - (objectBackground.getHeight()
                        - objectBackground.getHeight()* val) / 2);
                objectBackground.setScaleX(val);
                objectBackground.setScaleY(val);
                sliderLastVal = newVal;
                scaleSlider.setValue(newVal);
        });
        objectBackground.heightProperty().addListener((ov, t, t1) -> {
            double scaleVal = scaleSlider.getValue() * 0.9 + 0.1;
            objectBackground.setTranslateY(0 - (t1.doubleValue()
                            - t1.doubleValue() * scaleVal) / 2);
        });
        objectBackground.widthProperty().addListener((ov, t, t1) -> {
            double scaleVal = scaleSlider.getValue() * 0.9 + 0.1;
            objectBackground.setTranslateX(0 - (t1.doubleValue() 
                            - t1.doubleValue() * scaleVal) / 2);
        });
        mainSplitPane.getDividers().get(0).positionProperty().addListener((ov, t, t1) -> {
            double val = t1.doubleValue();
            val = val > 0.725 ? 0.725 : val < 0.25 ? 0.25 : val;
            mainSplitPane.getDividers().get(0).setPosition(val);
            if(mainSplitPane.getWidth() > 0){
                variablePane.setPrefWidth(mainSplitPane.getWidth() * (1.0 - val) - 14.5);
                variablePane.setMinWidth(mainSplitPane.getWidth() * (1.0 - val) - 14.5);
                variablePane.setMaxWidth(mainSplitPane.getWidth() * (1.0 - val) - 14.5);
            }
        });
    }    
    
}
