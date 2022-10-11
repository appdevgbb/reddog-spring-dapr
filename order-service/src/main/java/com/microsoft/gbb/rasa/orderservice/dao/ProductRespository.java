package com.microsoft.gbb.rasa.orderservice.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.gbb.rasa.orderservice.entities.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProductRespository
{
    @Value("classpath:${data.local.PRODUCT_DEFINITION_FILE}")
    Resource productResource;

    public ArrayList<Product> getAllProducts()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.readValue(productResource.getInputStream(), new TypeReference<ArrayList<Product>>() {});
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
