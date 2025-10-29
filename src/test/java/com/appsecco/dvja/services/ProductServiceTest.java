package com.appsecco.dvja.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductService productService;
    private EntityManager em;
    private Query mockQuery;

    @Before
    public void setUp() {
        productService = new ProductService();
        em = mock(EntityManager.class);
        mockQuery = mock(Query.class);

        when(em.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        productService.setEntityManager(em);
    }

    @Test
    public void findContainingName_usesParameterizedLike_andEscapesNotAppliedHere() {
        String payload = "%'; DROP TABLE users; --";

        productService.findContainingName(payload);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(captor.capture());
        String usedQuery = captor.getValue();

        assertEquals("SELECT p FROM Product p WHERE p.name LIKE :name", usedQuery);

        // Ensure the parameter is set and contains the user supplied input wrapped with %
        verify(mockQuery).setParameter(eq("name"), eq("%" + payload + "%"));
    }
}
