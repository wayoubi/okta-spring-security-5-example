package com.okta.developer.oidc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

@Configuration
class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.antMatchers("/").permitAll()
            .antMatchers("/authenticated/**").authenticated()
            .antMatchers("/userinfo/**").authenticated()
            .antMatchers("/administrator/**").hasAuthority("ROLE_admins")
            .and().oauth2Login().userInfoEndpoint().userAuthoritiesMapper(this.userAuthoritiesMapper());
    }
    
    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
        	Set<SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority)authority;
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    if (userInfo.containsClaim("groups")){                    	
                    	String temp = userInfo.getClaimAsString("groups");
                    	temp = temp.substring(1, temp.length()-1);
                    	StringTokenizer tokenizer = new StringTokenizer(temp, ",");
                    	while(tokenizer.hasMoreTokens()) {
                    		String roleName = "ROLE_" + tokenizer.nextToken().trim();
                    		mappedAuthorities.add(new SimpleGrantedAuthority(roleName));
                    	}
                    }
                } else if (OAuth2UserAuthority.class.isInstance(authority)) {
                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority)authority;
                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                    if (userAttributes.containsKey("groups")){                  
                        String temp = (String)userAttributes.get("groups");
                    	temp = temp.substring(1, temp.length()-1);
                    	StringTokenizer tokenizer = new StringTokenizer(temp, ",");
                    	while(tokenizer.hasMoreTokens()) {
                    		String roleName = "ROLE_" + tokenizer.nextToken().trim();
                    		mappedAuthorities.add(new SimpleGrantedAuthority(roleName));
                    	} 
                    }
                }
            });
            return mappedAuthorities;
        };
    }
}
