package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	// Injetar o BCryptPasswordEncoder que foi definido como
	// Bean no pacote config
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {

		Page<User> pageUserEntity = userRepository.findAll(pageable);

		Page<UserDTO> pageUserDTO = pageUserEntity.map(elementUserEntity -> new UserDTO(elementUserEntity));

		return pageUserDTO;

	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {

		Optional<User> objOptional = userRepository.findById(id);

		User entity = objOptional.orElseThrow(() -> new ResourceNotFoundException("User " + id + " n??o encontrado"));

		return new UserDTO(entity);

	}

	@Transactional
	public UserDTO insert(UserInsertDTO userInsertDTO) {

		User entity = new User();

		copyDtoToEntity(userInsertDTO, entity);

		// Criptografa a password antes de gravar no banco
		entity.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));

		entity = userRepository.save(entity);

		return new UserDTO(entity);

	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {

		try {
			User entity = userRepository.getById(id);

			copyDtoToEntity(userUpdateDTO, entity);

			entity = userRepository.save(entity);

			return new UserDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("User " + id + " n??o encontrado");
		}

	}

	public void delete(Long id) {
		try {

			userRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("User " + id + " n??o encontrado");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	private void copyDtoToEntity(UserDTO dto, User entity) {

		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());

		// Limpa a lista de roles da entidade
		entity.getRoles().clear();

		// Ler cada Role do DTO e adicionar no Set do Role Entidade
		for (RoleDTO roleDTO : dto.getRolesDTO()) {
			Role role = roleRepository.getById(roleDTO.getId());
			entity.getRoles().add(role);
		}

	}

	// Implementa m??todo obrigat??rio para pesquisar se usu??rio existe atrav??s do
	// e-mail
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(username);

		if (user == null) {
			logger.error("Usu??rio n??o encontrado para o e-mail: " + username);
			throw new UsernameNotFoundException("Usu??rio n??o encontrado para o e-mail: " + username);
		}

		logger.info("Usu??rio encontrado para o e-mail: " + username);

		return user;
	}

}
