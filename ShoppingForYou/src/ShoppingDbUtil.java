import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ShoppingDbUtil {

	private static ShoppingDbUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/shopping_tracker";
	
	public static ShoppingDbUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new ShoppingDbUtil();
		}
		
		return instance;
	}
	
	private ShoppingDbUtil() throws Exception {		
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		
		return theDataSource;
	}
		
	public List<Customer> getCustomers() throws Exception {

		List<Customer> customers = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = getConnection();

			String sql = "select * from customers order by Last_Name";

			myStmt = myConn.createStatement();

			myRs = myStmt.executeQuery(sql);

			// process result set
			while (myRs.next()) {
				
				// retrieve data from result set row
				int id = myRs.getInt("Customer_ID");
				String firstName = myRs.getString("First_Name");
				String lastName = myRs.getString("Last_Name");
				String email = myRs.getString("Email_Address");
				String password = myRs.getString("Password");
				int phoneNumber = myRs.getInt("Phone_Number");
				
				// create new customer object
				Customer tempCustomer = new Customer(id, firstName, lastName,
						email, password, phoneNumber);

				// add it to the list of customers
				customers.add(tempCustomer);
			}
			
			return customers;		
		}
		finally {
			close (myConn, myStmt, myRs);
		}
	}

	public void addCustomer(Customer theCustomer) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "insert into customers (Customer_ID, First_Name, Last_Name, Email_Address, Password, Phone_Number) values (?, ?, ?, ?, ?, ?)";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, theCustomer.getId());
			myStmt.setString(2, theCustomer.getFirstName());
			myStmt.setString(3, theCustomer.getLastName());
			myStmt.setString(4, theCustomer.getEmail());
			myStmt.setString(5, theCustomer.getPassword());
			myStmt.setInt(6, theCustomer.getPhoneNumber());
						
			myStmt.execute();			
		}
		finally {
			close (myConn, myStmt);
		}
		
	}
	
	public Customer getCustomer(int customerId) throws Exception {
	
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = getConnection();

			String sql = "select * from customers where Customer_ID = ?";

			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, customerId);
			
			myRs = myStmt.executeQuery();

			Customer theCustomer = null;
			
			// retrieve data from result set row
			if (myRs.next()) {
				int id = myRs.getInt("Customer_ID");
				String firstName = myRs.getString("First_Name");
				String lastName = myRs.getString("Last_Name");
				String email = myRs.getString("Email_Address");
				String password = myRs.getString("Password");
				int phoneNumber = myRs.getInt("Phone_Number");
				
				theCustomer = new Customer(id, firstName, lastName,
						email, password, phoneNumber);
			}
			else {
				throw new Exception("Could not find customer id: " + customerId);
			}

			return theCustomer;
		}
		finally {
			close (myConn, myStmt, myRs);
		}
	}
	
	public void updateCustomer(Customer theCustomer) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "update customers set First_Name=?, Last_Name=?, Email_Address=?, Password=?, Phone_Number=? where Customer_ID=?";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setString(1, theCustomer.getFirstName());
			myStmt.setString(2, theCustomer.getLastName());
			myStmt.setString(3, theCustomer.getEmail());
			myStmt.setString(4, theCustomer.getPassword());
			myStmt.setInt(5, theCustomer.getPhoneNumber());
			myStmt.setInt(6, theCustomer.getId());
			
			myStmt.execute();
		}
		finally {
			close (myConn, myStmt);
		}
		
	}
	
	public void deleteCustomer(int customerId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "delete from customers where Customer_ID = ?";

			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, customerId);
			
			myStmt.execute();
		}
		finally {
			close (myConn, myStmt);
		}		
	}	
	
	private Connection getConnection() throws Exception {

		Connection theConn = dataSource.getConnection();
		
		return theConn;
	}
	
	private void close(Connection theConn, Statement theStmt) {
		close(theConn, theStmt, null);
	}
	
	private void close(Connection theConn, Statement theStmt, ResultSet theRs) {

		try {
			if (theRs != null) {
				theRs.close();
			}

			if (theStmt != null) {
				theStmt.close();
			}

			if (theConn != null) {
				theConn.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}	
}