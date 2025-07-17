package com.logistics.financial.mapper;

import com.logistics.financial.dto.BillingDTO;
import com.logistics.financial.model.Billing;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillingMapper {
    @Mapping(target = "clientName", ignore = true)
    BillingDTO toDto(Billing billing);
    List<BillingDTO> toDtoList(List<Billing> billings);
    Billing toEntity(BillingDTO billingDTO);
}