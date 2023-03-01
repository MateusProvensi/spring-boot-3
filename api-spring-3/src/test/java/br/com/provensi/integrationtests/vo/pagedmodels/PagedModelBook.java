package br.com.provensi.integrationtests.vo.pagedmodels;

import java.util.List;

import br.com.provensi.integrationtests.vo.BookVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelBook {

	public PagedModelBook() {
	}

	@XmlElement(name = "content")
	private List<BookVO> content;

	public List<BookVO> getContent() {
		return content;
	}

	public void setContent(List<BookVO> content) {
		this.content = content;
	}

}
