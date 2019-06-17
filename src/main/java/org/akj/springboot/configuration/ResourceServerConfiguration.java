package org.akj.springboot.configuration;

import org.akj.springboot.common.exception.TechnicalException;
import org.akj.springboot.exception.RestAccessDeniedHandler;
import org.akj.springboot.exception.RestAuthenticationEntryPoint;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.ResourceUtils;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Value("${security.oauth2.resource.verifier-key.file-path}")
	private String publicKeyFilePath;

	private TokenStore tokenStore;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/uua/oauth/**").permitAll().antMatchers("/**").authenticated().and()
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
				.authenticationEntryPoint(authenticationEntryPoint());
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenStore(tokenStore()).authenticationEntryPoint(authenticationEntryPoint())
				.accessDeniedHandler(accessDeniedHandler());
	}

	@Bean
	@Primary
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		String publicKey = null;

		try {
			Resource resource = null;
			if (publicKeyFilePath.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
				resource = new FileUrlResource(publicKeyFilePath);
			} else {
				resource = new ClassPathResource(publicKeyFilePath);
			}

			publicKey = IOUtils.toString(resource.getInputStream());

		} catch (Exception e) {
			TechnicalException ex = new TechnicalException("ERROR-020-001", e.getMessage());
			throw ex;
		}

		converter.setVerifierKey(publicKey);
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		if (tokenStore == null) {
			tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
		}
		return tokenStore;
	}

	@Bean
	RestAccessDeniedHandler accessDeniedHandler() {
		return new RestAccessDeniedHandler();
	}

	@Bean
	RestAuthenticationEntryPoint authenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}
}