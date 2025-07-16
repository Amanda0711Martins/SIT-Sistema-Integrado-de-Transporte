package com.logistics.financial.mapper;

import com.logistics.financial.dto.InvoiceDTO;
import com.logistics.financial.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDTO toDto(Invoice invoice);
    List<InvoiceDTO> toDtoList(List<Invoice> invoices);
    Invoice toEntity(InvoiceDTO invoiceDTO);
}