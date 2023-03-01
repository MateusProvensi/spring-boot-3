package br.com.provensi.controllers;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.provensi.data.vo.v1.UploadFileResponseVO;
import br.com.provensi.exceptions.MyFileNotFoundException;
import br.com.provensi.services.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping(value = "/api/file/v1")
public class FileController {
	private Logger logger = Logger.getLogger(FileController.class.getName());

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("Storing file to disk");

		var filename = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/api/file/v1/downloadFile/")
				.path(filename)
				.toUriString();

		return new UploadFileResponseVO(filename, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponseVO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		logger.info("Storing Multiple files to disk");

		return Arrays.asList(files).stream().map(this::uploadFile).toList();
	}

	@PostMapping("/downloadFile/{filename:.+}")
	public
			ResponseEntity<Resource>
			downloadFile(@PathVariable("filename") String filename, HttpServletRequest request) {
		logger.info("Storing file to disk");

		Resource resource = fileStorageService.loadFileAsResource(filename);
		String contentType = "";

		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception e) {
			logger.info("Could not determine file type");
		}

		if (contentType.isBlank()) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
