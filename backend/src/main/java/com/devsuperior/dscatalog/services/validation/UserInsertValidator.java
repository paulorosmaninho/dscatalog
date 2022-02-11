package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.controllers.exceptions.FieldMessage;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;

//Minha Classe Validator implementa a validação
// ConstraintValidator<UserInsertValid, UserInsertDTO>
// UserInsertValid - É a interface, ou seja, o tipo da annotation
// UserInsertDTO - É a classe que vai receber a annotation UserInsertValid 
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired
	private UserRepository repository;

	@Override
	public void initialize(UserInsertValid ann) {

		// Aqui vai o código para inicializar algo antes do processamento principal

	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à
		// lista

		List<FieldMessage> list = new ArrayList<>();

		// Testa se e-mail já está no banco
		User user = repository.findByEmail(dto.getEmail());

		if (user != null) {
			list.add(new FieldMessage("email", "Esse e-mail já está cadastrado"));

		}

		// Esse for() adiciona as mensagens de erros que foram geradas acima
		// no contexto do Beans Validation para que ela seja interceptada pelo
		// ResourceExceptionHandler e gravada na saída

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}