import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class AddCustomerController {

	private List<Customer> customers;
	private ShoppingDbUtil shoppingDbUtil;
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public AddCustomerController() throws Exception {
		customers = new ArrayList<>();
		
		shoppingDbUtil = ShoppingDbUtil.getInstance();
	}
	
	public List<Customer> getCustomers() {
		return customers;
	}

	public void loadCustomers() {

		logger.info("Loading customers");
		
		customers.clear();

		try {
			
			// get all customer from database
			customers = shoppingDbUtil.getCustomers();
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error loading customers", exc);
			
			// add error message for JSF page
			addErrorMessage(exc);
		}
	}
		
	public String addCustomer(Customer theCustomer) {

		logger.info("Adding customer: " + theCustomer);

		try {
			
			// add customer to the database
			shoppingDbUtil.addCustomer(theCustomer);
			
		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error adding customers", exc);
			
			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}
		
		return "add-and-list-customers?faces-redirect=true";
	}
		
	private void addErrorMessage(Exception exc) {
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
}