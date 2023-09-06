package xyz.unpunished.util;

import com.sun.javafx.application.PlatformImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import xyz.unpunished.model.Delta;
import xyz.unpunished.model.Tile;

@Getter
@Setter
public class DocumentManager {
    
    private ArrayList<Tile> tiles = new ArrayList();
    private AnchorPane backgroundPane;
    private Accordion variableAccordion;
    private Slider scaleSlider;
    private AnchorPane variablePane;
    
    public DocumentManager(AnchorPane backgroundPane,
            Accordion variableAccordion, Slider scaleSlider, AnchorPane variablePane){
        this.backgroundPane = backgroundPane;
        this.scaleSlider = scaleSlider;
        this.variableAccordion = variableAccordion;
        this.variablePane = variablePane;
    }
    
    public void initManager(){
        
    }
    
    public Document initFromFile(File file){
        // make a temporary file that's flat
        Document document = readXml(file);
        flattenXML(document);
        document = readXml(new File("temp/flat.xml"));
        // get object list
        Element root = document.getRootElement();
        root = root.getChildren().get(0);
        for(Element element: root.getChildren()){
            Delta dragDelta = new Delta(0.0, 0.0);
            AnchorPane pane = new AnchorPane();
            
            TableView<Element> tableView = new TableView();
            TableColumn<Element, String> nameColumn 
                    = new TableColumn<>("Name");
            nameColumn.setCellValueFactory((p) -> {
                return new SimpleStringProperty(
                        p.getValue().getName());
            });
            nameColumn.setPrefWidth(100.0);
            nameColumn.setMaxWidth(100.0);
            TableColumn<Element, String> valueColumn 
                    = new TableColumn<>("Value (type)");
            valueColumn.setCellValueFactory((p) -> {
                return new SimpleStringProperty(
                        p.getValue().getValue() 
                        + " (" + p.getValue()
                                .getAttributeValue("type") + ")");
            });
            valueColumn.setPrefWidth(100.0);
            valueColumn.setMaxWidth(100.0);
            tableView.setEditable(false);
            tableView.getColumns().clear();
            tableView.getColumns().add(nameColumn);
            tableView.getColumns().add(valueColumn);
            tableView.getItems().addAll(element.getChildren());
            tableView.prefHeightProperty().bind(Bindings
                    .size(tableView.getItems()).multiply(24.025).add(27));
            tableView.setPrefWidth(202.0);
            tableView.setOnMouseClicked((t) -> {
                variableAccordion.getPanes().clear();
                // object stuff first
                {
                    AnchorPane aPane = new AnchorPane();
                    GridPane gPane = new GridPane();
                    gPane.setVgap(5.0);
                    gPane.setHgap(2.5);
                    AnchorPane.setLeftAnchor(gPane, -5.0);
                    AnchorPane.setRightAnchor(gPane, -5.0);
                    AnchorPane.setTopAnchor(gPane, -3.0);
                    AnchorPane.setBottomAnchor(gPane, 0.0);
                    int i = 0;
                    for(Attribute attr: element.getAttributes()){
                        TextField attrNameField 
                                = new TextField(attr.getName());
                        attrNameField.textProperty()
                                .addListener((ov, oan, nan) -> {
                            attr.setName(nan);
                        });
                        TextField attrValueField 
                                = new TextField(attr.getValue());
                        attrValueField.textProperty()
                                .addListener((ov, oav, nav) -> {
                            attr.setValue(nav);
                        });
                        attrNameField.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.65));
                        attrNameField.setMaxWidth(250);
                        attrNameField.setAlignment(Pos.CENTER_RIGHT);
                        GridPane.setHalignment(attrNameField, HPos.RIGHT);
                        GridPane.setValignment(attrNameField, VPos.CENTER);
                        attrValueField.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.9));
                        attrValueField.setAlignment(Pos.CENTER_LEFT);
                        GridPane.setHalignment(attrValueField, HPos.RIGHT);
                        GridPane.setValignment(attrValueField, VPos.CENTER);
                        gPane.add(attrNameField, 0, i);
                        gPane.add(attrValueField, 1, i++);
                    }
                    aPane.getChildren().add(gPane);
                    TitledPane attrTPane = new TitledPane("object", aPane);
                    variableAccordion.getPanes().add(attrTPane);
                }
                // now onto all elements inside of object
                for(Element child: element.getChildren()){
                    AnchorPane aPane = new AnchorPane();
                    GridPane gPane = new GridPane();
                    TitledPane elemTPane 
                            = new TitledPane(child.getName(),
                            aPane);
                    gPane.setVgap(5.0);
                    gPane.setHgap(2.5);
                    AnchorPane.setLeftAnchor(gPane, -5.0);
                    AnchorPane.setRightAnchor(gPane, -5.0);
                    AnchorPane.setTopAnchor(gPane, -3.0);
                    AnchorPane.setBottomAnchor(gPane, 0.0);
                    Label nameLabel = new Label();
                    nameLabel.setText("name");
                    nameLabel.setPrefWidth(Integer.MAX_VALUE);
                    nameLabel.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.65));
                    nameLabel.setMaxWidth(250);
                    nameLabel.setAlignment(Pos.CENTER_RIGHT);
                    GridPane.setHalignment(nameLabel, HPos.RIGHT);
                    GridPane.setValignment(nameLabel, VPos.CENTER);
                    TextField nameField = new TextField(child.getName());
                    nameField.textProperty().addListener((ov, oan, nan) -> {
                        child.setName(nan);
                        elemTPane.setText(nan);
                    });
                    nameField.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.9));
                    nameField.setAlignment(Pos.CENTER_LEFT);
                    GridPane.setHalignment(nameField, HPos.RIGHT);
                    GridPane.setValignment(nameField, VPos.CENTER);
                    gPane.add(nameLabel, 0, 0);
                    gPane.add(nameField, 1, 0);
                    int i = 1;
                    for(Attribute attr: child.getAttributes()){
                        TextField attrNameField 
                                = new TextField(attr.getName());
                        attrNameField.textProperty()
                                .addListener((ov, oan, nan) -> {
                            attr.setName(nan);
                        });
                        TextField attrValueField 
                                = new TextField(attr.getValue());
                        attrValueField.textProperty()
                                .addListener((ov, oav, nav) -> {
                            attr.setValue(nav);
                        });
                        attrNameField.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.65));
                        attrNameField.setMaxWidth(250);
                        attrNameField.setAlignment(Pos.CENTER_RIGHT);
                        GridPane.setHalignment(attrNameField, HPos.RIGHT);
                        GridPane.setValignment(attrNameField, VPos.CENTER);
                        attrValueField.prefWidthProperty()
                                .bind(variablePane.prefWidthProperty()
                                .multiply(0.9));
                        attrValueField.setAlignment(Pos.CENTER_LEFT);
                        GridPane.setHalignment(attrValueField, HPos.RIGHT);
                        GridPane.setValignment(attrValueField, VPos.CENTER);
                        gPane.add(attrNameField, 0, i);
                        gPane.add(attrValueField, 1, i++);
                    }
                    aPane.getChildren().add(gPane);
                    variableAccordion.getPanes().add(elemTPane);
                }
            });
            pane.getChildren().add(tableView);
            pane.setPrefSize(0, 0);
            pane.minWidthProperty().bind(tableView.prefWidthProperty());
            pane.minHeightProperty().bind(tableView.prefHeightProperty());
            TitledPane tPane = new TitledPane(element
                    .getAttributeValue("name"),
                pane);
            tPane.setOnMousePressed((MouseEvent mouseEvent) -> {
                // record a delta distance for the drag and drop operation
                double scaleVal = scaleSlider.getValue() * 0.9 + 0.1;
                dragDelta.setX(tPane.getLayoutX() - mouseEvent.getSceneX() / scaleVal);
                dragDelta.setY(tPane.getLayoutY() - mouseEvent.getSceneY() / scaleVal);
                tPane.setCursor(Cursor.MOVE);
            });
            tPane.setOnMouseReleased((MouseEvent mouseEvent) -> {
                tPane.setCursor(Cursor.HAND);
                for(Tile tile: tiles)
                    tile.getAttachedPane().setDisable(false);
            });
            tPane.setOnMouseDragged((MouseEvent mouseEvent) -> {
                double scaleVal = scaleSlider.getValue() * 0.9 + 0.1;
                tPane.setLayoutX(mouseEvent.getSceneX() / scaleVal + dragDelta.getX());
                tPane.setLayoutY(mouseEvent.getSceneY() / scaleVal + dragDelta.getY());
                tPane.setCursor(Cursor.MOVE);
                for(Tile tile: tiles)
                    tile.getAttachedPane().setDisable(true);
            });
            tPane.setOnMouseEntered((MouseEvent mouseEvent) -> {
                tPane.setCursor(Cursor.HAND);
            });
            addTileToPane(new Tile(element, tPane));
        }
        return document;
    }
    
    public void addTileToPane(Tile tile){
        tiles.add(tile);
        backgroundPane.getChildren().add(tile.getAttachedPane());
    }
    
    public void removeTile(){
        
    }
    
    private void flattenXML(Document document){
        try{
            Format format = Format.getCompactFormat();
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(document,
                    new FileWriter("temp/flat.xml"));
        }
        catch (IOException ioe){
            
        }
    }
    
    private Document readXml(File file){
        try{
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            return doc;
        }
        catch(JDOMException jde){
            
        }
        catch(IOException ioe){
            
        }
        return null;
    }
}
