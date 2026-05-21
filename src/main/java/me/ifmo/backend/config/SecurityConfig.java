package me.ifmo.backend.config;

import me.ifmo.backend.security.jaas.DatabaseLoginModule;
import me.ifmo.backend.security.jaas.JaasAuthorityGranter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String JAAS_LOGIN_CONTEXT = "application-jaas";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/courses/**").hasAuthority("COURSE_READ")

                        .requestMatchers(HttpMethod.POST, "/enrollments").hasAuthority("ENROLLMENT_CREATE")
                        .requestMatchers(HttpMethod.POST, "/enrollments/admin").hasAuthority("ENROLLMENT_CREATE_ALL")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/payments/enrollment/**").hasAuthority("PAYMENT_CREATE")
                        .requestMatchers(HttpMethod.GET, "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/payments/webhook").hasAuthority("PAYMENT_CALLBACK_HANDLE")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() throws Exception {
        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(jaasConfiguration());
        provider.setLoginContextName(JAAS_LOGIN_CONTEXT);
        provider.setAuthorityGranters(new JaasAuthorityGranter[]{new JaasAuthorityGranter()});
        provider.afterPropertiesSet();
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(List.of(authenticationProvider));
    }

    @Bean
    public javax.security.auth.login.Configuration jaasConfiguration() {
        AppConfigurationEntry entry = new AppConfigurationEntry(
                DatabaseLoginModule.class.getName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                Map.of()
        );

        return new InMemoryConfiguration(Map.of(
                JAAS_LOGIN_CONTEXT,
                new AppConfigurationEntry[]{entry}
        ));
    }
}