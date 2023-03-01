package br.com.provensi.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.provensi.data.vo.v1.BookVO;
import br.com.provensi.data.vo.v1.PersonVO;
import br.com.provensi.mapper.DozerMapper;
import br.com.provensi.model.Book;
import br.com.provensi.model.Person;
import br.com.provensi.unittests.mapper.mocks.MockBook;
import br.com.provensi.unittests.mapper.mocks.MockPerson;

public class DozerConverterTest {
    
    MockPerson inputPerson;
    MockBook inputBook;

    @BeforeEach
    public void setUp() {
        inputPerson = new MockPerson();
        inputBook = new MockBook();
    }

    @Test
    public void parsePersonEntityToVOTest() {
        PersonVO output = DozerMapper.parseObject(inputPerson.mockEntity(), PersonVO.class);
        assertEquals(Long.valueOf(0L), output.getKey());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Addres Test0", output.getAddress());
        assertEquals("Male", output.getGender());
    }
    
    @Test
    public void parseBookEntityToVOTest() {
    	BookVO output = DozerMapper.parseObject(inputBook.mockEntity(), BookVO.class);
        assertEquals(Long.valueOf(0L), output.getKey());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals("Title Test0", output.getTitle());
        assertEquals(10.5, output.getPrice());
    }

    @Test
    public void parsePersonEntityListToVOListTest() {
        List<PersonVO> outputList = DozerMapper.parseListObjects(inputPerson.mockEntityList(), PersonVO.class);
        PersonVO outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getKey());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Addres Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        
        PersonVO outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getKey());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Addres Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        
        PersonVO outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getKey());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Addres Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }

    @Test
    public void parseBookEntityListToVOListTest() {
        List<BookVO> outputList = DozerMapper.parseListObjects(inputBook.mockEntityList(), BookVO.class);
        BookVO outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getKey());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals("Title Test0", outputZero.getTitle());
        assertEquals(10.5, outputZero.getPrice());
        
        BookVO outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getKey());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals("Title Test7", outputSeven.getTitle());
        assertEquals(10.5, outputSeven.getPrice());
        
        BookVO outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getKey());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals("Title Test12", outputTwelve.getTitle());
        assertEquals(10.5, outputTwelve.getPrice());
    }

    @Test
    public void parsePersonVOToEntityTest() {
        Person output = DozerMapper.parseObject(inputPerson.mockVO(), Person.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Addres Test0", output.getAddress());
        assertEquals("Male", output.getGender());
    }
    
    @Test
    public void parseBookVOToEntityTest() {
        Book output = DozerMapper.parseObject(inputBook.mockVO(), Book.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals("Title Test0", output.getTitle());
        assertEquals(10.5, output.getPrice());
    }

    @Test
    public void parserPersonVOListToEntityListTest() {
        List<Person> outputList = DozerMapper.parseListObjects(inputPerson.mockVOList(), Person.class);
        Person outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Addres Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        
        Person outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Addres Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        
        Person outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Addres Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
    
    @Test
    public void parserBookVOListToEntityListTest() {
        List<Book> outputList = DozerMapper.parseListObjects(inputBook.mockVOList(), Book.class);
        Book outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals("Title Test0", outputZero.getTitle());
        assertEquals(10.5, outputZero.getPrice());
        
        Book outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals("Title Test7", outputSeven.getTitle());
        assertEquals(10.5, outputSeven.getPrice());
        
        Book outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals("Title Test12", outputTwelve.getTitle());
        assertEquals(10.5, outputTwelve.getPrice());
    }

}
