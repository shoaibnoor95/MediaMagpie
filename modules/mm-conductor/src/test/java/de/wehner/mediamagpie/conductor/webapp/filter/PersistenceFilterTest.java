package de.wehner.mediamagpie.conductor.webapp.filter;

import static org.junit.Assert.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import de.wehner.mediamagpie.conductor.webapp.filter.PersistenceFilter;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;


public class PersistenceFilterTest {

    private PersistenceFilter _filter;
    private PersistenceService _persistenceService;
    private ServletRequest _request;
    private HttpServletResponse _response;
    private FilterChain _filterChain;

    @Before
    public void setUp() {
        _filter = new PersistenceFilter();
        _persistenceService = mock(PersistenceService.class);
        ReflectionTestUtils.setField(_filter, "_persistenceService", _persistenceService);
        _request = mock(ServletRequest.class);
        _response = mock(HttpServletResponse.class);
        _filterChain = mock(FilterChain.class);
    }

    @Test
    public void testDoFilter_rethrowException() throws IOException, ServletException {
        setUpDoFilterThrowsException();

        try {
            _filter.doFilter(_request, _response, _filterChain);
            fail("expected exception");
        } catch (ServletException e) {
            // expected
        }

        verify(_persistenceService).beginTransaction();
        verify(_persistenceService).rollbackTransaction();
    }

    private void setUpDoFilterThrowsException() throws IOException, ServletException {
        doThrow(new IOException()).when(_filterChain).doFilter(any(ServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void testDoFilter_commitBeforeRedirect() throws IOException, ServletException {
        setUpDoFilterSendsRedirect("/newPage");

        _filter.doFilter(_request, _response, _filterChain);

        InOrder inOrder = inOrder(_persistenceService, _response);
        inOrder.verify(_persistenceService).beginTransaction();
        inOrder.verify(_persistenceService).commitTransaction();
        inOrder.verify(_response).sendRedirect("/newPage");
    }

    @SuppressWarnings("unchecked")
    private void setUpDoFilterSendsRedirect(final String destination) throws IOException, ServletException {
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                HttpServletResponse response = (HttpServletResponse) invocation.getArguments()[1];
                response.sendRedirect(destination);
                return null;
            }
        }).when(_filterChain).doFilter(any(ServletRequest.class), any(HttpServletResponse.class));
    }
}
