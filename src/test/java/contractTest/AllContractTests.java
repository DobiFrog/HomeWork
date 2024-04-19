package contractTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import needToTest.Authorization;
import needToTest.companySettings.Company;
import needToTest.companySettings.CompanyInterface;
import needToTest.employeeSettings.Employee;
import needToTest.employeeSettings.EmployeeInterface;
import needToTest.employeeSettings.EmployeeJson;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class AllContractTests {
    private static String TOKEN;
    private static final String CONNECTION_STRING = "jdbc:postgresql://dpg-cn1542en7f5s73fdrigg-a.frankfurt-postgres.render.com/x_clients_xxet";
    private static final String USER = "x_clients_user";
    private static final String PASS = "x7ngHjC1h08a85bELNifgKmqZa8KIR40";
    private static CompanyInterface COMPANY_TO_DELETE;
    private static EmployeeInterface EMPLOYEE;
    private static int ID_COMPANY_TO_DELETE;

    @BeforeAll
    static void allBeforeTests() {
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
    }

    @BeforeEach
    public void allBeforeEach() throws SQLException {
        //создать компанию
        COMPANY_TO_DELETE = new Company(CONNECTION_STRING, USER, PASS);
        ID_COMPANY_TO_DELETE = COMPANY_TO_DELETE.createCompany("Патрик тостит API");

        //коннект к БД employee
        EMPLOYEE = new Employee(CONNECTION_STRING, USER, PASS);
    }

    @AfterEach
    public void allAfterEach() throws SQLException {
        //удалить компанию
        given()
                .log().all()
                .pathParams("id", ID_COMPANY_TO_DELETE)
                .header("x-client-token", TOKEN)
                .get("/company/delete/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Vary", "Accept-Encoding")
                .header("Content-Type", "application/json; charset=utf-8")
                .body(is(notNullValue()))
                .log().all();

        //закрыть конект компани
        COMPANY_TO_DELETE.close();
        //закрыть конект емплои
        EMPLOYEE.close();
    }

    @Test
    @DisplayName("Добавить сотрудника без обязательного поля")
    public void emplWithoutName() {
        EmployeeJson json = new EmployeeJson();
        json.setLastName("безИмени");
        json.setPhone("string");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        given()
                .log().all()
                .body(json)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .post("/employee?company=" + ID_COMPANY_TO_DELETE)
                .then()
                .log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Получить несуществующего сотрудника")
    public void nonexistentEmpl() {
        int newEmployeeId = 2;

        given()
                .log().all()
                .pathParams("id", newEmployeeId)
                .header("x-client-token", "TOKEN")
                .header("content-length", 0)
                .get("/employee/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(404)
                .log().all();
    }

    @Test
    @DisplayName("Изменить поле сотрудника, которое нельзя менять")
    public void changeWrongFieldEmpl() {
        EmployeeJson json = new EmployeeJson();

        json.setFirstName("сотрудника");
        json.setLastName("меняем");
        json.setPhone("+7917654900");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        //создаём сотрудника для изменения
        int idEmplApi = newEmployeeId(json);

        //json для изменения
        EmployeeJson jsonPatch = new EmployeeJson();
        jsonPatch.setFirstName("другоеИмя");

        //меняем сотрудника и проверяем что он изменился
        given()
                .body(jsonPatch)
                .log().all()
                .pathParams("id", idEmplApi)
                .header("x-client-token", TOKEN)
                .patch("/employee/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(notNullValue()))
                .body("id", equalTo(idEmplApi))
                .body("firstName", equalTo(json.getFirstName()))
                .body("email", equalTo(jsonPatch.getEmail()))
                .log().all();
    }

    @Test
    @DisplayName("Изменить сотрудника, которого нет")
    public void changeWrongEmpl() {
        EmployeeJson json = new EmployeeJson();
        json.setFirstName("сотрудника");
        json.setLastName("меняем");
        json.setPhone("+7917654900");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        given()
                .body(json)
                .log().all()
                .pathParams("id", 1234)
                .header("x-client-token", TOKEN)
                .patch("/employee/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(404)
                .log().all();
    }

    static private int newEmployeeId(EmployeeJson json) {
        return given()
                .log().all()
                .body(json)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when().post("/employee?company=" + ID_COMPANY_TO_DELETE)
                .then()
                .log().all()
                .statusCode(201)
                .extract().path("id");
    }
}
