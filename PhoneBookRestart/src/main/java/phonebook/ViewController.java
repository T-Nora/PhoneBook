package phonebook;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

   
    //FXML annotációk:
    @FXML
    TableView table;
    @FXML
    TextField inputLastName;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    SplitPane mainSplit;
    @FXML
    AnchorPane anchor;
    @FXML
    TextField inputExportName;
    @FXML
    Button exportButton;

    DB db = new DB();

   

    private final String MENU_CONTACTS = "Kontaktok";
    private final String CONTACTS_LIST = "Lista";
    private final String CONTACTS_EXPORT = "Exportálás";
    private final String MENU_EXIT = "Kilépés";


    private  final ObservableList<Person> data =
            FXCollections.observableArrayList(
//                    new Person("Szabó", "Gyula", "sz.gy@test.com"),
//                    new Person("Bourne", "Jason", "bj.gy@test.com"),
//                    new Person("Scott", "Michael", "sm.gy@test.com")
            );
   

    @FXML
    private void addContact(ActionEvent event){
       
        String email = inputEmail.getText();
        if(email.length()>3 && email.contains("@") && email.contains(".")) {
            Person newPerson = new Person(inputLastName.getText(), inputFirstName.getText(), email);
            data.add(newPerson);
            db.addContacts(newPerson);
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        }else{
            alert("A kontakt hozzáadásához adj meg egy valódi email címet!");
        }
        
    }

    @FXML
    private void exportList(ActionEvent actionEvent){
        
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+", "");
        if(fileName != null && !fileName.equals("")) {
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
        } else{
            alert("Az exportáláshoz adj meg egy fájlnevet!");
        }
    }
   
    public void setTableData(){
        

        TableColumn lastNameCol = new TableColumn("Vezetéknév");

        lastNameCol.setMinWidth(130);

        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));

        lastNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> cellEditEvent) {
                Person actualPerson = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
                actualPerson.setLastName(cellEditEvent.getNewValue());
                db.updateContacts(actualPerson);
          
            }
        });


      
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        
        firstNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> cellEditEvent) {
                Person actualPerson = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
                actualPerson.setFirstName(cellEditEvent.getNewValue());
                db.updateContacts(actualPerson);}});

        TableColumn emailCol = new TableColumn("Email cím");
        emailCol.setMinWidth(200);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

      
        emailCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> cellEditEvent) {
                Person actualPerson = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
                actualPerson.setEmail(cellEditEvent.getNewValue());
                db.updateContacts(actualPerson);}});

       
        TableColumn removeCol = new TableColumn("Törlés");
        removeCol.setMinWidth(100);

        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory=
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
                    @Override
                    public TableCell<Person, String> call(final TableColumn<Person, String> param) {
                        final TableCell<Person, String> cell = new TableCell<Person, String>(){
                            final Button button = new Button("Törlés");
                            @Override
                            public void updateItem(String item, boolean empty){
                                super.updateItem(item, empty);
                                if(empty){
                                    setGraphic(null);
                                    setText(null);
                                }else{
                                    button.setOnAction((ActionEvent event)->{
                                        Person person = getTableView().getItems().get(getIndex());
                                        data.remove(person);
                                        db.removeContacts(person);
                                    });
                                    setGraphic(button);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
            removeCol.setCellFactory(cellFactory);

      
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol);

        
        data.addAll(db.getAllContacts());
        
        table.setItems(data);


    }

  
    private void setMenuData() {
       
        TreeItem<String> treeItemRoot = new TreeItem<>("Menü");
       
        TreeView<String> treeView = new TreeView<>(treeItemRoot);
      
        treeView.setShowRoot(false);
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);

     
            //nodeItemA.setExpanded(true);

        ClassLoader classLoader = getClass().getClassLoader();
        String contactImageUrl = classLoader.getResource("Images/contactmenu.png").toExternalForm();
        Node contactsNode  = new ImageView(new Image(contactImageUrl));
        String exportImageUrl = classLoader.getResource("Images/exporticonmenu.png").toExternalForm();
        Node exportNode = new ImageView(new Image(exportImageUrl));

        
        TreeItem<String> nodeItemA1 = new TreeItem<>(CONTACTS_LIST, contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(CONTACTS_EXPORT, exportNode);

       
        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);

       
        treeItemRoot.getChildren().addAll(nodeItemA, nodeItemB);

       
        menuPane.getChildren().add(treeView);


        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue  observableValue, Object oldValue, Object newValue) {
               
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
              
                String selectedMenu;
                selectedMenu = selectedItem.getValue();

                if(selectedMenu != null){
                    switch (selectedMenu){
                        case MENU_CONTACTS:
                            try{
                                selectedItem.setExpanded(true);
                            }catch (Exception exception){}
                            break;
                        case CONTACTS_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case CONTACTS_EXPORT:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;

                        case MENU_EXIT:
                            
                            System.exit(0);
                            break;
                    }
                }
            }
        });

    }

 
    private void alert(String text){
      
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);

        Label label = new Label(text);
        Button alertButton = new Button("OK");
        alertButton.setTextAlignment(TextAlignment.CENTER);
      
        VBox vBox = new VBox(label, alertButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(Color.TEAL, CornerRadii.EMPTY, Insets.EMPTY)));


        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vBox.setVisible(false);
            }
        });


        
        anchor.getChildren().add(vBox);
        anchor.setTopAnchor(vBox, 300.0);
        anchor.setLeftAnchor(vBox, 200.0);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableData();
        setMenuData();


    }

}
