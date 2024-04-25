import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RequestSorter extends Request {
    public void searchEmp(){
        try{
            String searchId = Main.prompt("What is the employee ID?: ");
            if (!Main.isValidEmployeeId(connection, searchId)) {
                System.out.println("Invalid employee ID.");
                return; // Exit the case if the employee ID is invalid
            }
            String SearchQuery = "SELECT * FROM employees WHERE empid = " + searchId;
            Statement SearchStatement = connection.createStatement();
            ResultSet resultSet = SearchStatement.executeQuery(SearchQuery);
            while (resultSet.next()) {
                String name = resultSet.getString("fname");
                System.out.println("ID: " + resultSet.getInt("empid") + ", Name: " + resultSet.getString("fname")
                        +" "+resultSet.getString("lname")+ ", Email: "+resultSet.getString("email")
                        + ", HireDate: "+resultSet.getString("HireDate")+ ", Salary: "+resultSet.getString("Salary"));
            }
        } catch(SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void searchTitleEmps(){

    }

    public void searchDivisionEmps(){

    }

    public void averageSalary(){

    }

    public RequestSorter(Main Program) {
        super(Program);
    }
}
