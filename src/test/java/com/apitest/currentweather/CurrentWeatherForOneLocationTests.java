package com.apitest.currentweather;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("Current Weather Data")
@Feature("Query current weather for one location by city name")
public class CurrentWeatherForOneLocationTests {
    private static final String INVALID_KEY = "invalidKEY001";
    private static final String KEY = "d1de7d35c12fd56249aadda70d7a4b87";
    private static final String WEATHER_URI = "http://api.openweathermap.org/data/2.5/weather";

    @Issue("CWO-1")
    @DisplayName("Successfully get weather information by city name")
    @Description("When authorized user uses London, uk to query weather, the API response should be a JSON payload"
            +" including one location weather information like weather id, temperature, humidity"
            +" and other system information like correct country and city name")
    @Test
    public void getWeatherByCityName(){
        given().
                params("q","London,uk","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("weather",hasSize(1)).
                body("weather[0]", hasKey("id")).
                body("main.temp",greaterThan(173.15f),"main.humidity",lessThanOrEqualTo(100)).
                body("sys.country",equalTo("GB"),"name",equalTo("London"));
    }

    @Issue("CWO-1")
    @DisplayName("Successfully get weather information by city ID")
    @Description("When authorized user uses city ID 2172797 to query weather, the API response should be a JSON payload"
            +" including one location weather information like weather id, temperature, humidity"
            +" and other system information like correct country")
    @Test
    public void getWeatherByCityID(){
        given().
                params("id",2172797,"appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("weather",hasSize(1)).
                body("weather[0]", hasKey("id")).
                body("main.temp",greaterThan(173.15f),"main.humidity",lessThanOrEqualTo(100)).
                body("sys.country",equalTo("AU"));
    }

    @Issue("CWO-1")
    @DisplayName("Successfully get weather information by geography coordinates")
    @Description("When authorized user uses geography coordinates (latitude 35,longitude139) to query weather,"
            +" the API response should be a JSON payload"
            +" including one location weather information like weather id, temperature, humidity"
            +" and other system information like correct country")
    @Test
    public void getWeatherByGeoCoordinates() {
        given().
                params("lat", 35, "lon", 139, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("weather", hasSize(1)).
                body("weather[0]", hasKey("id")).
                body("main.temp", greaterThan(173.15f), "main.humidity", lessThanOrEqualTo(100)).
                body("sys.country", equalTo("JP"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information with invalid appid")
    @Description("When unauthorized user uses London, uk to query weather, the API response should be a JSON payload"
            +" with status code 401 and error message Invalid API KEY")
    @Test
    public void shouldNotGetWeatherByInvalidAppid(){
        given().
                params("q","London,uk","appid", INVALID_KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(401).
                contentType(ContentType.JSON).
                body("cod",equalTo(401)).
                body("message", equalTo("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info."));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information with wrong city name")
    @Description("When authorized user uses city name loodoo to query weather, the API response should be a JSON payload"
            +" with status code 404 and error message city not found")
    @Test
    public void shouldNotGetWeatherByWrongCityName(){
        given().
                params("q","loodoo","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(404).
                contentType(ContentType.JSON).
                body("cod",equalTo("404")).
                body("message", equalTo("city not found"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information with empty city name")
    @Description("When authorized user uses empty city name to query weather, the API response should be a JSON payload"
            +" with status code 400 and error message \"Nothing to geocode\"")
    @Test
    public void shouldNotGetWeatherByEmptyCityName(){
        given().
                params("q","","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod",equalTo("400")).
                body("message", equalTo("Nothing to geocode"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information with not matching city and country")
    @Description("When authorized user uses london,se to query weather, the API response should be a JSON payload"
            +" with status code 404 and error message city not found")
    @Test
    public void shouldNotGetWeatherByNotMatchedCityAndCountry(){
        given().
                params("q","london,se","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(404).
                contentType(ContentType.JSON).
                body("cod",equalTo("404")).
                body("message", equalTo("city not found"));
    }

    @Issue("CWO-1")
    @DisplayName("Get weather information with a number in city name list")
    @Description("When authorized user uses city name 123 to query weather, the API response should be a JSON payload"
            +" including one location weather information like weather id, temperature, humidity"
            +" and other system information like correct country and city name")
    //not sure why user could use number as a city name
    @Test
    public void getWeatherByCorrectNumberFormatCityName(){
        given().
                params("q","123","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("weather",hasSize(1)).
                body("weather[0]", hasKey("id")).
                body("main.temp",greaterThan(173.15f),"main.humidity",lessThanOrEqualTo(100)).
                body("sys.country",equalTo("PH"),"name",equalTo("123"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by empty city ID")
    @Description("When authorized user uses empty city ID to query weather, the API response should be a JSON payload"
            +" with status code 400 and error message \"Nothing to geocode\"")
    @Test
    public void shouldNotGetWeatherByEmptyCityID(){
        given().
                params("id","","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod",equalTo("400")).
                body("message",equalTo("Nothing to geocode"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information with wrong city ID")
    @Description("When authorized user uses city id 1234567 to query weather, the API response should be a JSON payload"
            +" with status code 404 and error message city not found")
    @Test
    public void shouldNotGetWeatherByWrongCityID(){
        given().
                params("id","1234567","appid", KEY).
                when().
                get(WEATHER_URI).
                then().
                assertThat().
                statusCode(404).
                contentType(ContentType.JSON).
                body("cod",equalTo("404")).
                body("message", equalTo("city not found"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by malformed city ID")
    @Description("When authorized user uses malformed city ID to query weather, the API response should be a JSON payload"
            +" with status code 400 and error message \"london is not a city ID\"")
    @Test
    public void shouldNotGetWeatherByMalformedCityID(){
        given().
                params("id","london","appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod",equalTo("400")).
                body("message",equalTo("london is not a city ID"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by out of range latitude")
    @Description("When authorized user uses out of range latitude -91,longitude139 to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400 and error message \"-91 is not a float\"")
    @Test
    public void shouldNotGetWeatherByOutOfRangeLatitude() {
        given().
                params("lat", -91, "lon", 139, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod", equalTo("400")).
                body("message", equalTo("-91 is not a float"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by out of range longitude")
    @Description("When authorized user uses latitude 35,out of range longitude 181 to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400 and error message \"181 is not a float\"")
    @Test
    public void shouldNotGetWeatherByOutOfRangeLongitude() {
        given().
                params("lat", 35, "lon", 181, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod", equalTo("400")).
                body("message", equalTo("181 is not a float"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by empty latitude in geocode")
    @Description("When authorized user uses empty latitude,longitude 139 to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400")
    @Test
    public void shouldNotGetWeatherByEmptyLatitude() {
        given().
                params("lat", "", "lon", 139, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod", equalTo("400"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by empty longitude in geocode")
    @Description("When authorized user uses latitude 35, empty longitude to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400")
    @Test
    public void shouldNotGetWeatherByEmptyLongitude() {
        given().
                params("lat", 35, "lon", "", "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod", equalTo("400"));
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by malformed latitude in geocode")
    @Description("When authorized user uses malformed latitude q ,longitude 139 to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400")
    @Test
    public void shouldNotGetWeatherByMalformedLatitude() {
        given().
                params("lat", "q", "lon", 139, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON);
    }

    @Issue("CWO-1")
    @DisplayName("Should not get weather information by malformed longitude in geocode")
    @Description("When authorized user uses latitude 35, malformed longitude www to query weather,"
            +" the API response should be a JSON payload"
            +" with status code 400")
    @Test
    public void shouldNotGetWeatherByMalformedLongitude() {
        String test = "www";
        given().
                params("lat", 35, "lon", test, "appid", KEY).
        when().
                get(WEATHER_URI).
        then().
                assertThat().
                statusCode(400).
                contentType(ContentType.JSON).
                body("cod", equalTo("400"));
    }
}
