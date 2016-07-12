package br.com.casadocodigo.loja.daos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import br.com.casadocodigo.loja.models.User;

@Repository
public class UserDAO implements UserDetailsService {
	
	@PersistenceContext
	private EntityManager manager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			String  jpql = "select u from User u where u.login = :login";
			
			return this.manager.createQuery(jpql, User.class)
					.setParameter("login", username)
					.getSingleResult();
		} catch (NoResultException e) {
			throw new UsernameNotFoundException("O usuário " + username + " não existe!", e);
		}
	}
}