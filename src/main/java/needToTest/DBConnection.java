package needToTest;

import java.sql.*;


public class DBConnection {
    public static void main(String[] args) throws SQLException {
        String connectionString = "jdbc:postgresql://dpg-cn1542en7f5s73fdrigg-a.frankfurt-postgres.render.com/x_clients_xxet";
        String user = "x_clients_user";
        String pass = "x7ngHjC1h08a85bELNifgKmqZa8KIR40";
        //String SQL = "SELECT * FROM employee";

//        String SQL = "SELECT * FROM company where name like '%ompan%' order by id desc";
//        String SQL_COUNT = "SELECT count(*) FROM company where name like '%ompan%'";

        String SQL_COMPANY_BY_ID = "SELECT * FROM company where id = ?";
        String SQL_employee = "select count(*) as c FROM employee where  company_id = ?";

        //ХАРДКОД
        int idCompany = 1872;

        Connection connection = DriverManager.getConnection(connectionString, user, pass);


        //Запрос с параметром в SQL
        PreparedStatement statement = connection.prepareStatement(SQL_employee);
        statement.setInt(1, idCompany);
        ResultSet resultSetMyCompany = statement.executeQuery();
        while (resultSetMyCompany.next()) {
//            System.out.println("Id компании: " + resultSetMyCompany.getInt("id"));
//            System.out.println("Название компании: " + resultSetMyCompany.getString("name"));
            System.out.println("количество сотрудников: " + resultSetMyCompany.getInt("c"));
        }
//        ResultSet resultSet = connection.createStatement().executeQuery(SQL);
//
//        // count
//        ResultSet rowsCount = connection.createStatement().executeQuery(SQL_COUNT);
//
//        rowsCount.next();
//        int count = rowsCount.getInt(1);
//        System.out.println(count);


//        while (resultSet.next()) {
//            System.out.println(resultSet.getInt("id"));
//        }


        connection.close();
    }
}
