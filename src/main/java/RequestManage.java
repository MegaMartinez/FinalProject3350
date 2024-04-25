import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class RequestManage extends Request{
    public void addEmp(){

    }

    public void updateEmp(){
        try {
            String id = Main.prompt("What is the employee ID?: ");
            if (!Main.isValidEmployeeId(connection, id)) {
                System.out.println("Invalid employee ID.");
                return; // Exit the case if the employee ID is invalid
            }
            String field = Main.prompt("What is the table you'd like to update?: ");
            String newData = Main.prompt("What is the data you'd like to input?: ");
            List<String> validFields = Arrays.asList("empid", "fname", "lname", "email", "HireDate", "Salary");
            if (!validFields.contains(field.toLowerCase())) {
                System.out.println("Invalid field.");
                return;
            }
            String query = "UPDATE employees SET " + field + " = '" + newData + "' WHERE empid = " + id;
            Statement statement = connection.createStatement();
            int rowschanged = statement.executeUpdate(query);
            System.out.println("ROWS CHANGED: " + rowschanged);
        } catch (SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void rewriteEmp(){

    }

    public void updateSalary(){
        try{
            double percent = Main.NumPrompt("What percentage do you want to raise the salaries by?: ");
            if (!(percent >= 0 && percent <= 100)) {
                {
                    System.out.println("Invalid percentage.");
                    return;
                }
            }
            double thresholdSalary = Main.NumPrompt("What is the salary threshold for this raise?: ");
            //Updates salary for employees with salary less than the threshold
            String RaiseQuery = "UPDATE employees SET salary = salary * (1 + " + percent + "/ 100) WHERE salary < " + thresholdSalary;
            Statement RaiseStatement = connection.createStatement();
            int rowsChanged = RaiseStatement.executeUpdate(RaiseQuery);
            System.out.println("ROWS CHANGED: "+rowsChanged);
        } catch(SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public RequestManage(Main Program) {
        super(Program);
    }
}
