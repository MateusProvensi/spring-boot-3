package br.com.provensi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.provensi.data.vo.v1.security.AccountCredentialsVO;
import br.com.provensi.data.vo.v1.security.TokenVO;
import br.com.provensi.model.User;
import br.com.provensi.repositories.UserRepository;
import br.com.provensi.security.jwt.JwtTokenProvider;

@Service
public class AuthServices {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialsVO data) {
		try {
			String username = data.getUserName();
			String password = data.getPassword();

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

			User user = userRepository.findByUsername(username);

			if (user == null) {
				throw new UsernameNotFoundException("Username " + username + " not found!");
			}

			TokenVO tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());

			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Username/Passowrd Supplied!");
		}
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}

		TokenVO tokenResponse = tokenProvider.refreshToken(refreshToken);

		return ResponseEntity.ok(tokenResponse);
	}

}
