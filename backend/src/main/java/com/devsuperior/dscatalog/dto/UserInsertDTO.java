package com.devsuperior.dscatalog.dto;

import javax.validation.constraints.NotBlank;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;

// @UserInsertValid - Annotation customizada para validar e-mail duplicado
@UserInsertValid
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "A senha do usuário é um campo obrigatório")
	private String password;

	public UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
