package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

// @UserUpdateValid - Annotation customizada para validar e-mail duplicado
@UserUpdateValid
public class UserUpdateDTO extends UserDTO {

	private static final long serialVersionUID = 1L;


}
