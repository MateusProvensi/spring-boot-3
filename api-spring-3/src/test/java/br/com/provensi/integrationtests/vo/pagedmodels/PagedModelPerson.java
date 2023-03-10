package br.com.provensi.integrationtests.vo.pagedmodels;

import java.util.List;

import br.com.provensi.integrationtests.vo.PersonVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelPerson {

	public PagedModelPerson() {
	}

	@XmlElement(name = "content")
	private List<PersonVO> content;

	public List<PersonVO> getContent() {
		return content;
	}

	public void setContent(List<PersonVO> content) {
		this.content = content;
	}

}
