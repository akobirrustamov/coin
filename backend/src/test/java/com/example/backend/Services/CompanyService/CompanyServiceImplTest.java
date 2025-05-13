package com.example.backend.Services.CompanyService;

import com.example.backend.Entity.User;
import com.example.backend.Projection.DashboardProjection;
import com.example.backend.Repository.CompanyRepo;
import com.example.backend.Services.AuthService.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CompanyServiceImplTest {

    @Mock
    private CompanyRepo companyRepo;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CompanyServiceImpl underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void itShouldGetInfo() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String accessToken = "mock_access_token";

        User mockUser = new User();
        mockUser.setId(userId);

        DashboardProjection mockProjection = mock(DashboardProjection.class);
        when(authService.decode(accessToken)).thenReturn(mockUser);
        when(companyRepo.getDashboardInfo(userId)).thenReturn(mockProjection);

        // Act
        DashboardProjection response = (DashboardProjection) underTest.getInfo(accessToken).getBody();

        // Assert
        assertEquals(mockProjection, response);
        verify(authService, times(1)).decode(accessToken);
        verify(companyRepo, times(1)).getDashboardInfo(userId);
    }

    // Add more test cases as needed...
}
