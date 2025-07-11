package com.logistics.financial.mapper;

import com.logistics.financial.dto.ExpenseDTO;
import com.logistics.financial.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    ExpenseDTO toDto(Expense expense);
    List<ExpenseDTO> toDtoList(List<Expense> expenses);
    Expense toEntity(ExpenseDTO expenseDTO);
}