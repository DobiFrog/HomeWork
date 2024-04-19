package needToTest.employeeSettings;

import java.sql.SQLException;

public interface EmployeeInterface {
    //новый сотрудник
    int createEmployee(String firstName, String lastName, String phone, int companyId) throws SQLException;

    //получить по id
    EmployeeConst getEmployee(int employeeId) throws SQLException;

    //изменить инфо
    void patchEmployee(String lastName, String email, int employeeId) throws SQLException;

    void close() throws SQLException;

    //количество сотрудников
    int numOfEmployee(int companyId) throws SQLException;
}
