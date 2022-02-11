package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.controllers.exceptions.FieldMessage;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;

//Minha Classe Validator implementa a validação
// ConstraintValidator<UserUpdateValid, UserUpdateDTO>
// UserUpdateValid - É a interface, ou seja, o tipo da annotation
// UserUpdateDTO - É a classe que vai receber a annotation UserUpdateValid 
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	// Injeta a classe HttpServletRequest para receber parâmetros da URL
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserRepository repository;

	@Override
	public void initialize(UserUpdateValid ann) {

		// Aqui vai o código para inicializar algo antes do processamento principal

	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à
		// lista

		// Define var (java 10) para obter as o mapa de variáveis da URL
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		// Obtem o id da URL e atribui à userId
		long userId = Long.parseLong(uriVars.get("id"));

		List<FieldMessage> list = new ArrayList<>();

		// Testa se e-mail já está no banco
		User user = repository.findByEmail(dto.getEmail());

		if (user != null && userId != user.getId()) {
			list.add(new FieldMessage("email", "O e-mail: " + dto.getEmail() + ", já está cadastrado para outro usuário"));

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