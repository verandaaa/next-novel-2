package com.a509.service_novel.controller;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a509.service_novel.dto.NovelContentDto;
import com.a509.service_novel.dto.NovelImageDto;
import com.a509.service_novel.dto.NovelListDto;
import com.a509.service_novel.jpa.novel.Novel;
import com.a509.service_novel.service.NovelImageComponent;

import com.a509.service_novel.dto.NovelDetailDto;
import com.a509.service_novel.service.NovelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/novel")
@RequiredArgsConstructor
public class NovelController {

	private final NovelService novelService;
	@GetMapping("/hello")
	public String hello(){
		return "hello";
	}

	@PostMapping()
	public ResponseEntity<?> insertNovel(@RequestPart("start_images") MultipartFile[] startImages
		,@RequestPart("content_images") MultipartFile[] contentImages
		,@RequestPart("cover_images") MultipartFile[] coverImages
		,@RequestPart("novel") NovelDetailDto novelDetailDto){
		try{
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
			String UID = now.format(formatter);
			novelService.insertNovel(novelDetailDto,startImages,contentImages,coverImages,UID);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception e){
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> selectNovel(@PathVariable("id") int id){

		try{

			NovelDetailDto novelDetailDto = novelService.selectNovelDetail(id);
			// System.out.println();


			return ResponseEntity.ok().body(novelDetailDto);
		}
		catch (Exception e){
			return new ResponseEntity<>("SQL 예외 발생", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping()
	public ResponseEntity<?> selectNovels(){
		try{
			return ResponseEntity.ok(novelService.selectNovelList());
		}
		catch(Exception e){
			return new ResponseEntity<>("SQL 예외 발생", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> selectNovelByAuthorId(@PathVariable("id") int authorId){
		try{
			List<NovelListDto> novelWritten = novelService.selectNovelsByAuthorId(authorId);
			return ResponseEntity.ok(novelService.selectNovelsByAuthorId(authorId));
		}
		catch(Exception e){
			return new ResponseEntity<>("SQL 예외 발생", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/id/")

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteNovel(@PathVariable("id") int id){
		try{
			novelService.deleteNovel(id);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception e){
			return new ResponseEntity<>("SQL 예외 발생", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
