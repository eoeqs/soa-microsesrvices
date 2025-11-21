package org.fergoeqs.service;

import org.fergoeqs.dto.*;
import org.fergoeqs.exception.ResourceNotFoundException;
import org.fergoeqs.mapper.OrganizationMapper;
import org.fergoeqs.model.Organization;
import org.fergoeqs.repository.OrganizationRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class OrganizationServiceImpl implements OrganizationServiceRemote {

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private OrganizationMapper mapper;

    @Override
    public String test() {
        return "ejb работает, тварь";
    }

    @Override
    public OrganizationResponseDTO createOrganization(OrganizationRequestDTO organizationDTO) {
        if (organizationRepository.existsByFullName(organizationDTO.fullName())) {
            throw new IllegalArgumentException("Organization with fullName '" + organizationDTO.fullName() + "' already exists");
        }

        Organization organization = mapper.toEntity(organizationDTO);
        Organization saved = organizationRepository.save(organization);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public OrganizationResponseDTO getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        return mapper.toResponseDTO(organization);
    }

    @Override
    public OrganizationResponseDTO updateOrganization(Long id, OrganizationRequestDTO organizationDTO) {
        Organization existing = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));

        if (!existing.getFullName().equals(organizationDTO.fullName()) &&
                organizationRepository.existsByFullName(organizationDTO.fullName())) {
            throw new IllegalArgumentException("Organization with fullName '" + organizationDTO.fullName() + "' already exists");
        }

        existing.setName(organizationDTO.name());
        existing.setCoordinates(mapper.toCoordinatesEntity(organizationDTO.coordinates()));
        existing.setAnnualTurnover(organizationDTO.annualTurnover());
        existing.setFullName(organizationDTO.fullName());
        existing.setType(organizationDTO.type());
        existing.setPostalAddress(mapper.toAddressEntity(organizationDTO.postalAddress()));

        Organization updated = organizationRepository.save(existing);
        return mapper.toResponseDTO(updated);
    }

    @Override
    public void deleteOrganization(Long id) {
        if (!organizationRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Organization", id);
        }
        organizationRepository.deleteById(id);
    }

    @Override
    public void deleteOrganizationByAddress(String street) {
        Organization organization = organizationRepository.findFirstByPostalAddressStreet(street)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found with address: " + street));
        organizationRepository.delete(organization);
    }

    @Override
    public Map<String, Long> groupOrganizationsByFullName() {
        List<Object[]> results = organizationRepository.countOrganizationsByFullName();
        Map<String, Long> resultMap = new HashMap<>();

        for (Object[] result : results) {
            String fullName = (String) result[0];
            Long count = (Long) result[1];
            resultMap.put(fullName, count);
        }

        return resultMap;
    }

    @Override
    public Long countOrganizationsByAddressLessThan(String street) {
        return organizationRepository.countByPostalAddressStreetLessThan(street);
    }

    @Override
    public boolean existsByFullName(String fullName) {
        return organizationRepository.existsByFullName(fullName);
    }

    @Override
    public PaginatedResponseDTO searchOrganizationsWithSorting(FilterRequestDTO filterRequest) {
        try {
            // Базовая реализация - возвращаем все организации с пагинацией
            List<Organization> allOrganizations = organizationRepository.findAll();

            // Применяем простую пагинацию
            int page = filterRequest.page() != null ? filterRequest.page() : 0;
            int size = filterRequest.size() != null ? filterRequest.size() : 20;

            int start = page * size;
            int end = Math.min(start + size, allOrganizations.size());

            List<Organization> pagedOrganizations = allOrganizations.subList(start, end);

            List<OrganizationResponseDTO> organizationDTOs = pagedOrganizations.stream()
                    .map(mapper::toResponseDTO)
                    .collect(Collectors.toList());

            return new PaginatedResponseDTO(
                    organizationDTOs,
                    (int) Math.ceil((double) allOrganizations.size() / size),
                    (long) allOrganizations.size(),
                    page,
                    organizationDTOs.size()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error during organization search: " + e.getMessage(), e);
        }
    }
}
