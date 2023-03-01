package br.com.provensi.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.provensi.controllers.BookController;
import br.com.provensi.data.vo.v1.BookVO;
import br.com.provensi.exceptions.RequiredObjectIsNullException;
import br.com.provensi.exceptions.ResourceNotFoundException;
import br.com.provensi.mapper.DozerMapper;
import br.com.provensi.model.Book;
import br.com.provensi.repositories.BookRepository;

@Service
public class BookService {
	@Autowired
	private BookRepository bookRepository;

	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	public BookVO findById(Long id) {
		Book entity = bookRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		BookVO vo = DozerMapper.parseObject(entity, BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());

		return vo;
	}

	public PagedModel<EntityModel<BookVO>> findBooksByPage(Pageable pageable) {
		var booksPageables = bookRepository.findAll(pageable);

		Page<BookVO> booksVosPage = booksPageables.map(b -> DozerMapper.parseObject(b, BookVO.class));

		booksVosPage.map(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel()));

		Link link = linkTo(
				methodOn(BookController.class).findBooksByPage(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();

		return assembler.toModel(booksVosPage, link);
	}

	public BookVO create(BookVO book) {
		if (book == null) {
			throw new RequiredObjectIsNullException();
		}

		Book entity = DozerMapper.parseObject(book, Book.class);

		BookVO vo = DozerMapper.parseObject(bookRepository.save(entity), BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public BookVO update(BookVO book) {
		if (book == null) {
			throw new RequiredObjectIsNullException();
		}

		Book entity = bookRepository
				.findById(book.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		entity.setAuthor(book.getAuthor());
		entity.setTitle(book.getTitle());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());

		BookVO vo = DozerMapper.parseObject(bookRepository.save(entity), BookVO.class);

		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public void delete(Long id) {
		Book entity = bookRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		bookRepository.delete(entity);
	}
}
