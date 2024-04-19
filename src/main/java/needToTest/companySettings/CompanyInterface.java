package needToTest.companySettings;

import java.sql.SQLException;

public interface CompanyInterface {
    int createCompany(String name) throws SQLException;

    void deleteCompanyById(int id) throws SQLException;

    //мб сделать close, но наверное лучше в employee
    void close() throws SQLException;
}
