Feature: Agglomeration weather

  Scenario: Get agglomeration weather
    Given Location 'Gdynia' has temperature 8.6, pressure 920 and humidity 50
    Given Location 'Gdańsk' has temperature 8.7, pressure 935 and humidity 53
    Given Location 'Sopot' has temperature 9.0, pressure 933 and humidity 51
    When I request weather for agglomeration called 'Trójmiasto'
    Then I receive agglomeration weather with temperature 8.76666667, pressure 929.33333333 and humidity 51.33333333

  Scenario: Get agglomeration weather when location weather is empty
    Given Location 'Gdynia' has no weather details
    Given Location 'Gdańsk' has temperature 8.7, pressure 935 and humidity 53
    Given Location 'Sopot' has temperature 9.0, pressure 933 and humidity 51
    When I request weather for agglomeration called 'Trójmiasto'
    Then I receive error response with status 500 and message "Failed to retrieve location 'Gdynia' weather. Third party service didn't not send enough data"

  Scenario: Get agglomeration without specifying agglomeration
    When I request weather without specifying agglomeration
    Then I receive error response with status 400 and message "Required request parameter 'agglomeration' for method parameter type String is not present"

  Scenario: Get agglomeration weather when location weather has no name
    Given Location 'Gdynia' has temperature 8.6, pressure 920 and humidity 50 but has no name in the response
    Given Location 'Gdańsk' has temperature 8.7, pressure 935 and humidity 53
    Given Location 'Sopot' has temperature 9.0, pressure 933 and humidity 51
    When I request weather for agglomeration called 'Trójmiasto'
    Then I receive agglomeration weather with temperature 8.76666667, pressure 929.33333333 and humidity 51.33333333

  Scenario: Get agglomeration weather when Open Weather Map server is responding longer than 30 seconds
    Given Location 'Gdynia' has temperature 8.6, pressure 920 and humidity 50 but the response is timed out
    Given Location 'Gdańsk' has temperature 8.7, pressure 935 and humidity 53
    Given Location 'Sopot' has temperature 9.0, pressure 933 and humidity 51
    When I request weather for agglomeration called 'Trójmiasto'
    Then I receive error response with status 500 and message "Failed to retrieve location weather required to calculate agglomeration weather"

  Scenario: Get agglomeration weather for non existing agglomeration
    When I request weather for agglomeration called 'Mazury'
    Then I receive error response with status 400 and message "Failed to retrieve agglomeration weather data. Agglomeration 'Mazury' is unknown"

  Scenario: Get agglomeration weather when agglomeration don't have locations configured
    When I request weather for agglomeration called 'Metropolia_Górnośląska'
    Then I receive error response with status 500 and message "Failed to retrieve agglomeration weather. Configuration of the agglomeration 'METROPOLIA_GÓRNOŚLĄSKA' locations is missing"