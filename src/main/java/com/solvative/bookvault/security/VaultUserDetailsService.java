package com.solvative.bookvault.security;

import com.solvative.bookvault.repository.VaultUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VaultUserDetailsService implements UserDetailsService {

	private final VaultUserRepository vaultUserRepository;

	public VaultUserDetailsService(VaultUserRepository vaultUserRepository) {
		this.vaultUserRepository = vaultUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return vaultUserRepository.findByEmail(email)
				.map(u -> User.withUsername(u.getEmail())
						.password(u.getPasswordHash())
						.authorities("ROLE_" + u.getRole().name())
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}

