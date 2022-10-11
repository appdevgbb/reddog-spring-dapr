package com.microsoft.gbb.rasa.orderservice.mapper;

import com.microsoft.gbb.rasa.orderservice.dto.OrderSummaryDto;
import com.microsoft.gbb.rasa.orderservice.model.OrderSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderSummaryMapper extends EntityMapper<OrderSummaryDto, OrderSummary> {

    @Mapping(ignore = true, target = "orderId")
    OrderSummary toEntity(OrderSummaryDto dto);

    @Mapping(target = "orderDateInstant", source = "")
    @Mapping(ignore = true, target = "orderId")
    OrderSummaryDto toDto(OrderSummary entity);
}