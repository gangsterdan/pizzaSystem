/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;
import java.sql.*;
import Model.Client;
import Model.Pizza;
import java.util.ArrayList;

/**
 *
 * @author daniel
 * <p>
 * Class to control the interaction with our sqlite database.
 */
public class DBController {
    
    //String pointing to the DB file
    public String url;
    
    //Constructor
    public DBController(){
        //Note, this string is different if we run on windows.
        //url = "jdbc:sqlite:/home/daniel/NetBeansProjects/pizzaSystem/src/DBFiles/pizzaTestDB.db"; //
        
        //url = "jdbc:sqlite:C:/databases/mydatabase.db"; // Windows
        
        url = "jdbc:sqlite:/Users/danielespinosa/Documents/Computacion/Semestre 9/Ingenieria de Software/programacion proyecto/pizzaSystem/src/DBFiles/pizzaTestDB.db"; // MAC
    }
    
    //overloaded constructor to pass a custom path to the database.
    public DBController(String url){
        this.url = url;
    }
    
    //Method to check if we have connection to the DataBase.
    public boolean testConnect() {
        boolean res = false;
        Connection conn = null;
        
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
            res = true; //We indicate that the connection was succesful
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return res;
    }
    
    //Method to establish and return a Connection object.
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    
    //Method to verify that a user is in the system. return true if the user and password combination exists.
    public boolean verifyUser(String name, String password){
        boolean res = false;
        
        String sql = "SELECT * FROM user where name = '" + name + "' and password = '" + password +"';";
        
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                res = true; //We get thrown a value, therefore the user exists.
                
                System.out.println("Verification of: " + rs.getInt("id") +  "\t" + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }
    
    
    public boolean addClient(String name, String address, String phone){
        boolean res = true;

        String sql = "INSERT INTO CLIENT (NAME, ADDRESS, PHONE) VALUES(?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res = false;
        }
        return res;
    }
    
    //Method to select all from all clients and return the ResultSet of the query
    public ResultSet selectAllClients(){
        String sql = "SELECT * FROM client;";
        
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            return rs;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        
    }
    
     //Method to test the feasability of uncomplicating my life and using JTable's following constructor: JTable(Object[][] rowData, Object[] columnNames)
    public Object[][] selectAllClientsEASY(){
        int rowNum = 0;
        int columnNum = 4; //Using a known value
        Object[][] res;
        
        String sqlCount = "select count('id') as num from client;";
        String sql = "SELECT * FROM client;";
        
        //TryCatch section to get the number of clients that will be returned in the query.
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlCount)){
            
            rowNum = rs.getInt("num"); //Get the number of clients that will be returned.
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        res = new Object[rowNum][columnNum];
        
        
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            int currentRow = 0;
            // loop through the result set
            while (rs.next()) {
                
                for(int i = 1; i <= columnNum; i++){
                    res[currentRow][i-1] = rs.getObject(i);
                }
                currentRow++;
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        
        return res;
    }
    
    
    //Method to return a client queried from the db by its ID.
    public Client selectClientByID(int id){
        Client client = null;
        String sql = "Select * from client where id = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                client = new Client(id, rs.getString("name"), rs.getString("address"), rs.getString("phone"));
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        return client;
    }
    
    //Method to update a client's information given the new client object (obviously having the proper ID)
    public boolean updateClient(Client client){
        boolean res = true;

        String sql = "UPDATE client SET name = ?, address = ?,  phone = ? WHERE id = ?";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getAddress());
            pstmt.setString(3, client.getPhone());
            pstmt.setInt(4, client.getUid());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res = false;
        }
        return res;
    }
    
    
    //Method to delete a client given his ID.
    public boolean deleteClientByID(int id){
        boolean res = true;

        String sql = "DELETE from client WHERE id = ?";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res = false;
        }
        return res;
    
    
    }
    
        //Method to return a client queried from the db by its Phone.
    public Client selectClientByPhone(String phone){
        Client client = null;
        String sql = "Select * from client where phone = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                client = new Client(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("phone"));
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        return client;
    }
    
    
    //Method to test the feasability of uncomplicating my life and using JTable's following constructor: JTable(Object[][] rowData, Object[] columnNames)
    public Object[][] selectAllPizzas(){
        int rowNum = 0;
        int columnNum = 4; //Using a known value
        Object[][] res;
        
        String sqlCount = "select count('id') as num from pizza;";
        String sql = "SELECT * FROM pizza;";
        
        //TryCatch section to get the number of clients that will be returned in the query.
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlCount)){
            
            rowNum = rs.getInt("num"); //Get the number of clients that will be returned.
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        res = new Object[rowNum][columnNum];
        
        
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            int currentRow = 0;
            // loop through the result set
            while (rs.next()) {
                
                for(int i = 1; i <= columnNum; i++){
                    res[currentRow][i-1] = rs.getObject(i);
                }
                currentRow++;
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        
        return res;
    }
    
    //Method to return a pizza queried from the db by its ID.
    public Pizza selectPizzaByID(int id){
        Pizza pizza = null;
        String sql = "Select * from pizza where id = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                pizza = new Pizza(id, rs.getString("name"), rs.getString("size").charAt(0), rs.getDouble("price"));
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        return pizza;
    }
    
    
    public boolean addOrder(int clientId, double time, int completed, double cost){
        boolean res = true;

        String sql = "INSERT INTO ORDEN (CLIENT_ID, TIME, COMPLETED, COST) VALUES(?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            pstmt.setDouble(2, time);
            pstmt.setInt(3, completed);
            pstmt.setDouble(4, cost);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res = false;
        }
        return res;
    }
    
    public boolean addOrderedPizza(int orderId, int pizzaId){
        boolean res = true;

        String sql = "INSERT INTO ORDERED (ORDEN_ID, PIZZA_ID) VALUES(?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, pizzaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            res = false;
        }
        return res;
    }
    
    public int selectMaxOrderID(){
        int res = 0;
        String sql = "select max(id) as max from orden";

        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            res = rs.getInt("max"); //Get the number of clients that will be returned.
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1; //Watch out, we're returning -1 if we had an SQLException!
        }
        return res;
    }
    
    public Object[][] selectAllOrders(){
        int rowNum = 0;
        int columnNum = 5; //Using a known value
        Object[][] res;
        
        String sqlCount = "select count('id') as num from orden;";
        String sql = "SELECT * FROM orden;";
        
        //TryCatch section to get the number of orders that will be returned in the query.
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlCount)){
            
            rowNum = rs.getInt("num"); //Get the number of orders  that will be returned.
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        res = new Object[rowNum][columnNum];
        
        
        try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            int currentRow = 0;
            // loop through the result set
            while (rs.next()) {
                
                for(int i = 1; i <= columnNum; i++){
                    res[currentRow][i-1] = rs.getObject(i);
                }
                currentRow++;
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //Watch out, we're returning null if we had an SQLException!
        }
        
        return res;
    }
    
}
