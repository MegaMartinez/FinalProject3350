import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestReport extends Request {
    public void empInfoReport(boolean useWhere, String col, String param){
        try {
            ResultSet res;

            if(useWhere){
                res = query(sqlScript.substring(
                        sqlScript.lastIndexOf("-- START EMP REPORT WHERE"),
                        sqlScript.lastIndexOf("-- END EMP REPORT WHERE")
                )
                        .replace("/*COL*/", col)
                        .replace("/*PARAM*/", param)
                );
            } else {
                res = query(sqlScript.substring(
                        sqlScript.lastIndexOf("-- START EMP REPORT ALL"),
                        sqlScript.lastIndexOf("-- END EMP REPORT ALL")
                ));
            }

            int currentID = -1;
            while(res.next()){
                if(currentID != res.getInt("e.empid")){
                    currentID = res.getInt("e.empid");

                    int i;
                    List<String> EmployeeHeaders = new ArrayList<>();

                    i = 1;
                    while(!res.getMetaData().getColumnName(i).equals("payID")){
                        EmployeeHeaders.add(res.getMetaData().getColumnName(i));
                        i++;
                    }

                    String[] EmployeeData = new String[EmployeeHeaders.size()];
                    StringBuilder formatStr = new StringBuilder();
                    int total = 0;

                    i = 0;
                    for(; i < EmployeeHeaders.size(); i++){
                        EmployeeData[i] = res.getString(EmployeeHeaders.get(i));
                        int len = EmployeeData[i].length() + 2 >= EmployeeHeaders.get(i).length()
                                ? EmployeeData[i].length() + 2 : EmployeeHeaders.get(i).length() + 2;
                        formatStr.append("%-").append(len).append("s");
                        total += len;
                    }
                    formatStr.append("\n");

                    System.out.println("=".repeat(total));
                    System.out.format(formatStr.toString(), EmployeeHeaders.toArray());
                    System.out.format(formatStr.toString(), (Object[]) EmployeeData);
                    System.out.println("-".repeat(total));

                    List<String> PayHeaders = new ArrayList<>();
                    formatStr = new StringBuilder();

                    i = res.findColumn("payID");
                    total = 0;
                    for(; i < res.getMetaData().getColumnCount(); i++){
                        if(res.getMetaData().getColumnName(i).equals("p.empid")) continue;

                        PayHeaders.add(res.getMetaData().getColumnName(i));
                        formatStr.append("%-12s");
                        total += 12;
                    }
                    formatStr.append("\n");

                    System.out.format(formatStr.toString(), PayHeaders.toArray());
                    System.out.println("-".repeat(total));
                }

                StringBuilder formatStr = new StringBuilder();
                List<String> PayData = new ArrayList<>();

                int i = res.findColumn("payID");
                for(; i < res.getMetaData().getColumnCount(); i++){
                    if(res.getMetaData().getColumnName(i).equals("p.empid")) continue;

                    PayData.add(res.getString(i));
                    formatStr.append("%-12s");
                }
                formatStr.append("\n");

                System.out.format(formatStr.toString(), PayData.toArray());
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void titlePayReport(boolean useDates, String start, String end, boolean useName, String jobName){
        try {
            ResultSet res;

            if(useDates && useName){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START TITLE REPORT WHERE"),
                                        sqlScript.lastIndexOf("-- END TITLE REPORT WHERE")
                                )
                                .replace("/*START DATE*/", "'" + start + "'")
                                .replace("/*END DATE*/", "'" + end + "'")
                                .replace("/*TITLE NAME*/", "'" + jobName + "'")
                );
            } else if(useDates){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START TITLE REPORT DATE WHERE"),
                                        sqlScript.lastIndexOf("-- END TITLE REPORT DATE WHERE")
                                )
                                .replace("/*START DATE*/", "'" + start + "'")
                                .replace("/*END DATE*/", "'" + end + "'")
                );
            } else if(useName){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START TITLE REPORT NAME WHERE"),
                                        sqlScript.lastIndexOf("-- END TITLE REPORT NAME WHERE")
                                )
                                .replace("/*TITLE NAME*/", "'" + jobName + "'")
                );
            } else {
                res = query(sqlScript.substring(
                        sqlScript.lastIndexOf("-- START TITLE REPORT ALL"),
                        sqlScript.lastIndexOf("-- END TITLE REPORT ALL")
                ));
            }

            String[] headers = {
                    "Job Title Id",
                    "Job Title",
                    "Month",
                    "Earnings"
            };
            List<String> job_title_id = new ArrayList<>();
            List<String> job_title = new ArrayList<>();
            List<String> pay_date = new ArrayList<>();
            List<Double> earnings = new ArrayList<>();

            List<String[]> rows = new ArrayList<>();

            String currentId = "";
            while(res.next()){
                if(!currentId.equals(res.getString("job_title_id"))){
                    currentId = res.getString("job_title_id");

                    job_title_id.add(res.getString("job_title_id"));
                    job_title.add(res.getString("job_title"));
                    pay_date.add(res.getString("pay_date").substring(0, 7));
                    earnings.add(res.getDouble("earnings"));
                } else {
                    int last = job_title_id.size() - 1;

                    if(pay_date.get(last).equals(res.getString("pay_date").substring(0, 7))){
                        earnings.set(
                            last,
                            Math.round((earnings.get(last) + res.getDouble("earnings")) * 100) / 100.0
                        );
                    } else {
                        job_title_id.add(res.getString("job_title_id"));
                        job_title.add(res.getString("job_title"));
                        pay_date.add(res.getString("pay_date").substring(0, 7));
                        earnings.add(res.getDouble("earnings"));
                    }
                }
            }

            for(int i = 0; i < job_title_id.size(); i++){
                rows.add(new String[] {
                        job_title_id.get(i),
                        job_title.get(i),
                        pay_date.get(i),
                        String.valueOf(earnings.get(i))
                });
            }

            int a = headers[0].length();
            int b = headers[1].length();
            int c = headers[2].length();
            for(int i = 0; i < job_title_id.size(); i++){
                if(job_title_id.get(i).length() > a){
                    a = job_title_id.get(i).length();
                }
                if(job_title.get(i).length() > b){
                    b = job_title.get(i).length();
                }
                if(pay_date.get(i).length() > c){
                    c = pay_date.get(i).length();
                }
            }

            String format = "%-" + (a + 2) + "s%-" + (b + 2) + "s%-" + (c + 2) + "s%-12s\n";
            System.out.format(format, (Object[]) headers);
            for(int i = 0; i < rows.size(); i++){
                System.out.format(format, (Object[]) rows.get(i));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public void divPayReport(boolean useDates, String start, String end, boolean useName, String divName){
        try {
            ResultSet res;
            if(useDates && useName){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START DIV REPORT WHERE"),
                                        sqlScript.lastIndexOf("-- END DIV REPORT WHERE")
                                )
                                .replace("/*START DATE*/", "'" + start + "'")
                                .replace("/*END DATE*/", "'" + end + "'")
                                .replace("/*DIV NAME*/", "'" + divName + "'")
                );
            } else if(useDates){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START DIV REPORT DATE WHERE"),
                                        sqlScript.lastIndexOf("-- END DIV REPORT DATE WHERE")
                                )
                                .replace("/*START DATE*/", "'" + start + "'")
                                .replace("/*END DATE*/", "'" + end + "'")
                );
            } else if(useName){
                res = query(sqlScript.substring(
                                        sqlScript.lastIndexOf("-- START DIV REPORT NAME WHERE"),
                                        sqlScript.lastIndexOf("-- END DIV REPORT NAME WHERE")
                                )
                                .replace("/*DIV NAME*/", "'" + divName + "'")
                );
            } else {
                res = query(sqlScript.substring(
                        sqlScript.lastIndexOf("-- START DIV REPORT ALL"),
                        sqlScript.lastIndexOf("-- END DIV REPORT ALL")
                ));
            }

            String[] headers = {
                    "Division Id",
                    "Division Name",
                    "Month",
                    "Earnings"
            };
            List<String> division_id = new ArrayList<>();
            List<String> division_name = new ArrayList<>();
            List<String> pay_date = new ArrayList<>();
            List<Double> earnings = new ArrayList<>();

            List<String[]> rows = new ArrayList<>();

            String currentId = "";
            while(res.next()){
                if(!currentId.equals(res.getString("ID"))){
                    currentId = res.getString("ID");

                    division_id.add(res.getString("ID"));
                    division_name.add(res.getString("Name"));
                    pay_date.add(res.getString("pay_date").substring(0, 7));
                    earnings.add(res.getDouble("earnings"));
                } else {
                    int last = division_id.size() - 1;

                    if(pay_date.get(last).equals(res.getString("pay_date").substring(0, 7))){
                        earnings.set(
                                last,
                                Math.round((earnings.get(last) + res.getDouble("earnings")) * 100) / 100.0
                        );
                    } else {
                        division_id.add(res.getString("ID"));
                        division_name.add(res.getString("Name"));
                        pay_date.add(res.getString("pay_date").substring(0, 7));
                        earnings.add(res.getDouble("earnings"));
                    }
                }
            }

            for(int i = 0; i < division_id.size(); i++){
                rows.add(new String[] {
                        division_id.get(i),
                        division_name.get(i),
                        pay_date.get(i),
                        String.valueOf(earnings.get(i))
                });
            }

            int a = headers[0].length();
            int b = headers[1].length();
            int c = headers[2].length();
            for(int i = 0; i < division_id.size(); i++){
                if(division_id.get(i).length() > a){
                    a = division_id.get(i).length();
                }
                if(division_name.get(i).length() > b){
                    b = division_name.get(i).length();
                }
                if(pay_date.get(i).length() > c){
                    c = pay_date.get(i).length();
                }
            }

            String format = "%-" + (a + 2) + "s%-" + (b + 2) + "s%-" + (c + 2) + "s%-12s\n";
            System.out.format(format, (Object[]) headers);
            for(int i = 0; i < rows.size(); i++){
                System.out.format(format, (Object[]) rows.get(i));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public RequestReport(Main Program) {
        super(Program);
    }
}
