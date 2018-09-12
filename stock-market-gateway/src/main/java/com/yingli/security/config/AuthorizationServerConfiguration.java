package com.yingli.security.config;

import com.yingli.security.exception.CustomWebResponseExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static final int ACCESS_TOKEN_VALIDITYSECONDS = 3600 * 4;//access_token有效时长（秒）
    private static final int REFRESH_TOKEN_VALIDITYSECONDS = 3600 * 24 * 8;//refresh_token有效时长（秒）

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;

    protected OAuth2RequestFactory requestFactory;

    /**
     * tokenStore实现
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        //return new JdbcTokenStore(dataSource);
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * clientDetails实现
     * @return
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore());
        return store;
    }

    /**
     * 配置客户端细节服务
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource).clients(this.clientDetails());
    }

    /**
     * 配置授权和令牌端点和令牌服务
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
        endpoints.tokenStore(tokenStore());
        endpoints.userDetailsService(userService);

        CustomDefaultTokenServices tokenServices = new CustomDefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.SECONDS.toSeconds(ACCESS_TOKEN_VALIDITYSECONDS));
        tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.SECONDS.toSeconds(REFRESH_TOKEN_VALIDITYSECONDS));
        endpoints.tokenServices(tokenServices);
        endpoints.exceptionTranslator(customWebResponseExceptionTranslator);//指定自定义异常转换

        //endpoints.accessTokenConverter(accessTokenConverter());
        endpoints.tokenGranter(tokenGranter());
    }

    /*@Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }*/

    private OAuth2RequestFactory requestFactory() {
        if (requestFactory != null) {
            return requestFactory;
        }
        requestFactory = new DefaultOAuth2RequestFactory(clientDetails());
        return requestFactory;
    }

    private TokenGranter tokenGranter() throws Exception {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        ClientDetailsService clientDetailsService = clientDetails();
        tokenGranters.add(new RefreshTokenGranter(tokenServices(), clientDetailsService, requestFactory()));//刷新token授权
        ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(tokenServices(), clientDetailsService, requestFactory());
        clientCredentialsTokenGranter.setAllowRefresh(true);
        tokenGranters.add(clientCredentialsTokenGranter);//客户端授权模式
        tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices(), clientDetailsService, requestFactory()));//密码授权模式
        TokenGranter tokenGranter = new CompositeTokenGranter(tokenGranters);
        return tokenGranter;
    }

    @Bean
    @Primary
    public CustomDefaultTokenServices tokenServices() {
        CustomDefaultTokenServices tokenServices = new CustomDefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        return tokenServices;
    }
}
