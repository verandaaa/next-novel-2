package com.a509.service_novel.service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a509.service_novel.dto.ImageDto;
import com.a509.service_novel.dto.NovelContentDto;
import com.a509.service_novel.dto.NovelDetailDto;
import com.a509.service_novel.jpa.novel.Novel;
import com.a509.service_novel.jpa.novelComment.NovelComment;
import com.a509.service_novel.jpa.novelComment.NovelCommentRepogitory;
import com.a509.service_novel.jpa.novelContent.NovelContent;
import com.a509.service_novel.jpa.novelContent.NovelContentRepogitory;
import com.a509.service_novel.dto.NovelCommentDto;
import com.a509.service_novel.dto.NovelListDto;
import com.a509.service_novel.jpa.novel.NovelRepogitory;
import com.a509.service_novel.jpa.novelImage.NovelImage;
import com.a509.service_novel.jpa.novelImage.NovelImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NovelService {

	private final NovelRepogitory novelRepogitory;
	private final NovelContentRepogitory novelContentRepogitory;
	private final NovelCommentRepogitory novelCommentRepogitory;
	private final NovelImageRepository novelImageRepository;

	private final NovelImageComponent novelImageComponent;
	@Transactional
	public int insertNovel(NovelDetailDto novelDetailDto
		,MultipartFile[] startImages
		,MultipartFile[] contentImages
		,MultipartFile[] coverImages
		,String UID) throws Exception{

		List<NovelContentDto> novelContentDtos = novelDetailDto.getContents();
		System.out.println("start save content");

		Novel novel = novelDetailDto.toEntityNovel();
		novelRepogitory.save(novel);


		//content 저장
		for(int i = 0; i < novelContentDtos.size(); i++){

			NovelContentDto novelContentDto = novelContentDtos.get(i);

			String imageName = UID+"_"+contentImages[i].getOriginalFilename();
			NovelContent novelContent = novelContentDto.toEntity();
			novelContent.setImage(imageName);
			novelContent.setNovelId(novel.getId());

			System.out.println(novelContent);
			novelImageComponent.save(contentImages[i],UID);
			novelContentRepogitory.save(novelContent);
		}
		System.out.println("end save content");

		//시작 이미지 저장
		novel.setStartImage1(UID+"_"+startImages[0].getOriginalFilename());
		novel.setStartImage2(UID+"_"+startImages[1].getOriginalFilename());
		novel.setStartImage3(UID+"_"+startImages[2].getOriginalFilename());
		novel.setStartImage4(UID+"_"+startImages[3].getOriginalFilename());

		novelImageComponent.save(startImages[0],UID);
		novelImageComponent.save(startImages[1],UID);
		novelImageComponent.save(startImages[2],UID);
		novelImageComponent.save(startImages[3],UID);

		//cover
		novel.setCoverImg(UID+"_"+coverImages[0].getOriginalFilename());
		novel.setOriginCoverImg(UID+"_"+coverImages[1].getOriginalFilename());

		novelImageComponent.save(coverImages[0],UID);
		novelImageComponent.save(coverImages[1],UID);

		return novel.getId();
	}

	@Transactional
	public NovelDetailDto selectNovelDetail(int id) throws Exception{

		///소설 찾기
		Novel novel = novelRepogitory.getById(id);
		System.out.println("found novel");
		System.out.println(novel);
		//소설의 내용 찾기
		List<NovelContent> contents = novelContentRepogitory.findByNovelId(id);
		System.out.println("found contents");
		System.out.println(contents);
		List<NovelContentDto> novelContentDtos = new ArrayList<>();

		///소설 댓글 찾기
		Optional<List<NovelComment>> optionalComments = novelCommentRepogitory.findByNovelId(id);
		List<NovelComment> comments = new ArrayList<>();
		List<NovelCommentDto> commentDtos = new ArrayList<>();

		if (optionalComments.isPresent()) {
			comments = optionalComments.get();
			System.out.println("found comment");
			System.out.println(comments);
		}

		//소설 내용 저장
		for(NovelContent content : contents){

			NovelContentDto novelContentDto = content.toDto();

			System.out.println("novel is ");
			System.out.println(novelContentDto);
			novelContentDtos.add(novelContentDto);
		}
		for(NovelComment comment: comments){
			NovelCommentDto novelCommentDto = comment.toDto();
			commentDtos.add(novelCommentDto);
		}

		//찾은 소설 dto전환
		NovelDetailDto novelDetailDto = novel.toDto();

		//novelDto에 저장
		novelDetailDto.setContents(novelContentDtos);
		//찾은 코멘트 dto 저장
		novelDetailDto.setComments(commentDtos);

		return novelDetailDto;
	}

	@Transactional
	public List<NovelListDto> selectNovelList() throws Exception{
		Sort sortByCreatedAt = Sort.by("createdAt").ascending();

		try {
			List<Novel> novels = novelRepogitory.findAll(sortByCreatedAt);
			List<NovelListDto> novelDtos = new ArrayList<>();

			for (Novel novel : novels) {
				NovelListDto novelDto = novel.toListDto();
				novelDtos.add(novelDto);
			}

			return novelDtos;
		}
		catch (NoSuchElementException e){
			return null;
		}
	}

	@Transactional
	public List<NovelListDto> selectNovelsByAuthorId(String nickName) throws Exception{

		Sort sortByCreatedAt = Sort.by("createdAt").ascending();

		List<Novel> novels = novelRepogitory.findAllByNickNameOrderByCreatedAtDesc(nickName);
		List<NovelListDto> novelDtos = new ArrayList<>();

		for (Novel novel : novels) {
			NovelListDto novelDto = novel.toListDto();
			novelDtos.add(novelDto);
		}

		return novelDtos;
	}

	@Transactional
	public void deleteNovel(int id) throws Exception{
		List<NovelContent> contents = novelContentRepogitory.findByNovelId(id);
		for(NovelContent content : contents){
			novelContentRepogitory.deleteByNovelId(id);
		}
		novelRepogitory.deleteById(id);
	}

	@Transactional
	public void insertNovelImages(String nickName, MultipartFile[] startImages, MultipartFile[] contentImages,String UID) throws Exception{

		for(MultipartFile image : startImages){

			NovelImage novelImage = new NovelImage();
			novelImage.setImageName(UID+"_"+image.getOriginalFilename());
			novelImage.setNickName(nickName);
			novelImageRepository.save(novelImage);
		}

		for(MultipartFile image : contentImages){

			NovelImage novelImage = new NovelImage();
			novelImage.setImageName(UID+"_"+image.getOriginalFilename());
			novelImage.setNickName(nickName);
			novelImageRepository.save(novelImage);
		}
	}

	public List<ImageDto> selectAllNovelImage(String nickName) throws Exception{
		List<NovelImage> novelImageList = novelImageRepository.findByNickName(nickName);
		List<ImageDto> imageDtos = new ArrayList<>();
		for(NovelImage image : novelImageList){
			imageDtos.add(new ImageDto(image.getImageName()));
		}
		return imageDtos;
	}
}
