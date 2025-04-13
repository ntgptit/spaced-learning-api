package com.spacedlearning.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthRequest.AuthRequestBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.StandardServletEnvironment;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class})
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
@EnableConfigurationProperties
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisabledInAotMode
class JwtAuthenticationFilterDiffblueTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ObjectMapper objectMapper;

    /**
     * Test {@link JwtAuthenticationFilter#JwtAuthenticationFilter(AuthenticationManager, JwtTokenProvider, ObjectMapper)}.
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#JwtAuthenticationFilter(AuthenticationManager, JwtTokenProvider, ObjectMapper)}
     */
    @Test
    @DisplayName("Test new JwtAuthenticationFilter(AuthenticationManager, JwtTokenProvider, ObjectMapper)")
    @Tag("MaintainedByDiffblue")
    void testNewJwtAuthenticationFilter() {
        // Arrange and Act
        JwtAuthenticationFilter actualJwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
                jwtTokenProvider, objectMapper);

        // Assert
        Environment environment = actualJwtAuthenticationFilter.getEnvironment();
        assertTrue(((StandardServletEnvironment) environment).getConversionService() instanceof DefaultConversionService);
        assertTrue(actualJwtAuthenticationFilter.getRememberMeServices() instanceof NullRememberMeServices);
        assertTrue(environment instanceof StandardServletEnvironment);
        assertEquals("password", actualJwtAuthenticationFilter.getPasswordParameter());
        assertEquals("username", actualJwtAuthenticationFilter.getUsernameParameter());
        assertNull(actualJwtAuthenticationFilter.getFilterConfig());
        assertEquals(0, environment.getActiveProfiles().length);
        assertEquals(1, environment.getDefaultProfiles().length);
        Map<String, Object> systemEnvironment = ((StandardServletEnvironment) environment).getSystemEnvironment();
        assertEquals(49, systemEnvironment.size());
        Map<String, Object> systemProperties = ((StandardServletEnvironment) environment).getSystemProperties();
        assertEquals(79, systemProperties.size());
        assertTrue(systemEnvironment.containsKey("ALLUSERSPROFILE"));
        assertTrue(systemEnvironment.containsKey("PROCESSOR_LEVEL"));
        assertTrue(systemEnvironment.containsKey("SESSIONNAME"));
        assertTrue(systemEnvironment.containsKey("USERDOMAIN_ROAMINGPROFILE"));
        assertTrue(systemProperties.containsKey("cover.jar.path"));
        assertTrue(systemProperties.containsKey("java.specification.version"));
        assertTrue(systemProperties.containsKey("kotlinx.coroutines.debug"));
        assertTrue(systemProperties.containsKey("sun.cpu.isalist"));
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse)")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication() throws IOException, AuthenticationException {
        // Arrange
        BearerTokenAuthenticationToken bearerTokenAuthenticationToken = new BearerTokenAuthenticationToken("ABC123");
        when(authenticationManager.authenticate(Mockito.<Authentication>any())).thenReturn(bearerTokenAuthenticationToken);
        AuthRequest buildResult = AuthRequest.builder().password("iloveyou").usernameOrEmail("janedoe").build();
        when(objectMapper.readValue(Mockito.<InputStream>any(), Mockito.<Class<AuthRequest>>any())).thenReturn(buildResult);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        Authentication actualAttemptAuthenticationResult = jwtAuthenticationFilter.attemptAuthentication(request,
                new Response());

        // Assert
        verify(objectMapper).readValue(isA(InputStream.class), isA(Class.class));
        verify(authenticationManager).authenticate(isA(Authentication.class));
        assertSame(bearerTokenAuthenticationToken, actualAttemptAuthenticationResult);
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse)")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication2() throws IOException, AuthenticationException {
        // Arrange
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenThrow(new AuthenticationServiceException("Starting authentication attempt"));
        AuthRequest buildResult = AuthRequest.builder().password("iloveyou").usernameOrEmail("janedoe").build();
        when(objectMapper.readValue(Mockito.<InputStream>any(), Mockito.<Class<AuthRequest>>any())).thenReturn(buildResult);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(AuthenticationServiceException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
        verify(objectMapper).readValue(isA(InputStream.class), isA(Class.class));
        verify(authenticationManager).authenticate(isA(Authentication.class));
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse)")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication3() throws AuthenticationException {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        JwtTokenProvider tokenProvider = new JwtTokenProvider();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, tokenProvider,
                JsonMapper.builder().findAndAddModules().build());
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(AuthenticationServiceException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <ul>
     *   <li>Given {@link AuthRequest.AuthRequestBuilder} {@link AuthRequest.AuthRequestBuilder#password(String)} return builder.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse); given AuthRequestBuilder password(String) return builder")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication_givenAuthRequestBuilderPasswordReturnBuilder()
            throws IOException, AuthenticationException {
        // Arrange
        AuthRequestBuilder authRequestBuilder = mock(AuthRequestBuilder.class);
        when(authRequestBuilder.password(Mockito.<String>any())).thenReturn(AuthRequest.builder());
        AuthRequest buildResult = authRequestBuilder.password("iloveyou").usernameOrEmail("janedoe").build();
        when(objectMapper.readValue(Mockito.<InputStream>any(), Mockito.<Class<AuthRequest>>any())).thenReturn(buildResult);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(AuthenticationServiceException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
        verify(objectMapper).readValue(isA(InputStream.class), isA(Class.class));
        verify(authRequestBuilder).password(eq("iloveyou"));
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <ul>
     *   <li>Then calls {@link MapperBuilder#findAndAddModules()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse); then calls findAndAddModules()")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication_thenCallsFindAndAddModules() throws AuthenticationException {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        Builder builder = mock(Builder.class);
        when(builder.findAndAddModules()).thenReturn(JsonMapper.builder());
        JsonMapper objectMapper = builder.findAndAddModules().build();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
                new JwtTokenProvider(), objectMapper);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(AuthenticationServiceException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
        verify(builder).findAndAddModules();
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <ul>
     *   <li>Then calls {@link AuthRequest.AuthRequestBuilder#usernameOrEmail(String)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse); then calls usernameOrEmail(String)")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication_thenCallsUsernameOrEmail() throws IOException, AuthenticationException {
        // Arrange
        AuthRequestBuilder authRequestBuilder = mock(AuthRequestBuilder.class);
        when(authRequestBuilder.usernameOrEmail(Mockito.<String>any())).thenReturn(AuthRequest.builder());
        AuthRequestBuilder authRequestBuilder2 = mock(AuthRequestBuilder.class);
        when(authRequestBuilder2.password(Mockito.<String>any())).thenReturn(authRequestBuilder);
        AuthRequest buildResult = authRequestBuilder2.password("iloveyou").usernameOrEmail("janedoe").build();
        when(objectMapper.readValue(Mockito.<InputStream>any(), Mockito.<Class<AuthRequest>>any())).thenReturn(buildResult);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(AuthenticationServiceException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
        verify(objectMapper).readValue(isA(InputStream.class), isA(Class.class));
        verify(authRequestBuilder2).password(eq("iloveyou"));
        verify(authRequestBuilder).usernameOrEmail(eq("janedoe"));
    }

    /**
     * Test {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}.
     * <ul>
     *   <li>Then throw {@link BadCredentialsException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test attemptAuthentication(HttpServletRequest, HttpServletResponse); then throw BadCredentialsException")
    @Tag("MaintainedByDiffblue")
    void testAttemptAuthentication_thenThrowBadCredentialsException() throws IOException, AuthenticationException {
        // Arrange
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenThrow(new BadCredentialsException("Starting authentication attempt"));
        AuthRequest buildResult = AuthRequest.builder().password("iloveyou").usernameOrEmail("janedoe").build();
        when(objectMapper.readValue(Mockito.<InputStream>any(), Mockito.<Class<AuthRequest>>any())).thenReturn(buildResult);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act and Assert
        assertThrows(BadCredentialsException.class,
                () -> jwtAuthenticationFilter.attemptAuthentication(request, new Response()));
        verify(objectMapper).readValue(isA(InputStream.class), isA(Class.class));
        verify(authenticationManager).authenticate(isA(Authentication.class));
    }

    /**
     * Test {@link JwtAuthenticationFilter#successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}.
     * <ul>
     *   <li>When {@link BearerTokenAuthenticationToken#BearerTokenAuthenticationToken(String)} with token is {@code ABC123}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)}
     */
    @Test
    @DisplayName("Test successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication); when BearerTokenAuthenticationToken(String) with token is 'ABC123'")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testSuccessfulAuthentication_whenBearerTokenAuthenticationTokenWithTokenIsAbc123()
            throws ServletException, IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   com.diffblue.fuzztest.shared.proxy.BeanInstantiationException: Could not instantiate bean: jwtAuthenticationFilter defined in null
        //       at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
        //       at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
        //       at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
        //       at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
        //       at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921)
        //       at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682)
        //   java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@7ba1e98b testClass = com.spacedlearning.security.DiffblueFakeClass350, locations = [], classes = [com.spacedlearning.security.JwtAuthenticationFilter], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = [], contextCustomizers = [[ImportsContextCustomizer@27bc64ba key = [@org.springframework.boot.context.properties.EnableConfigurationProperties({}), @org.springframework.context.annotation.PropertySource(name="", factory=org.springframework.core.io.support.PropertySourceFactory.class, ignoreResourceNotFound=false, encoding="", value=/* Warning type mismatch! "java.lang.String[classpath:application-test.properties]" */), @org.springframework.context.annotation.Import(value={org.springframework.boot.context.properties.EnableConfigurationPropertiesRegistrar.class}), @org.springframework.test.context.web.WebAppConfiguration("src/main/webapp"), @org.springframework.test.context.ContextConfiguration(classes={com.spacedlearning.security.JwtAuthenticationFilter.class}, inheritInitializers=true, inheritLocations=true, initializers={}, loader=org.springframework.test.context.ContextLoader.class, locations={}, name="", value={})]], org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@29e6dc6, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@5466f9c2, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@3046f0b3, org.springframework.boot.test.web.reactor.netty.DisableReactorResourceFactoryGlobalResourcesContextCustomizerFactory$DisableReactorResourceFactoryGlobalResourcesContextCustomizerCustomizer@66776684, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@1b75371e, org.springframework.test.context.web.socket.MockServerContainerContextCustomizer@218dc50c], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.test.context.web.WebDelegatingSmartContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:180)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
        //       at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
        //       at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
        //       at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
        //       at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921)
        //       at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682)
        //   org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jwtAuthenticationFilter': authenticationManager must be specified
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1806)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:975)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:971)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:625)
        //       at org.springframework.test.context.web.AbstractGenericWebContextLoader.loadContext(AbstractGenericWebContextLoader.java:228)
        //       at org.springframework.test.context.web.AbstractGenericWebContextLoader.loadContext(AbstractGenericWebContextLoader.java:105)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:212)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:225)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:152)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
        //       at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
        //       at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
        //       at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
        //       at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921)
        //       at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682)
        //   java.lang.IllegalArgumentException: authenticationManager must be specified
        //       at org.springframework.util.Assert.notNull(Assert.java:172)
        //       at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.afterPropertiesSet(AbstractAuthenticationProcessingFilter.java:191)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1853)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1802)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600)
        //       at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
        //       at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
        //       at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
        //       at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:975)
        //       at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:971)
        //       at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:625)
        //       at org.springframework.test.context.web.AbstractGenericWebContextLoader.loadContext(AbstractGenericWebContextLoader.java:228)
        //       at org.springframework.test.context.web.AbstractGenericWebContextLoader.loadContext(AbstractGenericWebContextLoader.java:105)
        //       at org.springframework.test.context.support.AbstractDelegatingSmartContextLoader.loadContext(AbstractDelegatingSmartContextLoader.java:212)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:225)
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:152)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
        //       at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
        //       at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
        //       at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
        //       at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921)
        //       at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        //       at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682)
        //   To avoid this error, consider adding a custom base class to setup static
        //   mocking for org.springframework.util.Assert.
        //   For details on how to set up a custom base class, please follow this link:
        //   https://docs.diffblue.com/features/cover-cli/writing-tests/custom-test-setup
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        FilterChain chain = mock(FilterChain.class);

        // Act
        jwtAuthenticationFilter.successfulAuthentication(request, response, chain,
                new BearerTokenAuthenticationToken("ABC123"));
    }

    /**
     * Test {@link JwtAuthenticationFilter#unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException)}.
     * <ul>
     *   <li>Then calls {@link ObjectMapper#writeValue(OutputStream, Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JwtAuthenticationFilter#unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException)}
     */
    @Test
    @DisplayName("Test unsuccessfulAuthentication(HttpServletRequest, HttpServletResponse, AuthenticationException); then calls writeValue(OutputStream, Object)")
    @Tag("MaintainedByDiffblue")
    void testUnsuccessfulAuthentication_thenCallsWriteValue() throws ServletException, IOException {
        // Arrange
        doNothing().when(objectMapper).writeValue(Mockito.<OutputStream>any(), Mockito.<Object>any());
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponseWrapper response = mock(HttpServletResponseWrapper.class);
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(new ByteArrayOutputStream(1)));
        doNothing().when(response).setContentType(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());

        // Act
        jwtAuthenticationFilter.unsuccessfulAuthentication(request, response, new AccountExpiredException("Msg"));

        // Assert
        verify(objectMapper).writeValue(isA(OutputStream.class), isA(Object.class));
        verify(response).getOutputStream();
        verify(response).setContentType(eq("application/json"));
        verify(response).setStatus(eq(401));
    }
}
