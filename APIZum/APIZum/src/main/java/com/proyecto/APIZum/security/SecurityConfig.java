package com.proyecto.APIZum.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// =========================
//  Orquesta la seguridad
// =========================

@Configuration
public class SecurityConfig {

    private final RSAKeyProperties rsaKeys;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(RSAKeyProperties rsaKeys,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.rsaKeys = rsaKeys;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    // Codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Gestor de autentificación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception { return config.getAuthenticationManager(); }


    // Verifica los tokens (clave pública)
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }


    // Genera tokens firmados y válidos (clave privada)
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    // Maneja la seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // Sin CSRF: API stateless con JWT, no usa cookies de sesión
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Públicos
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/registro/cuenta").permitAll()

                        // USER: Perfil
                        .requestMatchers(HttpMethod.GET, "/usuarios/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/").hasRole("USER")

                        // USER: Cuentas
                        .requestMatchers(HttpMethod.GET, "/cuentas/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/cuentas/").hasRole("USER")

                        // USER: Transacciones
                        .requestMatchers(HttpMethod.POST, "/transacciones/").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/transacciones/").hasRole("USER")

                        // ADMIN: Usuarios
                        .requestMatchers(HttpMethod.GET, "/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/*").hasRole("ADMIN")

                        // ADMIN: Cuentas
                        .requestMatchers(HttpMethod.GET, "/cuentas/*").hasRole("ADMIN")

                        // ADMIN: Transacciones
                        .requestMatchers(HttpMethod.GET, "/transacciones/usuario/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/transacciones/cuenta/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/transacciones/*").hasRole("ADMIN")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                // Solo JWT: sin sesiones ni cookies
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                // Errores personalizados en JSON
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }


    // Convertidor de roles JWT → formato Spring Security (ROLE_USER, ROLE_ADMIN)
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        // Nombre del claim en el JWT donde se guardan los roles (TokenService: .claim("roles", roles))
        converter.setAuthoritiesClaimName("roles");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
