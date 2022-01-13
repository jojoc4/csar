package ch.hearc.csar;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private final String USERS_QUERY = "select email, password, true from user where email=?";
	private final String ROLES_QUERY = "select u.email, r.name from user u inner join user_role ur on (u.id = ur.user_id) inner join role r on (ur.role_id=r.id) where u.email=?";

	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable();
        http        
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/admin").hasAuthority("ADMIN")
                .antMatchers("/user").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/remove").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/edit").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/add").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/own").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/addTag").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/removeTag").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/deleteTag").hasAuthority("ADMIN")
                .antMatchers("/createTag").hasAuthority("ADMIN")
                .antMatchers("/loan-label").hasAuthority("ADMIN")
                .antMatchers("/ask").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/confirm").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/refuse").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/askback").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/back").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/account").hasAnyAuthority("USER", "ADMIN")
        		.and()
        		.formLogin().loginPage("/login").failureUrl("/login?error=true").defaultSuccessUrl("/").usernameParameter("email").passwordParameter("password")
        		.and()
        		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
        		.and()
        		.rememberMe();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.usersByUsernameQuery(USERS_QUERY)
		.authoritiesByUsernameQuery(ROLES_QUERY)
		.dataSource(dataSource)
		.passwordEncoder(bCryptPasswordEncoder);
    }
    
}
