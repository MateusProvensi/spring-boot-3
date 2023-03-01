package br.com.provensi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.provensi.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

}
