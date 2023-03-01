package br.com.provensi.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.provensi.controllers.PersonController;
import br.com.provensi.data.vo.v1.PersonVO;
import br.com.provensi.data.vo.v2.PersonVOV2;
import br.com.provensi.exceptions.RequiredObjectIsNullException;
import br.com.provensi.exceptions.ResourceNotFoundException;
import br.com.provensi.mapper.DozerMapper;
import br.com.provensi.mapper.custom.PersonMapper;
import br.com.provensi.model.Person;
import br.com.provensi.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository personRepository;

	@Autowired
	PersonMapper personMapper;

	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;

	public PersonVO findById(Long id) {
		logger.info("Finding One PersonVO");

		Person entity = personRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);

		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

		return vo;
	}

	public PagedModel<EntityModel<PersonVO>> findAllPeople(Pageable pageable) {
		logger.info("Finding All People");

		var personPage = personRepository.findAll(pageable);

		Page<PersonVO> personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));

		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

		Link link = linkTo(
				methodOn(PersonController.class)
						.findPersonsByPage(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();

		return assembler.toModel(personVosPage, link);
	}

	public PagedModel<EntityModel<PersonVO>> findPeopleByFirstName(String firstName, Pageable pageable) {
		logger.info("Finding All People"); 
		
		var personPage = personRepository.findPeopleByFirstName(firstName, pageable);
		
		Page<PersonVO> personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		
		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		Link link = linkTo(
				methodOn(PersonController.class)
				.findPersonsByPage(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();
		
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO create(PersonVO person) {
		if (person == null) {
			throw new RequiredObjectIsNullException();
		}

		logger.info("Creating One PersonVO");

		Person entity = DozerMapper.parseObject(person, Person.class);

		PersonVO vo = DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);

		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public PersonVO update(PersonVO person) {
		if (person == null) {
			throw new RequiredObjectIsNullException();
		}

		logger.info("Updating One PersonVO");

		Person entity = personRepository
				.findById(person.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		PersonVO vo = DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);

		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	@Transactional
	public PersonVO disablePerson(Long id) {
		logger.info("Disabling One person!");

		personRepository.disablePerson(id);

		Person entity = personRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);

		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

		return vo;
	}

	public void delete(Long id) {
		logger.info("Deleting One PersonVO");

		Person entity = personRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		personRepository.delete(entity);
	}

	public PersonVOV2 createV2(PersonVOV2 person) {
		logger.info("Creating One PersonVO2 With V2!");

		Person entity = personMapper.convertVoToEntity(person);

		return personMapper.convertEntityToVo(personRepository.save(entity));
	}
}
