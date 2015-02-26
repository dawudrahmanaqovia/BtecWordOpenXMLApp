package com.pearson.app.dao;


import com.pearson.app.model.Transformation;
import com.pearson.app.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Repository class for the Meal entity
 *
 */
@Repository
public class TransformationRepository implements TransformationDAOInterface {

    private final Logger LOGGER = LoggerFactory.getLogger(TransformationRepository.class);

    @PersistenceContext
    EntityManager em;


    /**
     * create new transformation
     */
    public void addTransformation(Transformation transformation) {
        em.persist(transformation);
    }

    /**
     * list of transformation
     * @return -  a list of matching meals, or an empty collection if no match found
     */
    public List<Transformation> listTransformations() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Transformation> searchQuery = cb.createQuery(Transformation.class);
        Root<Transformation> searchRoot = searchQuery.from(Transformation.class);

        List<Order> orderList = new ArrayList();
//        orderList.add(cb.desc(searchRoot.get("date")));
//        orderList.add(cb.asc(searchRoot.get("time")));
        orderList.add(cb.asc(searchRoot.get("id")));
        searchQuery.orderBy(orderList);

        TypedQuery<Transformation> filterQuery = em.createQuery(searchQuery);
        return filterQuery.getResultList();
    }


    /**
     * finds a transformation given its id
     */
    public Transformation getTransformationById(Long id) {
        return em.find(Transformation.class, id);
    }



    /**
     * save changes made to a transformation, or create the transformation if its a new transformation.
     */
    public void updateTransformation(Transformation transformation) {
        em.merge(transformation);
    }


    /**
     * Delete a transformation, given its identifier
     * @param transformId - the id of the transformation to be deleted
     */
    public void removeTransformation(Long transformId) {
        Transformation delete = em.find(Transformation.class, transformId);
        em.remove(delete);
    }

    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Transformation> searchRoot, Date fromDate, Date toDate,
                                                Time fromTime, Time toTime) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Transformation, User> user = searchRoot.join("user");

        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("date"), fromDate));

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("date"), toDate));
        }

        return predicates.toArray(new Predicate[]{});
    }


    /**
     *
     * counts the matching transformations, given the bellow criteria
     *
     * @return -  a list of matching transformations, or an empty collection if no match found
     */
    public Long countAllTransformations() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Transformation> countRoot = cq.from(Transformation.class);
        cq.select((cb.count(countRoot)));
        Long resultsCount = em.createQuery(cq).getSingleResult();

        LOGGER.info("Found " + resultsCount + " results.");

        return resultsCount;
    }


    /**
     *
     * finds all list of transformation
     *
     * @return -  a paginated list of matching meals, or an empty collection if no match found
     */
    public List<Transformation> listTransformation(int pageNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Transformation> searchQuery = cb.createQuery(Transformation.class);
        Root<Transformation> searchRoot = searchQuery.from(Transformation.class);

        List<Order> orderList = new ArrayList();
        orderList.add(cb.desc(searchRoot.get("date")));
        //orderList.add(cb.asc(searchRoot.get("time")));
        searchQuery.orderBy(orderList);

        TypedQuery<Transformation> filterQuery = em.createQuery(searchQuery)
                .setFirstResult((pageNumber - 1) * 10)
                .setMaxResults(10);

        return filterQuery.getResultList();
    }



}