import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestSorter extends Request {
    public void searchEmp() {
        try {
            System.out.println("Enter \"CANCEL\" at any time to leave");

            String table = Main.prompt("Which table would you like to search? ");
            if (table.equalsIgnoreCase("cancel")) return;

            ResultSet res;

            System.out.println("Remember to use 'Single Quotes' when entering strings!");
            String column = Main.prompt("Which column would you like to search in? ");
            if (column.equalsIgnoreCase("cancel")) return;
            String key = Main.prompt("What value would you like to filter for? ");
            if (key.equalsIgnoreCase("cancel")) return;
            res = query("SELECT * FROM " + table + " WHERE " + column + " LIKE " + key + ";");

            List<String[]> rows = new ArrayList<>();
            rows.add(new String[res.getMetaData().getColumnCount()]);
            for (int i = 1; i < res.getMetaData().getColumnCount(); i++) {
                rows.get(0)[i - 1] = res.getMetaData().getColumnName(i);
            }
            while (res.next()) {
                String[] row = new String[rows.get(0).length];
                for (int i = 0; i < rows.get(0).length; i++) {
                    row[i] = res.getString(i + 1);
                }
                rows.add(row);
            }
            int[] lengths = new int[rows.get(0).length];

            for (int i = 0; i < rows.size(); i++) {
                for (int j = 0; j < rows.get(i).length; j++) {
                    if (rows.get(i)[j] == null) {
                        if (4 > lengths[j]) {
                            lengths[j] = 4;
                        }
                    } else if (rows.get(i)[j].length() > lengths[j]) {
                        lengths[j] = rows.get(i)[j].length();
                    }
                }
            }

            StringBuilder format = new StringBuilder();

            for (int i = 0; i < lengths.length; i++) {
                format.append("%-").append(lengths[i] + 2).append("s");
            }
            format.append("\n");

            System.out.format(format.toString(), (Object[]) rows.get(0));
            System.out.println("-".repeat(Arrays.stream(lengths).sum()));
            for (int i = 1; i < rows.size(); i++) {
                System.out.format(format.toString(), (Object[]) rows.get(i));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute! Printing Stack Trace:");
            e.printStackTrace();
            System.out.println("\nSystem is still running just press enter a couple times.");
        }
    }

    public RequestSorter(Main Program) {
        super(Program);
    }
}
