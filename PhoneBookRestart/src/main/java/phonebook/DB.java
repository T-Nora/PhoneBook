package phonebook;

import java.sql.*;
import java.util.ArrayList;

//Ez a modell része az applikációnak

public class DB {
    final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    final String URL ="jdbc:derby:sampleDB;create=true";

    //Külső adatbázis esetén meg kellene adni. Ez most egy beágyazott adatbázis, ezért nem szükséges!
    final String USERNAME = "";
    final String PASSWORD = "";

    Connection conn = null;
    Statement createStatement =null;
    DatabaseMetaData dbmd = null;

    public DB() {
        //Megpróbáljuk életre kelteni az adatbázist
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("A híd létrejött!");
        } catch (SQLException ex) {
            System.out.println("Valami baj van a híd létrehozásakor!");
            System.out.println("" + ex);
        }

        //Ha életre kelt, csinálunk egy megpakolható teherautót
        if (conn != null) {
            try {
                createStatement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("Valami baj van a createStatement (teherautó) létrehozásakor!");
                System.out.println("" + ex);
            }
        }

        //Megnézzük, hogy üres-e az adatbázis, megnézzük létezik-e az adott adattábla
        try {
            dbmd = conn.getMetaData();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a DatabaseMetaData (adatbázis leírása) létrehozásakor!");
            System.out.println("" + ex);
        }
        try {
            ResultSet rs1 = dbmd.getTables("null", "APP", "CONTACTS", null);
            if(!rs1.next()){
                //Automatikusan növekvő id létrehozása
                createStatement.execute("create table contacts (id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), lastname varchar(20), firstname varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van az adattábla létrehozásakor!");
            System.out.println("" + ex);
        }
    }

    //Kikérünk minden kontaktot
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ArrayList<Person> persons = null;
        try {
            ResultSet resultSet = createStatement.executeQuery(sql);
            persons = new ArrayList<>();
            while(resultSet.next()){
                Person actualPerson = new Person(resultSet.getInt("id"), resultSet.getString("lastname"), resultSet.getString("firstname"), resultSet.getString("email"));
                persons.add(actualPerson);
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van!");
            System.out.println("" + ex);
        }
        return persons;
    }

    //Új kontaktok hozzáadása
    public void addContacts(Person person){
        try{
            String sql = "insert into contacts (lastname, firstname, email) values (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        }catch (SQLException exception){
            System.out.println("Probléma történt a contact hozzáadásakor!");
            System.out.println(""+exception);
        }

    }

    //Kontaktok törlése
    public void removeContacts(Person person){
        try{
            String sql = "delete from contacts where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        }catch (SQLException exception){
            System.out.println("Probléma történt a contact törlésekor!");
            System.out.println(""+exception);
        }

    }

    //Felhasználó módosítása. Mivel megkell mondani azt is, hogy hol történjen a a módosítás, ezért kell ID-t is létrehozni a Personnek.
    public void updateContacts(Person person) {
        try {
            String sql = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException exception) {
            System.out.println("Probléma történt a contact frissítésekor!");
            System.out.println("" + exception);
        }
    }
}
