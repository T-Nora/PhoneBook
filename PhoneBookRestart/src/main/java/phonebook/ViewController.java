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

    //1. lépés: Behivatkozzuk az fxml-ben megadott eszközöket annotációval ellátva.
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

    //Mivel a menük nevét sokszor használjuk érdemes kihozni osztályváltozóknak

    private final String MENU_CONTACTS = "Kontaktok";
    private final String CONTACTS_LIST = "Lista";
    private final String CONTACTS_EXPORT = "Exportálás";
    private final String MENU_EXIT = "Kilépés";


    //2. lépés: Létrehozunk egy observable listet--> Mivel még nincs adatbázishoz kötve a projekt, így ezzel
    //tudunk mintaadatokat adni a projektnek. Amit tárolunk benne, annak az osztályváltozói jelennek meg
    // az oszlopokban.
    //Ehhez létrehozzuk a Person POJO-t is.
    //Az adatbázis létrehozása előtt benne kell hagyni a kamu adatokat, az adatbázis létrehozása után kikommenteltem, mert már nem kell.
    private  final ObservableList<Person> data =
            FXCollections.observableArrayList(
//                    new Person("Szabó", "Gyula", "sz.gy@test.com"),
//                    new Person("Bourne", "Jason", "bj.gy@test.com"),
//                    new Person("Scott", "Michael", "sm.gy@test.com")
            );
    /*
    6. lépés A gomboknak funkciót kell adni, az fxml fájlhoz kötjük itt az fxml annotációval, ott az onaction paranccsal
    Az addContact metódusban, mivel új személyeket akarunk hozzáadni, ezért a data-ban létrehozunk egy új Persont, de most
    nem konkrét értékekkel, hanem a getText metódus segítségével ki tudjuk venni a TextFieldekből az oda beírt adatokat, azt
    adjuk a konstruktorába.
     */

    @FXML
    private void addContact(ActionEvent event){
        //Az emailt validálni kell, egyébként regexel, de most egy egyszerű módon csináljuk. Ezért kell kiemelni változónak az emailt.
        String email = inputEmail.getText();
        if(email.length()>3 && email.contains("@") && email.contains(".")) {
            Person newPerson = new Person(inputLastName.getText(), inputFirstName.getText(), email);
            data.add(newPerson);
            //Az adatbázis létrehozása után az alábbi sorra is szükség van, hogy  lehessen adatot
            //felvinni az adatbázisba.
            db.addContacts(newPerson);
            //Mivel a fenti sor önmagában hozzáad adatot, de a TextFieldben benne hagyja az előző értéket, mintha újból azt
            //akarnánk hozzáadni. Ezért van szükség a clear parancsra.
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        }else{
            alert("A kontakt hozzáadásához adj meg egy valódi email címet!");
        }
        //Else ágra lehetne pop up ablakot adni fejlesztésként!
    }

    @FXML
    private void exportList(ActionEvent actionEvent){
        //Kivesszük a felhasználó által beírt file nevet, valamint kicsrélünk benne minden üres helyet, hogy ne lehessen space
        //a fájlnév
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+", "");
        if(fileName != null && !fileName.equals("")) {
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
        } else{
            alert("Az exportáláshoz adj meg egy fájlnevet!");
        }
    }
    /*
    3-4. lépések
    Oszlopok hozzáadása, alapérték megjelenítése oszlopokban, eseménykezelő! Mivel az adatok a table-höz tartoznak, ezért ebben a metódusban
    adjuk meg az adatbázist a létrehozása után. (Az nem a 3-4. lépés része volt!) Ezzel a sorral kötjük hozzá az alkalmazást az adatbázishoz.
     */
    public void setTableData(){
        //3. lépés:Létrehozzuk az oszlopokat, de ettől még nem szerepel a táblán!

        TableColumn lastNameCol = new TableColumn("Vezetéknév");

        //Ezzel megakadályozzuk, hogy eltűnjön az oszlop kicsinyítéskor!

        lastNameCol.setMinWidth(130);

        // Beállítjuk, hogy ebben az oszlopban minden cellának TextField legyen a tartalma.
        //A textfield szerkeszthető, ha label lenne, azt nem lehetne szerkeszteni.

        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        //Már eleve adunk értéket a textfieldnek (setCellValueFactory):
        //Létrehozunk egy új objektumot(PropertyValueFactory), amiben Person-t adunk át ("ebbe keresd"), és a , után megmondjuk milyen értéket veszünk ki
        //a POJO-ból jelen esetben stringet. A paraméterben megadjuk milyen néven találja a keresett értéket.
        //A SimpleStringProperty miatt tudja összekötni a stringgel!

        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));

        /*
        A cella szerkeszthetőségét az fxml-ben kell beállítani ediatble= true-val.
        4.lépés: Miután létrehozzuk az oszlopokat, és szerkeszthetővé állítjuk, nem menti el az adatot. Belekattintásra mindig z eredetit hozza
        vissza. Ez azért van, mert így csak a nézetet szerkesztjük, magát az objektumot nem. Ez adatbázisnál is nagy gond lenne.Ennek a kezelésére
        alkalmazható az EventHandler!
        Commoit-elküld: setoneditcommit: Abban a pillanatban, hogy valaki szerkeszti a cellát, és ELKÜLDI-->paraméterként átadunk egy eseménykezelőt.
        (Mire figyeljen:-CellEditEvent- ha valaki szerkeszti a cellát(persont és stringet figyeljen)), az eseménykezelőben pedig megmondjuk
        mi történjen. Az event egy olyan objektum ami tartalmazza a régi és az új értéket is.
        Kérjük az adott eseménykor a táblázat össze elemét, az adott pozíció, adott sorát.
        Az adatbázishoz is hozzákötjük, hogy lehessen frissíteni az adatotkat az id alapján.
         */
        lastNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> cellEditEvent) {
                Person actualPerson = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
                actualPerson.setLastName(cellEditEvent.getNewValue());
                db.updateContacts(actualPerson);
           /* Ha ezt a sort hozzáadjuk akkor még megtudhatjuk mi volt a korábbi érték. Ha nem írjuk oda és kilépünk az eseménykezelőből,
           akkor a régi érték elveszett.
           Ezt az adatbázisnak kell lekezelnie általában, nem pedig a programnak. (A nézet nem foglalkozik a modellel vagy a kontrollerrel.)
           Az adatbázisnak kell biztonsági mentést készítenie nem a programnak.
            cellEditEvent.getOldValue();
            */
            }
        });


        //A többi oszlop létrehozása
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        //4. lépés eventhandler
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

        //4. lépés eventhandler
        emailCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> cellEditEvent) {
                Person actualPerson = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
                actualPerson.setEmail(cellEditEvent.getNewValue());
                db.updateContacts(actualPerson);}});

        //Fejlesztésként törlésgombot adunk hozzá egy új oszlopban
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

        //A fenti oszlopokat hozzáadjuk a táblához, hogy meg is jelenjenek rajta!
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol);

        //Ha már van adatbázis akkor szükséges az alábbi sor, anélkül csak az utána következőre volt szükség.
        data.addAll(db.getAllContacts());
        //Az adatokat is hozzáadjuk a táblához!
        table.setItems(data);


    }

    /*
    5. lépés: TreeView_Fa szerkezet- ahogy a menük általában megjelennek
    Node létrehozása kép beilleztéséhez PNG kép praktikus, mert a háttérbe bele tud olvadni, független tőle.
    Menü létrehozása
     */
    private void setMenuData() {
        //Főelem = gyökérelem, ehhez kell később mindent hozzáadni!
        TreeItem<String> treeItemRoot = new TreeItem<>("Menü");
        //A főág létrehozása, ennek viszont már rögtön meg kell adni legalább 1 elemet! Ezért van az előző sor.
        //1 db TreeView van, arra kell rátenni az összes többi TreeItemet.
        TreeView<String> treeView = new TreeView<>(treeItemRoot);
        //Nem szeretnénk, hogy kiírja, hogy "Menü", csak az alatta lévő dolgok
        treeView.setShowRoot(false);
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);

        //A menü alapból csukva van itt beállítjuk, hogy alapból  nyitva legyen. Mivel a menüben erre állítunk be funkciót
        //ezért ez nem kell, de be lehetne állítani.
            //nodeItemA.setExpanded(true);

        //Kép hozzáadása, ezelőtt a metódus többi része, már meg van írva, a menük létre vannek hozva.
        //A getclass.getresourceasstream parancsokkal adjuk meg az elérsi utat. Az ImageView kiterjeszti a Node-ot.
        // Az első két parancs a videóból van Maven-ben valamiért nem működik, csak az alatta lévő kód.
      // Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contactmenu.png")));
     //   Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/exportikonmenü.png")));
        ClassLoader classLoader = getClass().getClassLoader();
        String contactImageUrl = classLoader.getResource("Images/contactmenu.png").toExternalForm();
        Node contactsNode  = new ImageView(new Image(contactImageUrl));
        String exportImageUrl = classLoader.getResource("Images/exporticonmenu.png").toExternalForm();
        Node exportNode = new ImageView(new Image(exportImageUrl));

        //Almenük létrehozása. A contacts és exportNode utólag lett hozzáadva a konstruktorhoz, így lehet képet rendelni az
        //almenükhöz.
        TreeItem<String> nodeItemA1 = new TreeItem<>(CONTACTS_LIST, contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(CONTACTS_EXPORT, exportNode);

        //Mielőtt az a1 és a2-t is rátesszük a gyökérre, azelőtt rá kell tenni az a-ra!
        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);

        //A fa elem gyökéreleméhez adjuk hozzá az újabb elemeket, tehát a fa többfelé ágazik.
        treeItemRoot.getChildren().addAll(nodeItemA, nodeItemB);

        //Ennél is, a létrehozástól még nem jelenik meg a felületen, ezért mindig hozzá kell adni amit meg szeretnénk jeleníteni.
        // A fát rátesszük magára a pane-re. A fa áll treeviewból, ami a törzse, és ágakból -treeitem.
        menuPane.getChildren().add(treeView);

        /*
        Az evenhandlert elindítja valami, egy konkrét történés pl egy gombra kattintás, a listener folyamatosan figyel a változásra. Ha változást
        észlel, akkor rögtön elindul.
        Azt állítjuk itt be, hogy elég legyen csak egyszer klikkelni a menüre, a kinyitáshoz/összecsukáshoz, ne duplán kelljen,
        mint alaphelyzetben. Illetve a menüpontok működését.
         */

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue  observableValue, Object oldValue, Object newValue) {
                //Ez lesz az amire a felhasználó rákattint
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                //Eltároljuk a kiválasztott menü nevét (értékét), vagyis valahányszor a felhasználó egy menüpontra kattint
                //meghívódik a listener és elmenti egy változóba a kiválasztott menü nevét. Az alapján, hogy melyik menüpontra
                //kattintott létrehozunk egy swichet (lehetne if is kevés menünél.)
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
                            //Általánosan használható kilépéshez
                            System.exit(0);
                            break;
                    }
                }
            }
        });

    }

    /*
    Utolsó lépések: fejlesztések a már elkészült programba, pop-up ablak
    Ehhez a metódushoz először az fxml fájlban kell id-t adni az anchor és a split paneknek, majd beannotáni őket ide.
     */
    private void alert(String text){
        //Ha hiba van, akkor a főpane-en nem engedünk kattintani és a láthatóságát levesszük.
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);

        Label label = new Label(text);
        Button alertButton = new Button("OK");
        alertButton.setTextAlignment(TextAlignment.CENTER);
        //Egymás alá teszi az elemeket
        VBox vBox = new VBox(label, alertButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(Color.TEAL, CornerRadii.EMPTY, Insets.EMPTY)));


        /*
        Eseménykezelőt kétféleképpen lehet hozzáadni, vagy az fxml egyik elemével összekötni, vagy az adott gombra
        meghívjuk a setOnAction nevű parancsot.
         */
        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vBox.setVisible(false);
            }
        });


        //Ez a sor önmagában bal felső sarokba helyezi, az alatta levő sorokkal ezen változtatunk.
        anchor.getChildren().add(vBox);
        anchor.setTopAnchor(vBox, 300.0);
        anchor.setLeftAnchor(vBox, 200.0);

    }

    //Az initalize metódus amikor a program lefut, akkor minden olyan kód lefut, ami a törzsében definiálva van.
    //Érdemes metódusokat létrehozni a logikailag összefüggő egységeknek, és itt már csak az adott metódust meghívni -->áttekinthetőbb!
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableData();
        setMenuData();


    }

}
