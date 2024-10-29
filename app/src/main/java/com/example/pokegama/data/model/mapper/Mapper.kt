package com.example.pokegama.data.model.mapper

interface Mapper<Entity, DTO> {
    fun toEntity(dto: DTO): Entity

    fun toEntityList(dtos: List<DTO>): List<Entity>

    fun toDTO(entity: Entity): DTO
}