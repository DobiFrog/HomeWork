package needToTest.employeeSettings;

import java.sql.*;

public class Employee implements EmployeeInterface {
    private static final String SQL_INSERT_EMPLOYEE = "INSERT INTO employee (\"first_name\", \"last_name\", \"phone\", \"company_id\") values (?, ?, ?, ?)";
    public static final String SQL_SELECT_BY_ID_EMPLOYEE = "SELECT * FROM employee where id = ?";
    public static final String SQL_UPDATE_EMPLOYEE = "UPDATE employee SET last_name = ?, email = ? WHERE id = ?";


    private Connection connection;

    public Employee(String connectionString, String user, String pass) throws SQLException {
        this.connection = DriverManager.getConnection(connectionString, user, pass);
    }

    @Override
    public int createEmployee(String firstName, String lastName, String phone, int companyId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setString(3, phone);
        //надо ли переводить в стрингу?
        statement.setInt(4, companyId);
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        keys.next();
        return keys.getInt(1);
    }

    @Override
    public EmployeeConst getEmployee(int employeeId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID_EMPLOYEE);
        statement.setInt(1, employeeId);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next(); // 1

        return new EmployeeConst(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("middle_name"),
                resultSet.getInt("company_id"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getBoolean("is_active")
        );
    }

    @Override
    public void patchEmployee(String lastName, String email, int employeeId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_EMPLOYEE);
        statement.setString(1, lastName);
        statement.setString(2, email);
        statement.setInt(3, employeeId);

        //кол-во изменённых строк
        statement.executeUpdate();

    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }


}
