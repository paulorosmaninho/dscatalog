package com.devsuperior.dscatalog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public List<RoleDTO> findAll() {

		List<Role> listRoleEntity = roleRepository.findAll();

		List<RoleDTO> listRoleDTO = new ArrayList<>();

		listRoleEntity.forEach(entityElement -> listRoleDTO.add(new RoleDTO(entityElement)));

		return listRoleDTO;

	}

	@Transactional(readOnly = true)
	public RoleDTO findById(Long id) {

		Optional<Role> objOptional = roleRepository.findById(id);

		Role entity = objOptional.orElseThrow(() -> new ResourceNotFoundException("Role " + id + " não encontrada"));

		return new RoleDTO(entity);

	}

	@Transactional
	public RoleDTO insert(RoleDTO roleDTO) {

		Role entity = new Role();

		copyDtoToEntity(roleDTO, entity);

		entity = roleRepository.save(entity);

		return new RoleDTO(entity);

	}

	@Transactional
	public RoleDTO update(Long id, RoleDTO roleDTO) {

		try {
			Role entity = roleRepository.getById(id);

			copyDtoToEntity(roleDTO, entity);

			entity = roleRepository.save(entity);

			return new RoleDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Role " + id + " não encontrada");
		}
	}

	public void delete(Long id) {

		try {
			roleRepository.deleteById(id);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Role " + id + " não encontrada");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	private void copyDtoToEntity(RoleDTO dto, Role entity) {

		entity.setAuthority(dto.getAuthority());
	}

}
