package needToTest.companySettings;

import java.sql.*;

public class Company implements CompanyInterface {
    private static final String SQL_INSERT_COMPANY = "INSERT INTO company(\"name\") values (?)";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM company WHERE id = ?";

    private Connection connection;

    public Company(String connectionString, String user, String pass) throws SQLException {
        this.connection = DriverManager.getConnection(connectionString, user, pass);
    }

    @Override
    public int createCompany(String name) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_INSERT_COMPANY, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        keys.next();
        return keys.getInt("id");
    }

    @Override
    public void deleteCompanyById(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID);
        statement.setInt(1, id);
        statement.executeUpdate();
    }


    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
