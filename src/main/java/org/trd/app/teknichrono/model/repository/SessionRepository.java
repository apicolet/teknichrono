package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Dependent
public class SessionRepository extends PanacheRepositoryWrapper<Session, SessionDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Session> {
  }

  private final Panache panacheRepository;

  private final ChronometerRepository.Panache chronometerRepository;

  private final LocationRepository.Panache locationRepository;

  private final EventRepository.Panache eventRepository;

  private final PilotRepository.Panache pilotRepository;

  @Inject
  public SessionRepository(Panache panacheRepository, ChronometerRepository.Panache chronometerRepository,
                           LocationRepository.Panache locationRepository, EventRepository.Panache eventRepository,
                           PilotRepository.Panache pilotRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.chronometerRepository = chronometerRepository;
    this.locationRepository = locationRepository;
    this.eventRepository = eventRepository;
    this.pilotRepository = pilotRepository;
  }

  public void deleteById(long id) throws NotFoundException {
    Session entity = ensureFindById(id);

    removeFromManyToManyRelationship(id, entity.getChronometers(), Chronometer::getSessions, chronometerRepository);
    removeFromManyToManyRelationship(id, entity.getPilots(), Pilot::getSessions, pilotRepository);
    removeFromManyToOneRelationship(id, entity.getLocation(), Location::getSessions, locationRepository);
    removeFromManyToOneRelationship(id, entity.getEvent(), Event::getSessions, eventRepository);

    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Session.class.getName();
  }

  @Override
  public void create(SessionDTO entity) throws ConflictingIdException, NotFoundException {
    Session session = fromDTO(entity);
    panacheRepository.persist(session);
  }

  @Override
  public Session fromDTO(SessionDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    Session session = new Session();
    session.setInactivity(dto.getInactivity());
    session.setStart(dto.getStart());
    session.setEnd(dto.getEnd());
    session.setType(dto.getType());
    session.setCurrent(dto.isCurrent());
    session.setName(dto.getName());

    setManyToManyRelationship(session, dto.getChronometers(), Session::getChronometers, Chronometer::getSessions, chronometerRepository);
    setManyToManyRelationship(session, dto.getPilots(), Session::getPilots, Pilot::getSessions, pilotRepository);
    setManyToOneRelationship(session, dto.getLocation(), Session::setLocation, Location::getSessions, locationRepository);
    setManyToOneRelationship(session, dto.getEvent(), Session::setEvent, Event::getSessions, eventRepository);

    // Create create with Pings (not even in DTO)
    return session;
  }

  @Override
  public SessionDTO toDTO(Session dto) {
    return SessionDTO.fromSession(dto);
  }

  @Override
  public void update(long id, SessionDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Session session = ensureFindById(id);

    session.setInactivity(dto.getInactivity());
    session.setStart(dto.getStart());
    session.setEnd(dto.getEnd());
    session.setType(dto.getType());
    session.setCurrent(dto.isCurrent());
    session.setName(dto.getName());

    setManyToManyRelationship(session, dto.getChronometers(), Session::getChronometers, Chronometer::getSessions, chronometerRepository);
    setManyToManyRelationship(session, dto.getPilots(), Session::getPilots, Pilot::getSessions, pilotRepository);
    setManyToOneRelationship(session, dto.getLocation(), Session::setLocation, Location::getSessions, locationRepository);
    setManyToOneRelationship(session, dto.getEvent(), Session::setEvent, Event::getSessions, eventRepository);

    panacheRepository.persist(session);
  }
}