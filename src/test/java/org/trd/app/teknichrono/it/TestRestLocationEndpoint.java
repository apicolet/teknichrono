package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestLocationEndpoint extends TestRestEndpoint<LocationDTO> {

  private TestRestSessionEndpoint restSession;

  public TestRestLocationEndpoint() {
    super("locations", LocationDTO.class, new ArrayList<LocationDTO>() {
      private static final long serialVersionUID = 3043219685887053856L;
    }.getClass().getGenericSuperclass());
  }

  @BeforeEach
  public void prepare() {
    this.restSession = new TestRestSessionEndpoint();
  }

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<LocationDTO> locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(0);
    create("Newbie");
    locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(1);
    create("Expert");
    locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(2);

    List<LocationDTO> someBeacons = getAllInWindow(1, 1);
    Assertions.assertThat(someBeacons.size()).isEqualTo(1);

    locations = getAll();
    for (LocationDTO beacon : locations) {
      delete(beacon.getId());
    }
    locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    create("Somewhere");
    LocationDTO locationDTO = getByName("Somewhere");
    assertThat(locationDTO.getSessions()).isNullOrEmpty();
    long id = locationDTO.getId();
    getById(id);

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elsewhere");
    modifiedLocation.setId(id);
    modifiedLocation.setMinimum(Duration.ofSeconds(60));
    modifiedLocation.setMaximum(Duration.ofSeconds(120));
    update(id, modifiedLocation);
    List<LocationDTO> locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(1);
    getByName("Somewhere", NOT_FOUND);
    LocationDTO newLocation = getByName("Elsewhere");
    Assertions.assertThat(newLocation.getId()).isEqualTo(id);
    Assertions.assertThat(newLocation.getMinimum()).isEqualTo(Duration.ofSeconds(60));
    Assertions.assertThat(newLocation.getMaximum()).isEqualTo(Duration.ofSeconds(120));

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithSession() {
    createWithSession("Somewhere", "SessionName");
    LocationDTO location = getByName("Somewhere");
    assertThat(location.getSessions()).isNotNull();
    assertThat(location.getSessions()).hasSize(1);
    assertThat(location.getName()).isEqualTo("Somewhere");
    assertThat(location.getSessions().iterator().next().getName()).isEqualTo("SessionName");

    long id = location.getId();
    location = getById(id);
    assertThat(location.getSessions()).isNotNull();
    assertThat(location.getSessions()).hasSize(1);
    assertThat(location.getName()).isEqualTo("Somewhere");
    assertThat(location.getSessions().iterator().next().getName()).isEqualTo("SessionName");
    long sessionId = location.getSessions().iterator().next().getId();

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaUpdate() {
    create("Here");
    LocationDTO b = getByName("Here");
    assertThat(b.getSessions()).isNullOrEmpty();

    long id = b.getId();

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elswere");
    modifiedLocation.setLoopTrack(true);
    modifiedLocation.setId(id);

    this.restSession.create("Session Name");
    SessionDTO sessionDto1 = this.restSession.getByName("Session Name");
    long session1Id = sessionDto1.getId();
    NestedSessionDTO nestedSession1dto = new NestedSessionDTO();
    nestedSession1dto.setId(session1Id);
    modifiedLocation.getSessions().add(nestedSession1dto);

    update(id, modifiedLocation);
    // Update twice has no impact
    update(id, modifiedLocation);

    getByName("Here", NOT_FOUND);
    LocationDTO newReturnedLocation = getByName("Elswere");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.isLoopTrack()).isEqualTo(true);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedLocation.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elswere");
    modifiedLocation.setId(id);
    modifiedLocation.setLoopTrack(false);

    this.restSession.create("Other Session Name");
    SessionDTO sessionDto2 = this.restSession.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();
    NestedSessionDTO nestedSession2dto = new NestedSessionDTO();
    nestedSession2dto.setId(session2Id);
    modifiedLocation.getSessions().add(nestedSession1dto);
    modifiedLocation.getSessions().add(nestedSession2dto);

    update(id, modifiedLocation);

    newReturnedLocation = getByName("Elswere");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.isLoopTrack()).isEqualTo(false);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaAdd() {
    create("Heaven");
    LocationDTO b = getByName("Heaven");
    long id = b.getId();

    this.restSession.create("Session Name");
    SessionDTO sessionDto1 = this.restSession.getByName("Session Name");
    long session1Id = sessionDto1.getId();

    addSession(id, session1Id);
    // Adding twice has no impact
    addSession(id, session1Id);

    LocationDTO newReturnedLocation = getByName("Heaven");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedLocation.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    this.restSession.create("Other Session Name");
    SessionDTO sessionDto2 = this.restSession.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();

    addSession(id, session2Id);

    newReturnedLocation = getByName("Heaven");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemoveSession() {
    createWithSession("Where?", "SessionName");
    LocationDTO c = getByName("Where?");
    long id = c.getId();
    long sessionId = c.getSessions().iterator().next().getId();

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Where?");
    modifiedLocation.setId(id);

    update(id, modifiedLocation);

    LocationDTO newReturnedLocation = getByName("Where?");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(0);

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }

  /**
   * ******************** Reusable *********************
   **/

  public static void addSession(long id, long sessionId) {
    given().pathParam("id", id).queryParam("sessionId", sessionId)
        .when().contentType(ContentType.JSON).post("/rest/locations/{id}/addSession")
        .then()
        .statusCode(200);
  }

  public void deleteWithSessions(long id, long... sessionIds) {
    for (Long sessionId : sessionIds) {
      this.restSession.delete(sessionId);
    }
    delete(id);
  }

  public void create(String name) {
    LocationDTO p = new LocationDTO();
    p.setName(name);
    p.setLoopTrack(true);
    create(p);
  }

  public void createWithSession(String name, String sessionName) {
    this.restSession.create(sessionName);
    SessionDTO session = this.restSession.getByName(sessionName);
    NestedSessionDTO nestedSessionDTO = new NestedSessionDTO();
    nestedSessionDTO.setId(session.getId());
    nestedSessionDTO.setName(session.getName());

    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setName(name);
    locationDTO.getSessions().add(nestedSessionDTO);

    create(locationDTO);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAll()).isNullOrEmpty();
    assertThat(this.restSession.getAll()).isNullOrEmpty();
  }
}
