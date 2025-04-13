package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserResponse.UserResponseBuilder;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.RoleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.CustomUserDetails;
import com.spacedlearning.security.CustomUserDetailsService;
import com.spacedlearning.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AuthServiceImpl.class})
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
@EnableConfigurationProperties
@DisabledInAotMode
class AuthServiceImplDiffblueTest {
  @Autowired
  private AuthServiceImpl authServiceImpl;

  @MockBean
  private AuthenticationManager authenticationManager;

  @MockBean
  private CustomUserDetailsService customUserDetailsService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private MessageSource messageSource;

  @MockBean
  private RoleRepository roleRepository;

  @MockBean
  private UserMapper userMapper;

  @MockBean
  private UserRepository userRepository;

  /**
   * Test {@link AuthServiceImpl#authenticate(AuthRequest)}.
   * <ul>
   *   <li>Given {@link JwtTokenProvider}.</li>
   *   <li>Then throw {@link SpacedLearningException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#authenticate(AuthRequest)}
   */
  @Test
  @DisplayName("Test authenticate(AuthRequest); given JwtTokenProvider; then throw SpacedLearningException")
  @Tag("MaintainedByDiffblue")
  void testAuthenticate_givenJwtTokenProvider_thenThrowSpacedLearningException() {
    // Arrange
    AuthRequest request = mock(AuthRequest.class);
    when(request.getPassword()).thenThrow(SpacedLearningException.forbidden("An error occurred"));
    when(request.getUsernameOrEmail()).thenReturn("janedoe");

    // Act and Assert
    assertThrows(SpacedLearningException.class, () -> authServiceImpl.authenticate(request));
    verify(request).getPassword();
    verify(request).getUsernameOrEmail();
  }

  /**
   * Test {@link AuthServiceImpl#getUsernameFromToken(String)}.
   * <ul>
   *   <li>Then return {@code janedoe}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#getUsernameFromToken(String)}
   */
  @Test
  @DisplayName("Test getUsernameFromToken(String); then return 'janedoe'")
  @Tag("MaintainedByDiffblue")
  void testGetUsernameFromToken_thenReturnJanedoe() {
    // Arrange
    when(jwtTokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");

    // Act
    String actualUsernameFromToken = authServiceImpl.getUsernameFromToken("ABC123");

    // Assert
    verify(jwtTokenProvider).getUsernameFromToken(eq("ABC123"));
    assertEquals("janedoe", actualUsernameFromToken);
  }

  /**
   * Test {@link AuthServiceImpl#getUsernameFromToken(String)}.
   * <ul>
   *   <li>Then throw {@link SpacedLearningException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#getUsernameFromToken(String)}
   */
  @Test
  @DisplayName("Test getUsernameFromToken(String); then throw SpacedLearningException")
  @Tag("MaintainedByDiffblue")
  void testGetUsernameFromToken_thenThrowSpacedLearningException() {
    // Arrange
    when(jwtTokenProvider.getUsernameFromToken(Mockito.<String>any()))
        .thenThrow(SpacedLearningException.forbidden("An error occurred"));

    // Act and Assert
    assertThrows(SpacedLearningException.class, () -> authServiceImpl.getUsernameFromToken("ABC123"));
    verify(jwtTokenProvider).getUsernameFromToken(eq("ABC123"));
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Given {@link User} {@link User#getName()} return {@code Name}.</li>
   *   <li>Then return User DisplayName is {@code Name}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); given User getName() return 'Name'; then return User DisplayName is 'Name'")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_givenUserGetNameReturnName_thenReturnUserDisplayNameIsName() {
    // Arrange
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("jane.doe@example.org");
    when(user.getName()).thenReturn("Name");
    when(user.getUsername()).thenReturn("janedoe");
    LocalDate ofResult = LocalDate.of(1970, 1, 1);
    when(user.getCreatedAt()).thenReturn(ofResult.atStartOfDay());
    when(user.getRoles()).thenReturn(new HashSet<>());
    UUID randomUUIDResult = UUID.randomUUID();
    when(user.getId()).thenReturn(randomUUIDResult);
    doNothing().when(user).setCreatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setDeletedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setId(Mockito.<UUID>any());
    doNothing().when(user).setUpdatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setBooks(Mockito.<Set<Book>>any());
    doNothing().when(user).setEmail(Mockito.<String>any());
    doNothing().when(user).setLastActiveDate(Mockito.<LocalDateTime>any());
    doNothing().when(user).setName(Mockito.<String>any());
    doNothing().when(user).setPassword(Mockito.<String>any());
    doNothing().when(user).setRoles(Mockito.<Set<Role>>any());
    doNothing().when(user).setStatus(Mockito.<UserStatus>any());
    doNothing().when(user).setUsername(Mockito.<String>any());
    user.setBooks(new HashSet<>());
    user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    user.setId(UUID.randomUUID());
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult2 = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult2);

    ArrayList<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new RunAsImplAuthenticationProvider());
    ProviderManager authenticationManager = new ProviderManager(providers);
    JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    when(tokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(tokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(tokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    User user2 = new User();
    user2.setBooks(new HashSet<>());
    user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setEmail("jane.doe@example.org");
    user2.setId(UUID.randomUUID());
    user2.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setName("Name");
    user2.setPassword("iloveyou");
    user2.setRoles(new HashSet<>());
    user2.setStatus(UserStatus.ACTIVE);
    user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setUsername("janedoe");
    Optional<User> ofResult3 = Optional.of(user2);
    UserRepository userRepository2 = mock(UserRepository.class);
    when(userRepository2.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult3);
    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository2);
    RoleRepository roleRepository = mock(RoleRepository.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = new UserMapper(passwordEncoder, new CustomUserDetailsService(mock(UserRepository.class)));

    AuthServiceImpl authServiceImpl = new AuthServiceImpl(userRepository, roleRepository, userMapper,
        authenticationManager, tokenProvider, new AnnotationConfigReactiveWebApplicationContext(), userDetailsService);

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(user).getCreatedAt();
    verify(user).getId();
    verify(user).setCreatedAt(isA(LocalDateTime.class));
    verify(user).setDeletedAt(isA(LocalDateTime.class));
    verify(user).setId(isA(UUID.class));
    verify(user).setUpdatedAt(isA(LocalDateTime.class));
    verify(user).getEmail();
    verify(user, atLeast(1)).getName();
    verify(user, atLeast(1)).getRoles();
    verify(user).getUsername();
    verify(user).setBooks(isA(Set.class));
    verify(user).setEmail(eq("jane.doe@example.org"));
    verify(user).setLastActiveDate(isA(LocalDateTime.class));
    verify(user).setName(eq("Name"));
    verify(user).setPassword(eq("iloveyou"));
    verify(user).setRoles(isA(Set.class));
    verify(user).setStatus(eq(UserStatus.ACTIVE));
    verify(user).setUsername(eq("janedoe"));
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(userRepository2).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(tokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(tokenProvider).generateToken(isA(Authentication.class));
    verify(tokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(tokenProvider).isRefreshToken(eq("ABC123"));
    verify(tokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    assertEquals("", user3.getLastName());
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("Name", user3.getDisplayName());
    assertEquals("Name", user3.getFirstName());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult, toLocalDateResult);
    assertSame(randomUUIDResult, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Given {@link User} {@link User#getName()} return {@code null}.</li>
   *   <li>Then return User DisplayName is {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); given User getName() return 'null'; then return User DisplayName is 'null'")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_givenUserGetNameReturnNull_thenReturnUserDisplayNameIsNull() {
    // Arrange
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("jane.doe@example.org");
    when(user.getName()).thenReturn(null);
    when(user.getUsername()).thenReturn("janedoe");
    LocalDate ofResult = LocalDate.of(1970, 1, 1);
    when(user.getCreatedAt()).thenReturn(ofResult.atStartOfDay());
    when(user.getRoles()).thenReturn(new HashSet<>());
    UUID randomUUIDResult = UUID.randomUUID();
    when(user.getId()).thenReturn(randomUUIDResult);
    doNothing().when(user).setCreatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setDeletedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setId(Mockito.<UUID>any());
    doNothing().when(user).setUpdatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setBooks(Mockito.<Set<Book>>any());
    doNothing().when(user).setEmail(Mockito.<String>any());
    doNothing().when(user).setLastActiveDate(Mockito.<LocalDateTime>any());
    doNothing().when(user).setName(Mockito.<String>any());
    doNothing().when(user).setPassword(Mockito.<String>any());
    doNothing().when(user).setRoles(Mockito.<Set<Role>>any());
    doNothing().when(user).setStatus(Mockito.<UserStatus>any());
    doNothing().when(user).setUsername(Mockito.<String>any());
    user.setBooks(new HashSet<>());
    user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    user.setId(UUID.randomUUID());
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult2 = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult2);

    ArrayList<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new RunAsImplAuthenticationProvider());
    ProviderManager authenticationManager = new ProviderManager(providers);
    JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    when(tokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(tokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(tokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    User user2 = new User();
    user2.setBooks(new HashSet<>());
    user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setEmail("jane.doe@example.org");
    user2.setId(UUID.randomUUID());
    user2.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setName("Name");
    user2.setPassword("iloveyou");
    user2.setRoles(new HashSet<>());
    user2.setStatus(UserStatus.ACTIVE);
    user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setUsername("janedoe");
    Optional<User> ofResult3 = Optional.of(user2);
    UserRepository userRepository2 = mock(UserRepository.class);
    when(userRepository2.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult3);
    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository2);
    RoleRepository roleRepository = mock(RoleRepository.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = new UserMapper(passwordEncoder, new CustomUserDetailsService(mock(UserRepository.class)));

    AuthServiceImpl authServiceImpl = new AuthServiceImpl(userRepository, roleRepository, userMapper,
        authenticationManager, tokenProvider, new AnnotationConfigReactiveWebApplicationContext(), userDetailsService);

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(user).getCreatedAt();
    verify(user).getId();
    verify(user).setCreatedAt(isA(LocalDateTime.class));
    verify(user).setDeletedAt(isA(LocalDateTime.class));
    verify(user).setId(isA(UUID.class));
    verify(user).setUpdatedAt(isA(LocalDateTime.class));
    verify(user).getEmail();
    verify(user, atLeast(1)).getName();
    verify(user, atLeast(1)).getRoles();
    verify(user).getUsername();
    verify(user).setBooks(isA(Set.class));
    verify(user).setEmail(eq("jane.doe@example.org"));
    verify(user).setLastActiveDate(isA(LocalDateTime.class));
    verify(user).setName(eq("Name"));
    verify(user).setPassword(eq("iloveyou"));
    verify(user).setRoles(isA(Set.class));
    verify(user).setStatus(eq(UserStatus.ACTIVE));
    verify(user).setUsername(eq("janedoe"));
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(userRepository2).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(tokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(tokenProvider).generateToken(isA(Authentication.class));
    verify(tokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(tokenProvider).isRefreshToken(eq("ABC123"));
    verify(tokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    assertEquals("", user3.getFirstName());
    assertEquals("", user3.getLastName());
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertNull(user3.getDisplayName());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult, toLocalDateResult);
    assertSame(randomUUIDResult, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Then return User DisplayName is {@code Display Name}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); then return User DisplayName is 'Display Name'")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_thenReturnUserDisplayNameIsDisplayName() throws UsernameNotFoundException {
    // Arrange
    User user = new User();
    user.setBooks(new HashSet<>());
    user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    user.setId(UUID.randomUUID());
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult = Optional.of(user);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult);
    UserResponseBuilder builderResult = UserResponse.builder();
    LocalDate ofResult2 = LocalDate.of(1970, 1, 1);
    UserResponseBuilder firstNameResult = builderResult.createdAt(ofResult2.atStartOfDay())
        .displayName("Display Name")
        .email("jane.doe@example.org")
        .firstName("Jane");
    UUID id = UUID.randomUUID();
    UserResponseBuilder lastNameResult = firstNameResult.id(id).lastName("Doe");
    UserResponse buildResult = lastNameResult.roles(new ArrayList<>()).username("janedoe").build();
    when(userMapper.toDto(Mockito.<User>any())).thenReturn(buildResult);
    when(jwtTokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(jwtTokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(jwtTokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(jwtTokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(jwtTokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);
    HashSet<Role> roles = new HashSet<>();
    HashSet<Book> books = new HashSet<>();
    User user2 = new User("Name", "janedoe", "jane.doe@example.org", "iloveyou", UserStatus.ACTIVE, roles, books,
        LocalDate.of(1970, 1, 1).atStartOfDay());

    when(customUserDetailsService.loadUserByUsername(Mockito.<String>any()))
        .thenReturn(new CustomUserDetails(user2, new ArrayList<>()));

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(userMapper).toDto(isA(User.class));
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(customUserDetailsService).loadUserByUsername(eq("janedoe"));
    verify(jwtTokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(jwtTokenProvider).generateToken(isA(Authentication.class));
    verify(jwtTokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(jwtTokenProvider).isRefreshToken(eq("ABC123"));
    verify(jwtTokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("Display Name", user3.getDisplayName());
    assertEquals("Doe", user3.getLastName());
    assertEquals("Jane", user3.getFirstName());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult2, toLocalDateResult);
    assertSame(id, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Then return User DisplayName is empty string.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); then return User DisplayName is empty string")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_thenReturnUserDisplayNameIsEmptyString() {
    // Arrange
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("jane.doe@example.org");
    when(user.getName()).thenReturn("");
    when(user.getUsername()).thenReturn("janedoe");
    LocalDate ofResult = LocalDate.of(1970, 1, 1);
    when(user.getCreatedAt()).thenReturn(ofResult.atStartOfDay());
    when(user.getRoles()).thenReturn(new HashSet<>());
    UUID randomUUIDResult = UUID.randomUUID();
    when(user.getId()).thenReturn(randomUUIDResult);
    doNothing().when(user).setCreatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setDeletedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setId(Mockito.<UUID>any());
    doNothing().when(user).setUpdatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setBooks(Mockito.<Set<Book>>any());
    doNothing().when(user).setEmail(Mockito.<String>any());
    doNothing().when(user).setLastActiveDate(Mockito.<LocalDateTime>any());
    doNothing().when(user).setName(Mockito.<String>any());
    doNothing().when(user).setPassword(Mockito.<String>any());
    doNothing().when(user).setRoles(Mockito.<Set<Role>>any());
    doNothing().when(user).setStatus(Mockito.<UserStatus>any());
    doNothing().when(user).setUsername(Mockito.<String>any());
    user.setBooks(new HashSet<>());
    user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    user.setId(UUID.randomUUID());
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult2 = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult2);

    ArrayList<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new RunAsImplAuthenticationProvider());
    ProviderManager authenticationManager = new ProviderManager(providers);
    JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    when(tokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(tokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(tokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    User user2 = new User();
    user2.setBooks(new HashSet<>());
    user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setEmail("jane.doe@example.org");
    user2.setId(UUID.randomUUID());
    user2.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setName("Name");
    user2.setPassword("iloveyou");
    user2.setRoles(new HashSet<>());
    user2.setStatus(UserStatus.ACTIVE);
    user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setUsername("janedoe");
    Optional<User> ofResult3 = Optional.of(user2);
    UserRepository userRepository2 = mock(UserRepository.class);
    when(userRepository2.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult3);
    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository2);
    RoleRepository roleRepository = mock(RoleRepository.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = new UserMapper(passwordEncoder, new CustomUserDetailsService(mock(UserRepository.class)));

    AuthServiceImpl authServiceImpl = new AuthServiceImpl(userRepository, roleRepository, userMapper,
        authenticationManager, tokenProvider, new AnnotationConfigReactiveWebApplicationContext(), userDetailsService);

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(user).getCreatedAt();
    verify(user).getId();
    verify(user).setCreatedAt(isA(LocalDateTime.class));
    verify(user).setDeletedAt(isA(LocalDateTime.class));
    verify(user).setId(isA(UUID.class));
    verify(user).setUpdatedAt(isA(LocalDateTime.class));
    verify(user).getEmail();
    verify(user, atLeast(1)).getName();
    verify(user, atLeast(1)).getRoles();
    verify(user).getUsername();
    verify(user).setBooks(isA(Set.class));
    verify(user).setEmail(eq("jane.doe@example.org"));
    verify(user).setLastActiveDate(isA(LocalDateTime.class));
    verify(user).setName(eq("Name"));
    verify(user).setPassword(eq("iloveyou"));
    verify(user).setRoles(isA(Set.class));
    verify(user).setStatus(eq(UserStatus.ACTIVE));
    verify(user).setUsername(eq("janedoe"));
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(userRepository2).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(tokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(tokenProvider).generateToken(isA(Authentication.class));
    verify(tokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(tokenProvider).isRefreshToken(eq("ABC123"));
    verify(tokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    assertEquals("", user3.getDisplayName());
    assertEquals("", user3.getFirstName());
    assertEquals("", user3.getLastName());
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult, toLocalDateResult);
    assertSame(randomUUIDResult, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Then return User DisplayName is {@code Name}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); then return User DisplayName is 'Name'")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_thenReturnUserDisplayNameIsName() {
    // Arrange
    User user = new User();
    user.setBooks(new HashSet<>());
    LocalDate ofResult = LocalDate.of(1970, 1, 1);
    user.setCreatedAt(ofResult.atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    UUID id = UUID.randomUUID();
    user.setId(id);
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult2 = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult2);

    ArrayList<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new RunAsImplAuthenticationProvider());
    ProviderManager authenticationManager = new ProviderManager(providers);
    JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    when(tokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(tokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(tokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    User user2 = new User();
    user2.setBooks(new HashSet<>());
    user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setEmail("jane.doe@example.org");
    user2.setId(UUID.randomUUID());
    user2.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setName("Name");
    user2.setPassword("iloveyou");
    user2.setRoles(new HashSet<>());
    user2.setStatus(UserStatus.ACTIVE);
    user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setUsername("janedoe");
    Optional<User> ofResult3 = Optional.of(user2);
    UserRepository userRepository2 = mock(UserRepository.class);
    when(userRepository2.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult3);
    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository2);
    RoleRepository roleRepository = mock(RoleRepository.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = new UserMapper(passwordEncoder, new CustomUserDetailsService(mock(UserRepository.class)));

    AuthServiceImpl authServiceImpl = new AuthServiceImpl(userRepository, roleRepository, userMapper,
        authenticationManager, tokenProvider, new AnnotationConfigReactiveWebApplicationContext(), userDetailsService);

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(userRepository2).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(tokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(tokenProvider).generateToken(isA(Authentication.class));
    verify(tokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(tokenProvider).isRefreshToken(eq("ABC123"));
    verify(tokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    assertEquals("", user3.getLastName());
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("Name", user3.getDisplayName());
    assertEquals("Name", user3.getFirstName());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult, toLocalDateResult);
    assertSame(id, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}.
   * <ul>
   *   <li>Then return User DisplayName is {@code Refresh token request must not be null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#refreshToken(RefreshTokenRequest)}
   */
  @Test
  @DisplayName("Test refreshToken(RefreshTokenRequest); then return User DisplayName is 'Refresh token request must not be null'")
  @Tag("MaintainedByDiffblue")
  void testRefreshToken_thenReturnUserDisplayNameIsRefreshTokenRequestMustNotBeNull() {
    // Arrange
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("jane.doe@example.org");
    when(user.getName()).thenReturn("Refresh token request must not be null");
    when(user.getUsername()).thenReturn("janedoe");
    LocalDate ofResult = LocalDate.of(1970, 1, 1);
    when(user.getCreatedAt()).thenReturn(ofResult.atStartOfDay());
    when(user.getRoles()).thenReturn(new HashSet<>());
    UUID randomUUIDResult = UUID.randomUUID();
    when(user.getId()).thenReturn(randomUUIDResult);
    doNothing().when(user).setCreatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setDeletedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setId(Mockito.<UUID>any());
    doNothing().when(user).setUpdatedAt(Mockito.<LocalDateTime>any());
    doNothing().when(user).setBooks(Mockito.<Set<Book>>any());
    doNothing().when(user).setEmail(Mockito.<String>any());
    doNothing().when(user).setLastActiveDate(Mockito.<LocalDateTime>any());
    doNothing().when(user).setName(Mockito.<String>any());
    doNothing().when(user).setPassword(Mockito.<String>any());
    doNothing().when(user).setRoles(Mockito.<Set<Role>>any());
    doNothing().when(user).setStatus(Mockito.<UserStatus>any());
    doNothing().when(user).setUsername(Mockito.<String>any());
    user.setBooks(new HashSet<>());
    user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setEmail("jane.doe@example.org");
    user.setId(UUID.randomUUID());
    user.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setName("Name");
    user.setPassword("iloveyou");
    user.setRoles(new HashSet<>());
    user.setStatus(UserStatus.ACTIVE);
    user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user.setUsername("janedoe");
    Optional<User> ofResult2 = Optional.of(user);
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult2);

    ArrayList<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(new RunAsImplAuthenticationProvider());
    ProviderManager authenticationManager = new ProviderManager(providers);
    JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    when(tokenProvider.generateRefreshToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.generateToken(Mockito.<Authentication>any())).thenReturn("ABC123");
    when(tokenProvider.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
    when(tokenProvider.isRefreshToken(Mockito.<String>any())).thenReturn(true);
    when(tokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    User user2 = new User();
    user2.setBooks(new HashSet<>());
    user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setEmail("jane.doe@example.org");
    user2.setId(UUID.randomUUID());
    user2.setLastActiveDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setName("Name");
    user2.setPassword("iloveyou");
    user2.setRoles(new HashSet<>());
    user2.setStatus(UserStatus.ACTIVE);
    user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
    user2.setUsername("janedoe");
    Optional<User> ofResult3 = Optional.of(user2);
    UserRepository userRepository2 = mock(UserRepository.class);
    when(userRepository2.findByUsernameOrEmailWithRoles(Mockito.<String>any())).thenReturn(ofResult3);
    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository2);
    RoleRepository roleRepository = mock(RoleRepository.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserMapper userMapper = new UserMapper(passwordEncoder, new CustomUserDetailsService(mock(UserRepository.class)));

    AuthServiceImpl authServiceImpl = new AuthServiceImpl(userRepository, roleRepository, userMapper,
        authenticationManager, tokenProvider, new AnnotationConfigReactiveWebApplicationContext(), userDetailsService);

    // Act
    AuthResponse actualRefreshTokenResult = authServiceImpl.refreshToken(new RefreshTokenRequest("ABC123"));

    // Assert
    verify(user).getCreatedAt();
    verify(user).getId();
    verify(user).setCreatedAt(isA(LocalDateTime.class));
    verify(user).setDeletedAt(isA(LocalDateTime.class));
    verify(user).setId(isA(UUID.class));
    verify(user).setUpdatedAt(isA(LocalDateTime.class));
    verify(user).getEmail();
    verify(user, atLeast(1)).getName();
    verify(user, atLeast(1)).getRoles();
    verify(user).getUsername();
    verify(user).setBooks(isA(Set.class));
    verify(user).setEmail(eq("jane.doe@example.org"));
    verify(user).setLastActiveDate(isA(LocalDateTime.class));
    verify(user).setName(eq("Name"));
    verify(user).setPassword(eq("iloveyou"));
    verify(user).setRoles(isA(Set.class));
    verify(user).setStatus(eq(UserStatus.ACTIVE));
    verify(user).setUsername(eq("janedoe"));
    verify(userRepository).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(userRepository2).findByUsernameOrEmailWithRoles(eq("janedoe"));
    verify(tokenProvider).generateRefreshToken(isA(Authentication.class));
    verify(tokenProvider).generateToken(isA(Authentication.class));
    verify(tokenProvider).getUsernameFromToken(eq("ABC123"));
    verify(tokenProvider).isRefreshToken(eq("ABC123"));
    verify(tokenProvider).validateToken(eq("ABC123"));
    UserResponse user3 = actualRefreshTokenResult.getUser();
    LocalDateTime createdAt = user3.getCreatedAt();
    assertEquals("00:00", createdAt.toLocalTime().toString());
    LocalDate toLocalDateResult = createdAt.toLocalDate();
    assertEquals("1970-01-01", toLocalDateResult.toString());
    assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
    assertEquals("ABC123", actualRefreshTokenResult.getToken());
    assertEquals("Refresh token request must not be null", user3.getDisplayName());
    assertEquals("Refresh", user3.getFirstName());
    assertEquals("jane.doe@example.org", user3.getEmail());
    assertEquals("janedoe", user3.getUsername());
    assertEquals("token request must not be null", user3.getLastName());
    assertTrue(user3.getRoles().isEmpty());
    assertSame(ofResult, toLocalDateResult);
    assertSame(randomUUIDResult, user3.getId());
  }

  /**
   * Test {@link AuthServiceImpl#register(RegisterRequest)}.
   * <ul>
   *   <li>Given forbidden {@code An error occurred}.</li>
   *   <li>Then throw {@link SpacedLearningException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#register(RegisterRequest)}
   */
  @Test
  @DisplayName("Test register(RegisterRequest); given forbidden 'An error occurred'; then throw SpacedLearningException")
  @Tag("MaintainedByDiffblue")
  void testRegister_givenForbiddenAnErrorOccurred_thenThrowSpacedLearningException() {
    // Arrange
    RegisterRequest request = mock(RegisterRequest.class);
    when(request.getPassword()).thenThrow(SpacedLearningException.forbidden("An error occurred"));
    when(request.getEmail()).thenReturn("jane.doe@example.org");
    when(request.getUsername()).thenReturn("janedoe");

    // Act and Assert
    assertThrows(SpacedLearningException.class, () -> authServiceImpl.register(request));
    verify(request).getEmail();
    verify(request).getPassword();
    verify(request).getUsername();
  }

  /**
   * Test {@link AuthServiceImpl#register(RegisterRequest)}.
   * <ul>
   *   <li>Then throw {@link DataIntegrityViolationException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#register(RegisterRequest)}
   */
  @Test
  @DisplayName("Test register(RegisterRequest); then throw DataIntegrityViolationException")
  @Tag("MaintainedByDiffblue")
  void testRegister_thenThrowDataIntegrityViolationException() {
    // Arrange
    when(userRepository.existsByEmail(Mockito.<String>any()))
        .thenThrow(new DataIntegrityViolationException("Register request must not be null"));
    RegisterRequest request = RegisterRequest.builder()
        .email("jane.doe@example.org")
        .firstName("Jane")
        .lastName("Doe")
        .password("iloveyou")
        .username("janedoe")
        .build();

    // Act and Assert
    assertThrows(DataIntegrityViolationException.class, () -> authServiceImpl.register(request));
    verify(userRepository).existsByEmail(eq("jane.doe@example.org"));
  }

  /**
   * Test {@link AuthServiceImpl#validateToken(String)}.
   * <p>
   * Method under test: {@link AuthServiceImpl#validateToken(String)}
   */
  @Test
  @DisplayName("Test validateToken(String)")
  @Tag("MaintainedByDiffblue")
  void testValidateToken() {
    // Arrange
    when(jwtTokenProvider.validateToken(Mockito.<String>any())).thenThrow(new JwtException("An error occurred"));

    // Act
    boolean actualValidateTokenResult = authServiceImpl.validateToken("ABC123");

    // Assert
    verify(jwtTokenProvider).validateToken(eq("ABC123"));
    assertFalse(actualValidateTokenResult);
  }

  /**
   * Test {@link AuthServiceImpl#validateToken(String)}.
   * <ul>
   *   <li>Given {@link JwtTokenProvider} {@link JwtTokenProvider#validateToken(String)} return {@code true}.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#validateToken(String)}
   */
  @Test
  @DisplayName("Test validateToken(String); given JwtTokenProvider validateToken(String) return 'true'; then return 'true'")
  @Tag("MaintainedByDiffblue")
  void testValidateToken_givenJwtTokenProviderValidateTokenReturnTrue_thenReturnTrue() {
    // Arrange
    when(jwtTokenProvider.validateToken(Mockito.<String>any())).thenReturn(true);

    // Act
    boolean actualValidateTokenResult = authServiceImpl.validateToken("ABC123");

    // Assert
    verify(jwtTokenProvider).validateToken(eq("ABC123"));
    assertTrue(actualValidateTokenResult);
  }

  /**
   * Test {@link AuthServiceImpl#validateToken(String)}.
   * <ul>
   *   <li>Given {@link JwtTokenProvider}.</li>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#validateToken(String)}
   */
  @Test
  @DisplayName("Test validateToken(String); given JwtTokenProvider; when 'null'; then return 'false'")
  @Tag("MaintainedByDiffblue")
  void testValidateToken_givenJwtTokenProvider_whenNull_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(authServiceImpl.validateToken(null));
  }

  /**
   * Test {@link AuthServiceImpl#validateToken(String)}.
   * <ul>
   *   <li>Given {@link JwtTokenProvider}.</li>
   *   <li>When space.</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#validateToken(String)}
   */
  @Test
  @DisplayName("Test validateToken(String); given JwtTokenProvider; when space; then return 'false'")
  @Tag("MaintainedByDiffblue")
  void testValidateToken_givenJwtTokenProvider_whenSpace_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(authServiceImpl.validateToken(" "));
  }

  /**
   * Test {@link AuthServiceImpl#validateToken(String)}.
   * <ul>
   *   <li>Then throw {@link SpacedLearningException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AuthServiceImpl#validateToken(String)}
   */
  @Test
  @DisplayName("Test validateToken(String); then throw SpacedLearningException")
  @Tag("MaintainedByDiffblue")
  void testValidateToken_thenThrowSpacedLearningException() {
    // Arrange
    when(jwtTokenProvider.validateToken(Mockito.<String>any()))
        .thenThrow(SpacedLearningException.forbidden("An error occurred"));

    // Act and Assert
    assertThrows(SpacedLearningException.class, () -> authServiceImpl.validateToken("ABC123"));
    verify(jwtTokenProvider).validateToken(eq("ABC123"));
  }
}
