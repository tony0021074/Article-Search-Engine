package userinterface;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.basex.core.BaseXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import javafx.scene.control.*;
import storage.Database;

public class Controller {

    @FXML
    private TreeView<String> stringTreeView;

    @FXML
    private ChoiceBox<String> stringChoiceBox;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    private Database database;

    private ObservableList<String> choiceBoxStringList;

    @FXML
    private void initialize(){
        try {
            this.database = new Database();
            TreeItem<String> root = parseXMLToTreeItem(database.getXMLString());
            this.stringTreeView.setRoot(root);
            this.choiceBoxStringList = FXCollections.observableArrayList();
            this.stringChoiceBox.setItems(this.choiceBoxStringList);
            for (String field: this.database.getXMLElements()) {
                this.choiceBoxStringList.add(field);
            }
            for (String field: this.database.getXMLAttributes()) {
                this.choiceBoxStringList.add(field);
            }

        } catch (BaseXException e) {
            e.printStackTrace();
            Platform.exit();
        } catch (SAXException e) {
            e.printStackTrace();
            Platform.exit();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    @FXML
    private void startSearch() {
        String field = this.stringChoiceBox.getValue();
        String inputText = this.inputTextField.getText();
        try {
            if ((!field.isEmpty()) && field.length() > 0 && (!inputText.isEmpty()) && inputText.length() > 0) {
                TreeItem<String> root = parseXMLToTreeItem(this.database.getXMLString(field, inputText));
                this.stringTreeView.setRoot(root);
            } else if (((field.isEmpty()) || field.length() == 0) && ((inputText.isEmpty()) || inputText.length() == 0)) {
                TreeItem<String> root = parseXMLToTreeItem(this.database.getXMLString());
                this.stringTreeView.setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    @FXML
    private void clearSearch() {
        this.stringChoiceBox.setValue("");
        this.inputTextField.setText("");
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
    }

    private void setAllControlDisable(boolean value) {
        this.stringChoiceBox.setDisable(value);
        this.inputTextField.setDisable(value);
        this.searchButton.setDisable(value);
        this.clearButton.setDisable(value);
        this.stringTreeView.setDisable(value);
    }

    private static class TreeItemCreationContentHandler extends DefaultHandler {

        private TreeItem<String> item = new TreeItem<>();

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            // finish this node by going back to the parent
            this.item = this.item.getParent();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // start a new node and use it as the current item
            if(attributes.getLength()>0) {
                qName = qName + " ("+attributes.getLocalName(0)+" : "+attributes.getValue(0)+")";
            }
            TreeItem<String> item = new TreeItem<>(qName);
            item.setExpanded(true);
            this.item.getChildren().add(item);
            this.item = item;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String s = String.valueOf(ch, start, length).trim();
            if (!s.isEmpty()) {
                // add text content as new child
                this.item.setValue(this.item.getValue()+" : "+s);
                //this.item.getChildren().add(new TreeItem<>(s));
            }
        }
    }

    public static TreeItem<String> parseXMLToTreeItem(String xmlString) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        TreeItemCreationContentHandler contentHandler = new TreeItemCreationContentHandler();

        // parse file using the content handler to create a TreeItem representation
        reader.setContentHandler(contentHandler);
        reader.parse(new InputSource(new StringReader(xmlString)));

        // use first child as root (the TreeItem initially created does not contain data from the file)
        TreeItem<String> item = contentHandler.item.getChildren().get(0);
        contentHandler.item.getChildren().clear();
        return item;
    }

    @FXML
    private void handleTreeViewClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        }
    }
}
