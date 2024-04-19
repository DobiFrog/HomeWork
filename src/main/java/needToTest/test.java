package needToTest;

import needToTest.employeeSettings.Employee;
import needToTest.employeeSettings.EmployeeInterface;

import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {

        String connectionString = "jdbc:postgresql://dpg-cn1542en7f5s73fdrigg-a.frankfurt-postgres.render.com/x_clients_xxet";
        String user = "x_clients_user";
        String pass = "x7ngHjC1h08a85bELNifgKmqZa8KIR40";

        EmployeeInterface newEmpl = new Employee(connectionString, user, pass);


//        int newEmplId = newEmpl.createEmployee("работает", "ли", "7856783420",1035);
//        System.out.println(newEmplId);
//
//        EmployeeConst sush = newEmpl.getEmployee(newEmplId);
//        System.out.println(sush);
//        System.out.println(sush.firstName());
//
//        newEmpl.patchEmployee("иванович", "kokoko@mail.ru",newEmplId);
//
//        EmployeeConst sush1 = newEmpl.getEmployee(newEmplId);
//        System.out.println(sush1);
//        System.out.println(sush1.lastName());
//        System.out.println(sush1.firstName());
//
        System.out.println(newEmpl.numOfEmployee(1872));


        newEmpl.close();


//        CompanyInterface TO_DELETE = new Company(connectionString, user, pass);
//        int ID_COMPANY_TO_DELETE = TO_DELETE.createCompany("Патрик тостит API");
//        System.out.println("ID новой компании: " + ID_COMPANY_TO_DELETE);


    }
}
