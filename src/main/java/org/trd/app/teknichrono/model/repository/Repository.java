package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Repository<E extends PanacheEntity, D> {

  E findById(Long id);

  /**
   * Returns up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
   *
   * @param pageIndex the index (0-based) of the result page you want to get.
   *                  If <code>null</code>, then the first page (index 0) is assumed.
   * @param pageSize  the size of each page.
   *                  If <code>null</code>, <code>Integer.MAX_VALUE</code> is assumed.
   * @return up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
   */
  Stream<E> findAll(Integer pageIndex, Integer pageSize);

  void persist(E entity);

  void deleteById(long id) throws NotFoundException;

  String getEntityName();

  E create(D dto) throws ConflictingIdException, NotFoundException;

  E fromDTO(D dto) throws ConflictingIdException, NotFoundException;

  D toDTO(E entity);

  void update(long id, D dto) throws ConflictingIdException, NotFoundException;

  E findByField(String fieldName, Object fieldValue);

  E ensureFindById(long id) throws NotFoundException;

  <F extends PanacheEntity> F addToOneToManyRelationship(E entity, Long fieldDtoId,
                                                         Function<E, ? extends Collection<F>> entityCollectionGetter,
                                                         BiConsumer<F, E> setterFieldEntity,
                                                         PanacheRepository<F> fieldRepository) throws NotFoundException;

  <F extends PanacheEntity> F addToManyToManyRelationship(E entity, Long fieldDtoId,
                                                          Function<E, ? extends Collection<F>> entityCollectionGetter,
                                                          Function<F, ? extends Collection<E>> fieldCollectionGetter,
                                                          PanacheRepository<F> fieldRepository) throws NotFoundException;

  <F extends PanacheEntity> F setOneToOneRelationship(E entity, Long fieldId,
                                                      BiConsumer<E, F> setterEntity,
                                                      BiConsumer<F, E> setterFieldEntity,
                                                      PanacheRepository<F> fieldRepository) throws NotFoundException;
}
