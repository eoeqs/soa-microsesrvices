package org.fergoeqs.repository;

import org.fergoeqs.dto.FilterRequestDTO;
import org.fergoeqs.model.Address;
import org.fergoeqs.model.Coordinates;
import org.fergoeqs.model.Organization;
import org.fergoeqs.specification.OrganizationSpecifications;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Stateless
public class OrganizationRepository {

    @PersistenceContext(unitName = "organizationPU")
    private EntityManager entityManager;

    @Inject
    private OrganizationSpecifications organizationSpecifications;

    public List<Organization> findAll() {
        return entityManager.createQuery("SELECT o FROM Organization o", Organization.class)
                .getResultList();
    }

    public Organization save(Organization organization) {
        if (organization.getId() == null) {
            entityManager.persist(organization);
            return organization;
        } else {
            return entityManager.merge(organization);
        }
    }

    public Optional<Organization> findById(Long id) {
        Organization organization = entityManager.find(Organization.class, id);
        return Optional.ofNullable(organization);
    }

    public void deleteById(Long id) {
        Organization organization = entityManager.find(Organization.class, id);
        if (organization != null) {
            entityManager.remove(organization);
        }
    }

    public void delete(Organization organization) {
        entityManager.remove(entityManager.contains(organization) ? organization : entityManager.merge(organization));
    }

    public Optional<Organization> findFirstByPostalAddressStreet(String street) {
        TypedQuery<Organization> query = entityManager.createQuery(
                "SELECT o FROM Organization o WHERE o.postalAddress.street = :street ORDER BY o.id",
                Organization.class
        );
        query.setParameter("street", street);
        query.setMaxResults(1);
        List<Organization> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Long countByPostalAddressStreetLessThan(String street) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(o) FROM Organization o WHERE o.postalAddress.street < :street",
                Long.class
        );
        query.setParameter("street", street);
        return query.getSingleResult();
    }

    public Long countByPostalAddressStreet(String street) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(o) FROM Organization o WHERE o.postalAddress.street = :street",
                Long.class
        );
        query.setParameter("street", street);
        return query.getSingleResult();
    }

    public List<Object[]> countOrganizationsByFullName() {
        return entityManager.createQuery(
                "SELECT o.fullName, COUNT(o) FROM Organization o GROUP BY o.fullName",
                Object[].class
        ).getResultList();
    }

    public boolean existsByFullName(String fullName) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(o) FROM Organization o WHERE o.fullName = :fullName",
                Long.class
        );
        query.setParameter("fullName", fullName);
        return query.getSingleResult() > 0;
    }

    public Optional<Organization> findByFullName(String fullName) {
        TypedQuery<Organization> query = entityManager.createQuery(
                "SELECT o FROM Organization o WHERE o.fullName = :fullName",
                Organization.class
        );
        query.setParameter("fullName", fullName);
        List<Organization> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Organization> searchWithFilter(FilterRequestDTO filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
        Root<Organization> root = cq.from(Organization.class);

        Join<Organization, Address> addressJoin = root.join("postalAddress", JoinType.INNER);
        Join<Organization, Coordinates> coordinatesJoin = root.join("coordinates", JoinType.INNER);

        Predicate predicate = organizationSpecifications.buildPredicate(root, cb, filterRequest.filters());
        if (predicate != null) {
            cq.where(predicate);
        }

        List<Order> orders = organizationSpecifications.buildOrders(cb, root, filterRequest.sort());
        if (!orders.isEmpty()) {
            cq.orderBy(orders);
        }

        TypedQuery<Organization> query = entityManager.createQuery(cq);

        Integer page = filterRequest.page() != null ? filterRequest.page() : 0;
        Integer size = filterRequest.size() != null ? filterRequest.size() : 20;

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }
    public Long countWithFilter(FilterRequestDTO filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Organization> root = cq.from(Organization.class);

        Predicate predicate = organizationSpecifications.buildPredicate(root, cb, filterRequest.filters());
        if (predicate != null) {
            cq.where(predicate);
        }

        cq.select(cb.count(root));

        return entityManager.createQuery(cq).getSingleResult();
    }
}