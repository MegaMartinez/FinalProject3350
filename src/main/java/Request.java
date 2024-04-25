import java.sql.*;

public class Request {
    public boolean closed;
    protected final String sqlScript;
    protected final Connection connection;

    public ResultSet query(String query) throws SQLException {
        if (closed) {
            System.out.println("Attempted to query closed connection!");
            return null;
        }

        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void close() {
        if (closed) return;
        try {
            connection.close();
            closed = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Request(Main Program) {
        try {
            connection = DriverManager.getConnection(Program.url, Program.username, Program.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sqlScript = Program.getResource("/sqlBuiltIn.sql");
        closed = false;
    }
}
