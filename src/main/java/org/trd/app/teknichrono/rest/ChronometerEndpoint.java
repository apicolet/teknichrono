package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.repository.ChronometerRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/chronometers")
public class ChronometerEndpoint {

  private final EntityEndpoint<Chronometer, ChronometerDTO> entityEndpoint;

  @Inject
  public ChronometerEndpoint(ChronometerRepository chronometerRepository) {
    this.entityEndpoint = new EntityEndpoint<>(chronometerRepository);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(ChronometerDTO entity) {
    return entityEndpoint.create(entity, entity.getName());
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    return entityEndpoint.deleteById(id);
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findById(@PathParam("id") long id) {
    return entityEndpoint.findById(id);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findChronometerByName(@QueryParam("name") String name) {
    return entityEndpoint.findByField("name", name);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ChronometerDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, ChronometerDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
