// src/main/java/com/fraud/ui/controller/UIController.java
package com.fraud.ui.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class UIController 
{
	@Value("${api.gateway.url}")
	private String uploadUrl;

	@GetMapping("/")
	public String index() 
	{
		return "index";
	}
	
	@GetMapping("/rules")
	public String rules()
	{
		return "rules";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) 
	{
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			ByteArrayResource resource = new ByteArrayResource(file.getBytes()) 
			{
				@Override
				public String getFilename() 
				{
					return file.getOriginalFilename();
				}
			};

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", resource);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

			model.addAttribute("message", response.getBody());
		} 
		catch (Exception e)
		{
			model.addAttribute("message", "Error uploading file: " + e.getMessage());
		}
		return "index";
	}

	private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	@ResponseBody
	@GetMapping(path = "/fraud-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream() 
	{
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		return emitter;
	}

	@KafkaListener(topics = "fraud-result", groupId = "ui-group")
	public void consumeFraudResult(String message) 
	{
		System.out.println("Sending to UI: " + message);
		for (SseEmitter emitter : emitters) 
		{
			try 
			{
				emitter.send(SseEmitter.event().name("message").data(message));
				Thread.sleep(50);
			}
			catch (IOException | InterruptedException e) 
			{
				emitter.completeWithError(e);
				emitters.remove(emitter);
			}
		}
	}
}
