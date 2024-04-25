import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static boolean processInput(String inputRaw, Main Program){
        String[] input = Stream.of(inputRaw.split(" ")).filter(w -> !w.isEmpty()).toArray(String[]::new);
        // ----------------------------------------------------------
        // TODO: WRITE MORE COMMANDS IN HERE TO IMPLEMENT THE PROJECT
        // ----------------------------------------------------------
        switch(input[0].toUpperCase()){
            case "HELP":
                System.out.println(Program.getResource("/helpCommand.txt"));
                break;

            case "REPORT":
                boolean useDate = false;
                boolean useName = false;
                String start = null;
                String end = null;
                String reportName = null;

                boolean runLoop = true;
                RequestReport report = new RequestReport(Program);
                while(runLoop){
                    String reportType =
                            prompt("Select a prompt:\n - Employee\n - Job Title\n - Division\n - Cancel\nEnter: ");
                    switch(reportType.toLowerCase()){
                        case "employee":
                            if(TFPrompt("Report on All? [true/false]: ")){
                                report.empInfoReport(false, null, null);
                            } else if(TFPrompt("Report on Full Name? [true/false]: ")) {
                                String fullname = prompt("Full name of employee to report on: ");
                                report.empInfoReport(true, "CONCAT(Fname, ' ', Lname)", fullname);
                            } else {
                                String col = prompt("Which column to filter on: ");
                                String val = prompt("Which value to filter by (include 'single quotes' if value is a string): ");
                                report.empInfoReport(true, col, val);
                            }
                            runLoop = false;
                            break;
                        case "job title":
                            if(TFPrompt("Report on Pay-date Range? [true/false]: ")){
                                start = prompt("Start of the date range (yyyy-mm-dd): ");
                                end = prompt("End of the date range (yyyy-mm-dd): ");
                                useDate = true;
                            }
                            if(TFPrompt("Report on Name-Matching Titles? [true/false]: ")){
                                reportName = prompt("Title name filter: ");
                                useName = true;
                            }
                            report.titlePayReport(useDate, start, end, useName, reportName);

                            runLoop = false;
                            break;
                        case "division":
                            if(TFPrompt("Report on Pay-date Range? [true/false]: ")){
                                start = prompt("Start of the date range (yyyy-mm-dd): ");
                                end = prompt("End of the date range (yyyy-mm-dd): ");
                                useDate = true;
                            }
                            if(TFPrompt("Report on Name-Matching Divisions? [true/false]: ")){
                                reportName = prompt("Division name filter: ");
                                useName = true;
                            }
                            report.divPayReport(useDate, start, end, useName, reportName);

                            runLoop = false;
                            break;
                        case "cancel":
                            runLoop = false;
                            break;
                        default:
                            System.out.println("Invalid answer, try again!");
                    }
                }
                report.close();
                break;

            case "UPDATE":
                RequestManage updateManager = new RequestManage(Program);
                updateManager.updateEmp();
                updateManager.close();

            case "SEARCH":
                RequestSorter searchSorter = new RequestSorter(Program);
                searchSorter.searchEmp();
                searchSorter.close();

            case "SALARYRAISE":
                RequestManage salaryManager = new RequestManage(Program);
                salaryManager.updateSalary();
                salaryManager.close();

            case "EXIT":
                System.out.println("Exiting...");
                return true;
            default:
                System.out.println("Unrecognized Command: " + input[0]);
                break;
        }
        return false;
    }

    public static String prompt(String prompt){
        System.out.print(prompt);
        Scanner promptScan = new Scanner(System.in);
        return promptScan.nextLine();
    }
    public static boolean TFPrompt(String prompt){
        System.out.print(prompt);
        Scanner promptScan = new Scanner(System.in);
        try {
            return promptScan.nextBoolean();
        } catch (InputMismatchException e){
            System.out.println("Invalid Response! Value must be \"true\" or \"false\"");
            return TFPrompt(prompt);
        }
    }
    public static Double NumPrompt(String prompt){
        System.out.print(prompt);
        Scanner NumPromptScan = new Scanner(System.in);
        return NumPromptScan.nextDouble();
    }

    public static boolean isValidEmployeeId(Connection connection, String empId) throws SQLException {
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

    public String url = "jdbc:mysql://localhost:3306/employeeData";
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

    private String password = "";
    public String getPassword() {
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
