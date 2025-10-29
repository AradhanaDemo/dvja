package com.appsecco.dvja.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private EntityManager em;
    private Query mockQuery;

    @Before
    public void setUp() {
        userService = new UserService();
        em = mock(EntityManager.class);
        mockQuery = mock(Query.class);

        // default query behaviour
        when(em.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(anyInt())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        userService.setEntityManager(em);
    }

    @Test
    public void findByLogin_usesParameterizedQuery_noConcatenation() {
        String malicious = "admin' OR '1'='1";

        userService.findByLogin(malicious);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(captor.capture());
        String usedQuery = captor.getValue();

        // Expect parameterized JPQL, not concatenation of user input
        assertEquals("SELECT u FROM User u WHERE u.login = :login", usedQuery);

        // Ensure setParameter is called with the :login parameter
        verify(mockQuery).setParameter(eq("login"), eq(malicious));
    }

    @Test
    public void findByLoginUnsafe_behavesSame_way_parameterized() {
        String malicious = "' OR 1=1 --";

        userService.findByLoginUnsafe(malicious);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(captor.capture());
        String usedQuery = captor.getValue();

        assertEquals("SELECT u FROM User u WHERE u.login = :login", usedQuery);
        verify(mockQuery).setParameter(eq("login"), eq(malicious));
    }
}
