package org.fergoeqs.mapper;

import org.fergoeqs.dto.*;
import org.fergoeqs.model.*;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrganizationMapper {

    public Organization toEntity(OrganizationRequestDTO dto) {
        if (dto == null) return null;

        Organization organization = new Organization();
        organization.setName(dto.name());
        organization.setCoordinates(toCoordinatesEntity(dto.coordinates()));
        organization.setAnnualTurnover(dto.annualTurnover());
        organization.setFullName(dto.fullName());
        organization.setType(dto.type());
        organization.setPostalAddress(toAddressEntity(dto.postalAddress()));
        return organization;
    }

    public OrganizationResponseDTO toResponseDTO(Organization organization) {
        if (organization == null) return null;

        return new OrganizationResponseDTO(
                organization.getId(),
                organization.getName(),
                toCoordinatesDTO(organization.getCoordinates()),
                organization.getAnnualTurnover(),
                organization.getFullName(),
                organization.getType(),
                toAddressDTO(organization.getPostalAddress()),
                organization.getCreationDate()
        );
    }

    public Coordinates toCoordinatesEntity(CoordinatesDTO dto) {
        if (dto == null) return null;
        return new Coordinates(dto.x(), dto.y());
    }

    public CoordinatesDTO toCoordinatesDTO(Coordinates coordinates) {
        if (coordinates == null) return null;
        return new CoordinatesDTO(coordinates.getX(), coordinates.getY());
    }

    public Address toAddressEntity(AddressDTO dto) {
        if (dto == null) return null;
        return new Address(dto.street());
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) return null;
        return new AddressDTO(address.getStreet());
    }
}