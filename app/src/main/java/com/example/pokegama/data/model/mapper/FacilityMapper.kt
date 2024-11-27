package com.example.pokegama.data.model.mapper

import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.data.model.remote.FacilityDTO

class FacilityMapper : Mapper<Facility, FacilityDTO> {
    override fun toEntity(dto: FacilityDTO): Facility {
        return Facility(
            type = dto.type,
            name = dto.name,
            faculty = dto.faculty,
            latitude = dto.latitude,
            longitude = dto.longitude,
            description = dto.description,
            isAccepted = dto.isAccepted
        )
    }

    override fun toEntityList(dtos: List<FacilityDTO>): List<Facility> {
        return dtos.map { toEntity(it) }
    }

    override fun toDTO(entity: Facility): FacilityDTO {
        return FacilityDTO(
            type = entity.type,
            name = entity.name,
            faculty = entity.faculty,
            latitude = entity.latitude,
            longitude = entity.longitude,
            description = entity.description,
            isAccepted = entity.isAccepted
        )
    }
}