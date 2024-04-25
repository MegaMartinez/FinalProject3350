import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RequestManage extends Request{
    public void addEmp(){
        try {
            System.out.println("Enter \"CANCEL\" at any time to leave.");
            String table = Main.prompt("What table would you like to add to? ");
            if(table.equalsIgnoreCase("cancel")) return;
            ResultSet colRes = query("SELECT * FROM " + table + " WHERE 1 = 2;");
            System.out.println("Fill out the following fields. Remember to leave 'Single Quotes' around any strings!");
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            for(int i = 1; i < colRes.getMetaData().getColumnCount()+1; i++){
                columns.add(colRes.getMetaData().getColumnName(i));
                String val = Main.prompt(colRes.getMetaData().getColumnName(i) + ": ");
                if(val.equalsIgnoreCase("cancel")) return;
                values.add(val);
            }

            StringBuilder queryString = new StringBuilder("INSERT INTO " + table + "(");
            for(int i = 0; i < columns.size(); i++){
                if(i == 0)
                    queryString.append(columns.get(i));
                else
                    queryString.append(", ").append(columns.get(i));
            }
            queryString.append(") VALUES (");
            for(int i = 0; i < values.size(); i++){
                if(i == 0)
                    queryString.append(values.get(i));
                else
                    queryString.append(", ").append(values.get(i));
            }
            queryString.append(");");
            Statement statement = connection.createStatement();
            int rowschanged = statement.executeUpdate(queryString.toString());
            System.out.println("Rows Changed: "+rowschanged);
            
        } catch (SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void deleteEmp(){
        try{
            System.out.println("Enter \"CANCEL\" at any time to leave.");
            String table = Main.prompt("What table would you like to delete from? ");
            if(table.equalsIgnoreCase("cancel")) return;
            String column = Main.prompt("Which column to select from? ");
            if(column.equalsIgnoreCase("cancel")) return;
            String key = Main.prompt("Which value to delete entries containing? ");
            if(column.equalsIgnoreCase("cancel")) return;
            String query = "DELETE FROM " + table + " WHERE " + column + " LIKE " + key + ";";
            Statement statement = connection.createStatement();
            int rowschanged = statement.executeUpdate(query);
            System.out.println("Rows Changed: "+rowschanged);
        } catch(SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void updateEmp(){
        try {
            System.out.println("Enter \"CANCEL\" at any time to leave.");
            String table = Main.prompt("Which table would you like to update in? ");
            if(table.equalsIgnoreCase("cancel")) return;

            if(table.equalsIgnoreCase("employees")){
                String columnIdentifier = "";
                String id = "";

                String identifier = Main.prompt("How would you like to identify this person?\n" +
                        " - empid\n - full name\n - specific column\n - Cancel\nEnter: ");

                switch(identifier.toLowerCase()){
                    case "empid":
                        columnIdentifier = "empid";
                        id = Main.prompt("What is the employee ID?: ");
                        if(id.equalsIgnoreCase("cancel")) return;
                        break;
                    case "full name":
                        id = Main.prompt("What is the employee full name?: ");
                        columnIdentifier = "CONCAT(Fname, ' ', Lname)";
                        if(id.equalsIgnoreCase("cancel")) return;
                        break;
                    case "specific column":
                        columnIdentifier = Main.prompt("Which column to filter in: ");
                        id = Main.prompt("What to filter for: ");
                        if(id.equalsIgnoreCase("cancel")) return;
                        break;
                    case "cancel":
                        return;
                    default:
                        System.out.println("Invalid Response");
                        updateEmp();
                        return;
                }

                if (!Main.isValidEmployeeId(connection, id)) {
                    System.out.println("Invalid employee ID.");
                    return; // Exit the case if the employee ID is invalid
                }
                String field = Main.prompt("What is the table you'd like to update?: ");
                String newData = Main.prompt("What is the data you'd like to input?: ");
//            List<String> validFields = Arrays.asList("empid", "fname", "lname", "email", "HireDate", "Salary");
//            if (!validFields.contains(field.toLowerCase())) {
//                System.out.println("Invalid field.");
//                return;
//            }
                String query = "UPDATE employees SET " + field + " = ? WHERE " + columnIdentifier + " = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, newData); // Assuming newData is the updated email
                pstmt.setInt(2, Integer.parseInt(id)); 
                int rowschanged = pstmt.executeUpdate();

                System.out.println("ROWS CHANGED: " + rowschanged);
            } else {
                String searchCol = Main.prompt("Which column would you like to match entries in? ");
                if(searchCol.equalsIgnoreCase("cancel")) return;
                String searchKey = Main.prompt("Which value would you like to match before updating? ");
                if(searchKey.equalsIgnoreCase("cancel")) return;
                String field = Main.prompt("Which field would you like to update? ");
                if(field.equalsIgnoreCase("cancel")) return;
                String value = Main.prompt("Which value would you like to replace it with? ");
                if(value.equalsIgnoreCase("cancel")) return;

                String query = "UPDATE " + table + " SET " + field + " = " + value + " WHERE " + searchCol
                        + " LIKE " + searchKey + ";";
                int rowschanged = connection.createStatement().executeUpdate(query);
                System.out.println("ROWS CHANGED: " + rowschanged);
            }
        } catch (SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void rewriteTable(){
        System.out.println("You can type \"CANCEL\" at any time to exit this process.");
        String table = Main.prompt("Which table would you like to alter? ");
        String colName;
        String newType;
        if(table.equalsIgnoreCase("cancel")) return;
        boolean loop = true;
        while(loop){
            String options = Main.prompt("Which of the following would you like to do?\n" +
                    " - Add Column\n - Drop Column\n - Alter Column\nEnter: ");
            try {
                switch(options.toLowerCase()) {
                    case "add column":
                        colName = Main.prompt("What would you like to name the column? ");
                        if (colName.equalsIgnoreCase("cancel")) return;
                        newType = Main.prompt("What is the new type you would like for this column" +
                                "(feel free to include other attributes such as 'not null' if you want)? ");
                        if(newType.equalsIgnoreCase("cancel")) return;
                        String query= "ALTER TABLE " + table + " ADD " + colName + " " + newType + ";";
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(query);
                        loop = false;
                        break;
                    case "drop column":
                        colName = Main.prompt("What column would you like to drop? ");
                        if (colName.equalsIgnoreCase("cancel")) return;
                        String query2= "ALTER TABLE " + table + " DROP COLUMN " + colName + ";";
                        Statement statement2 = connection.createStatement();
                        statement2.executeUpdate(query2);
                        loop = false;
                        break;
                    case "alter column":
                        colName = Main.prompt("Which column would you like to alter? ");
                        if (colName.equalsIgnoreCase("cancel")) return;
                        newType = Main.prompt("What is the new type you would like for this column" +
                                "(feel free to include other attributes such as 'not null' if you want)? ");
                        if(newType.equalsIgnoreCase("cancel")) return;
                        String query3= "ALTER TABLE " + table + " MODIFY " + colName + " " + newType + ";";
                        Statement statement3 = connection.createStatement();
                        statement3.executeUpdate(query3);
                        loop = false;
                        break;
                    case "cancel":
                        return;
                    default:
                        System.out.println("Invalid Option");
                }
            } catch (SQLException e){
                System.out.println("Failed to execute! Printing Stack Trace:");
                e.printStackTrace();
                System.out.println("\nSystem is still running just press enter a couple times.");
            }
        }
    }

    public void resetDatabase(){
        try {
            query(sqlScript.substring(
                    sqlScript.lastIndexOf("-- START INIT DEFAULT"),
                    sqlScript.lastIndexOf("-- END INIT DEFAULT")
            ));
        } catch (SQLException e){
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
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
