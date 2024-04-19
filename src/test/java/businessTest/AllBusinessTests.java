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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class AllBusinessTests {
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
    @DisplayName("Создать нового сотрудника")
    public void NewEmployee() throws SQLException {
        //создаем нового сотрудника
        EmployeeJson json = new EmployeeJson();
        json.setFirstName("проверчик");
        json.setLastName("проверка");
        json.setPhone("string");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        int idEmplApi = newEmployeeId(json);

        EmployeeConst emplDBId = EMPLOYEE.getEmployee(idEmplApi);

        //проверка что такой id есть в БД
        given()
                .log().all()
                .pathParams("id", idEmplApi)
                .header("x-client-token", TOKEN)
                .get("/employee/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(notNullValue()))
                .body("id", equalTo(emplDBId.id()))
                .body("firstName", equalTo(emplDBId.firstName()))
                .log().all();
    }

    @Test
    @DisplayName("Изменить сотрудника")
    public void PatchEmployee() throws SQLException {
        //создаём сотрудника для изменения
        EmployeeJson json = new EmployeeJson();
        json.setFirstName("сотрудника");
        json.setLastName("меняем");
        json.setPhone("+7917654900");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        int idEmplApi = newEmployeeId(json);

        //json для изменения
        EmployeeJson jsonPatch = new EmployeeJson();
        jsonPatch.setLastName("отчествович");
        jsonPatch.setEmail("newSoap@mil.ru");

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
                .body("lastName", equalTo(jsonPatch.getLastName()))
                .body("email", equalTo(jsonPatch.getEmail()))
                .log().all();

        //получаем изменённого сотрудника и проверяем что он есть в БД
        EmployeeConst emplDBId = EMPLOYEE.getEmployee(idEmplApi);

        given()
                .log().all()
                .pathParams("id", idEmplApi)
                .header("x-client-token", TOKEN)
                .get("/employee/{id}")// пришел ответ
                .then()// работа с результатом -> проверка ответа
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Vary", "Accept-Encoding")
                .header("Content-Type", "application/json; charset=utf-8")
                .body(is(notNullValue()))
                .body("id", equalTo(emplDBId.id()))
                .body("firstName", equalTo(emplDBId.firstName()))
                .body("lastName", equalTo(emplDBId.lastName()))
                .log().all();
    }

    @Test
    @DisplayName("Список всех сотрулников (проверка количества записей)")
    public void ListEmployee() throws SQLException {
        //создадим двух сотрудников
        EmployeeJson json = new EmployeeJson();
        json.setFirstName("проверчик");
        json.setLastName("проверка");
        json.setPhone("string");
        json.setCompanyId(ID_COMPANY_TO_DELETE);

        int idEmplApi_1 = newEmployeeId(json);
        int idEmplApi_2 = newEmployeeId(json);

        assertEquals(EMPLOYEE.numOfEmployee(ID_COMPANY_TO_DELETE), 2);
    }

    static private int newEmployeeId(EmployeeJson json) {
        return given()
                .log().all()
                .body(json)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .post("/employee?company=" + ID_COMPANY_TO_DELETE)
                .then()
                .log().all()
                .statusCode(201)
                .extract().path("id");
    }
}
