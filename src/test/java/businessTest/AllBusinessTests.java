package businessTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import needToTest.Authorization;
import needToTest.companySettings.Company;
import needToTest.companySettings.CompanyInterface;
import needToTest.employeeSettings.Employee;
import needToTest.employeeSettings.EmployeeConst;
import needToTest.employeeSettings.EmployeeInterface;
import needToTest.employeeSettings.EmployeeJson;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

public class AllBusinessTests {
    private static String TOKEN;
    private static final String CONNECTION_STRING = "jdbc:postgresql://dpg-cn1542en7f5s73fdrigg-a.frankfurt-postgres.render.com/x_clients_xxet";
    private static final String USER = "x_clients_user";
    private static final String PASS = "x7ngHjC1h08a85bELNifgKmqZa8KIR40";
    private static CompanyInterface COMPANY_TO_DELETE;
    private static EmployeeInterface EMPLOYEE;
    private static int ID_COMPANY_TO_DELETE;




    @BeforeAll
    static void allBeforeTests() throws SQLException {
        //авторизация
        RestAssured.baseURI = "https://x-clients-be.onrender.com";
        Authorization auth = new Authorization();
        auth.setUsername("tecna");
        auth.setPassword("tecna-fairy");

        TOKEN = given()
                .body(auth)
                .contentType(ContentType.JSON)
                .when().post("/auth/login")
                .then()
                .statusCode(201)
                .extract().path("userToken");

        //создать компанию
        COMPANY_TO_DELETE = new Company(CONNECTION_STRING, USER, PASS);
        ID_COMPANY_TO_DELETE = COMPANY_TO_DELETE.createCompany("Патрик тостит API");
        //System.out.println("ID новой компании: " + ID_COMPANY_TO_DELETE);

        //коннект к БД employee
        EMPLOYEE = new Employee(CONNECTION_STRING, USER, PASS);

    }

    @BeforeEach
    public void allBeforeEach() {
        //конект к БД
    }

    @AfterAll
    public static void allAfterTests() throws SQLException {
        //удалить компанию
        //COMPANY_TO_DELETE.deleteCompanyById(ID_COMPANY_TO_DELETE);

    }

    @AfterEach
    public void allAfterEach() throws SQLException {
        //закрыть конект компани
        COMPANY_TO_DELETE.close();
        //закрыть конект емплои
        EMPLOYEE.close();
    }

    @Test
    @DisplayName("Создать нового сотрудника")
    public void NewEmployee(){
        //EmployeeConst empl = new EmployeeConst();
        EmployeeJson s = new EmployeeJson();
        s.setFirstName("проверчик");
        s.setLastName("проверка");
        s.setCompanyId(ID_COMPANY_TO_DELETE);

        given()
                .log().all()
                .body(s)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when().post("/employee?company=" + ID_COMPANY_TO_DELETE)
                .then()
                .log().all()
                .statusCode(201)
                .body("id", greaterThan(0))
                .extract().path("id");



    }

}
