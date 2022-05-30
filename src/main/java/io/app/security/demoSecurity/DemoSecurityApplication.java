package io.app.security.demoSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoSecurityApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoSecurityApplication.class, args);
	}
}
@Configuration
class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// using our oun defined authentication Provider
		// auth.authenticationProvider(new myAuthenticationProvider());
		// TODO add a proper Authentication Manager
		 // this way we have to respect Spring boot and all its requirements
		List<GrantedAuthority> myAuthorities = new ArrayList<>();
		myAuthorities.add(new SimpleGrantedAuthority("user"));
		InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
		inMemoryUserDetailsManager.createUser(new User("omar","12345",myAuthorities));
		auth.userDetailsService(inMemoryUserDetailsManager).passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
	}
}

/**
 *
 * in this case we are using our own AuthenticationProvider
 * that will result in us having to make our own system to fetch user data ...
 * by using an authentication manager we have to respect Spring boots way in implementing stuff
 *
 */
@Component
class myAuthenticationProvider implements AuthenticationProvider{
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		List<GrantedAuthority> myAuthorities = new ArrayList<>();
		myAuthorities.add(new SimpleGrantedAuthority("user"));
		return new UsernamePasswordAuthenticationToken("og","og", myAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}

@Controller
class BasicController {
	@GetMapping(name="home", value = "/home")
	public String home(Model model){
		model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
		return "home";
	}
}