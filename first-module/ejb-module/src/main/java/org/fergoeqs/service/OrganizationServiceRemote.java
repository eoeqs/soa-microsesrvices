    package org.fergoeqs.service;

    import org.fergoeqs.dto.*;

    import jakarta.ejb.Remote;
    import java.util.Map;

    @Remote
    public interface OrganizationServiceRemote {
        OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationDTO);
        OrganizationResponseDTO getOrganizationById(Long id);
        OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO organizationDTO);
        void deleteOrganization(Long id);
        void deleteOrganizationByAddress(String street);
        Map<String, Long> groupOrganizationsByFullName();
        Long countOrganizationsByAddressLessThan(String street);
        boolean existsByFullName(String fullName);
        PaginatedResponseDTO searchOrganizationsWithSorting(FilterRequestDTO filterRequest);
        String test();
    }