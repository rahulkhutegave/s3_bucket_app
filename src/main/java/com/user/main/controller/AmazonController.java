package com.user.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.user.main.service.BucketService;

@RestController
@RequestMapping("/bucket")
public class AmazonController {

	@Autowired
	private BucketService service;

	@Autowired
	private AmazonController(BucketService service) {
		this.service = service;
	}

	@PostMapping("/uploadFile")
	public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
		return this.service.uploadFile(file);
	}
	
	@GetMapping("/downloadFile/{fileName}")
	public String downloadTxtFile(@PathVariable String fileName) {
		return this.service.downloadFile(fileName);
	}

}
