package org.fergoeqs.repository;

import org.fergoeqs.model.Organization;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class OrganizationRepository {

    @PersistenceContext(unitName = "organizationPU")
    private EntityManager entityManager;

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

}