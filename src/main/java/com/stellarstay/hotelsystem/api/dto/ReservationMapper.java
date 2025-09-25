package com.stellarstay.hotelsystem.api.dto;

import com.stellarstay.hotelsystem.domain.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationMapper {
    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomType", source = "room.type")
    ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomType", source = "room.type")
    ReservationCreatedEvent toEvent(Reservation reservation);
}
