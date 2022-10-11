package com.microsoft.gbb.rasa.orderservice.mapper;

import com.microsoft.gbb.rasa.orderservice.dto.OrderItemSummaryDto;
import com.microsoft.gbb.rasa.orderservice.model.OrderItemSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemSummaryMapper extends EntityMapper<OrderItemSummaryDto, OrderItemSummary> {
}