import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static boolean processInput(String inputRaw, Main Program){
        
        String[] input = Stream.of(inputRaw.split(" ")).filter(w -> !w.isEmpty()).toArray(String[]::new);;
        // ----------------------------------------------------------
        // TODO: WRITE MORE COMMANDS IN HERE TO IMPLEMENT THE PROJECT
        // ----------------------------------------------------------
        String username = "root";
        String password = getPassword();
        try{
            Connection connection = DriverManager.getConnection(url, username,password);
            switch(input[0].toUpperCase()){
                case "HELP":
                    System.out.println(Program.getResource("/helpCommand.txt"));
                    break;
                case "UPDATE":
                    String id = prompt("What is the employee ID?: ");
                    if (!isValidEmployeeId(connection, id)) {
                        System.out.println("Invalid employee ID.");
                        break; // Exit the case if the employee ID is invalid
                    }
                    String field = prompt("What is the table you'd like to update?: ");
                    String newData = prompt("What is the data you'd like to input?: ");
                    List<String> validFields = Arrays.asList("empid","fname", "lname", "email","HireDate","Salary");
                    if (!validFields.contains(field.toLowerCase())) {
                        System.out.println("Invalid field.");
                        break;
                    }
                    String query = "UPDATE employees SET "+field+ " = '" + newData + "' WHERE empid = " + id;
                    Statement statement = connection.createStatement();
                    int rowschanged = statement.executeUpdate(query);
                    System.out.println("ROWS CHANGED: "+rowschanged);
                
                case "SEARCH":
                    String searchId = prompt("What is the employee ID?: ");
                    if (!isValidEmployeeId(connection, searchId)) {
                        System.out.println("Invalid employee ID.");
                        break; // Exit the case if the employee ID is invalid
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
                case "SALARYRAISE":
                //UPDATE employees SET salary = salary * (1 + :percentage / 100) WHERE salary < :specifiedAmount;

                    double percent = NumPrompt("What percentage do you want to raise the salaries by?: ");
                    if (!(percent >= 0 && percent <= 100)) {
                        {
                            System.out.println("Invalid percentage.");
                            break;
                        }
                    }
                    double thresholdSalary = NumPrompt("What is the salary threshold for this raise?: "); 
                    //Updates salary for employees with salary less than the threshold
                    String RaiseQuery = "UPDATE employees SET salary = salary * (1 + " + percent + "/ 100) WHERE salary < " + thresholdSalary;
                    Statement RaiseStatement = connection.createStatement();
                    int rowsChanged = RaiseStatement.executeUpdate(RaiseQuery);
                    System.out.println("ROWS CHANGED: "+rowsChanged);


                case "EXIT":
                    System.out.println("Exiting...");
                    return true;
                default:
                    System.out.println("Unrecognized Command: " + input[0]);
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // To make things easier for you, here's a function specifically for taking input from the user.
    // You probably won't need it since most prompting is done through the main feedback loop.
    // Still, this could be useful for you in certain situations.
    public static String prompt(String prompt){
        System.out.print(prompt);
        Scanner promptScan = new Scanner(System.in);
        return promptScan.nextLine();
    }
    public static Double NumPrompt(String prompt){
        System.out.print(prompt);
        Scanner NumPromptScan = new Scanner(System.in);
        return NumPromptScan.nextDouble();
    }

    private static boolean isValidEmployeeId(Connection connection, String empId) throws SQLException {
        if(!empId.matches("\\d+"))
        {
            return false;
        }
        String query = "SELECT COUNT(*) FROM employees WHERE empid = " + empId;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false; 
    }

    public static void main(String[] args) throws ClassNotFoundException
    {
        Main Program = new Main();
        Class.forName("com.mysql.cj.jdbc.Driver");

        for(int i = 0; i < args.length; i++){
            if(args[i].startsWith("-")){
                String arg = args[i];
                String[] params;
                int j = i;
                while(true){
                    if(j + 1 < args.length)
                        if(args[j + 1].startsWith("-")) break;
                    if(j == args.length) break;
                    j++;
                }
                if(j > i){
                    params = new String[j - i];
                } else {
                    params = new String[0];
                }
                j = i;
                while(true){
                    if(i + 1 < args.length)
                        if(args[i + 1].startsWith("-")) break;
                    if(i == args.length) break;
                    params[0] = args[i - j];
                    i++;
                }
                processArg(arg, params, Program);
            }
        }

        System.out.print(Program.getResource("/header.txt")
                .replace("[ADDRESS_RUNTIME_SWAP]", Program.getAddress())
                .replace("[PORT_RUNTIME_SWAP]", Program.getPort())
                .replace("[DATABASE_RUNTIME_SWAP]", Program.getDatabase())
                .replace("[USER_RUNTIME_SWAP]", Program.username));

        int attempts = 0;
        if(System.console() == null){
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! WARNING !!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("             YOU ARE USING THIS ON AN IDE TERMINAL");
            System.out.println("  PASSWORD OBSCURITY IS ONLY SUPPORTED FOR A GENUINE TERMINAL");
            System.out.println("       SWITCH TO A GENUINE TERMINAL FOR INPUT OBSCURITY\n");
        }

        while(true){
            attempts++;
            System.out.print("Enter Password: ");

            if(System.console() == null){
                Scanner passScan = new Scanner(System.in);
                String input = passScan.nextLine();

                if(Program.checkPassword(input)) break;
                else if(attempts >= 3){
                    System.out.println("ACCESS DENIED");
                    System.exit(0);
                }
            } else {
                if(Program.checkPassword(new String(System.console().readPassword()))) break;
                else if(attempts >= 3){
                    System.out.println("ACCESS DENIED");
                    System.exit(0);
                }
            }
        }
        System.out.println("ACCEPTED");

        System.out.println(Program.getResource("/headerCommand.txt"));

        while(true){
            System.out.print("> ");
            Scanner inputScan = new Scanner(System.in);
            String input = inputScan.nextLine();

            if(processInput(input, Program)){
                break;
            }
        }
    }

    public static String url = "jdbc:mysql://localhost:3306/employeeData";
    private void generateURL(){
        String tempUrl = "jdbc:mysql://[ADDRESS]:[PORT]/[DATABASE]";
        url = tempUrl
                .replace("[ADDRESS]", address)
                .replace("[PORT]", port)
                .replace("[DATABASE]", database);
    }

    private String address = "localhost";
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
        generateURL();
    }

    private String port = "3306";
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
        generateURL();
    }

    private String database = "employeeData";
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
        generateURL();
    }

    public String username = "root";

    private static String password = "";
    public static String getPassword() {
        return password;
    }
    public boolean checkPassword(String passwordInput){
        try {
            Connection testConnection = DriverManager.getConnection(url, username, passwordInput);
            testConnection.close();
        } catch(SQLException e){
            if(!e.getMessage().contains("Access denied")){
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("INCORRECT");
            return false;
        }
        password = passwordInput;
        return true;
    }

    public String getResource(String fileName)
    {
        System.out.println(getClass().getResource("").getPath());

        File resourceFile = new File(getClass().getResource(fileName).getPath());
        StringBuilder fileText = new StringBuilder();

        try
        {
            Scanner fileScanner = new Scanner(resourceFile);
            while(fileScanner.hasNextLine())
            {
                fileText.append(fileScanner.nextLine()).append('\n');
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        return fileText.toString();
    }

    public static void processArg(String arg, String[] params, Main Program){
        switch(arg){
            case "-h":
            case "--help":
                System.out.println(Program.getResource("/helpArguments.txt"));
                System.exit(0);
            case "-p":
            case "--port":
                if(params.length < 1){
                    System.out.println("AN ARGUMENT MUST BE PROVIDED FOR " + arg);
                    System.out.println("USING DEFAULT PORT");
                } else {
                    try {
                        Integer.parseInt(params[0]);
                    } catch (NumberFormatException e) {
                        System.out.println("PORT MUST BE AN INTEGER");
                        System.out.println("USING DEFAULT PORT");
                        return;
                    }
                    Program.setPort(params[0]);
                }
                break;
            case "-u":
            case "--user":
            case "--username":
                if(params.length < 1){
                    System.out.println("AN ARGUMENT MUST BE PROVIDED FOR " + arg);
                    System.out.println("USING DEFAULT USERNAME");
                } else {
                    Program.username = params[0];
                }
                break;
            case "-d":
            case "-db":
            case "--database":
                if(params.length < 1){
                    System.out.println("AN ARGUMENT MUST BE PROVIDED FOR " + arg);
                    System.out.println("USING DEFAULT DATABASE");
                } else {
                    Program.setDatabase(params[0]);
                }
                break;
            case "-a":
            case "--addr":
            case "--address":
                if(params.length < 1){
                    System.out.println("AN ARGUMENT MUST BE PROVIDED FOR " + arg);
                    System.out.println("USING DEFAULT ADDRESS");
                } else {
                    Program.setAddress(params[0]);
                }
                break;
            default:
                System.out.println("UNKNOWN ARGUMENT: " + arg);
        }
    }
}
