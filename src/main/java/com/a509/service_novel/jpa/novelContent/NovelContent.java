package com.a509.service_novel.jpa.novelContent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.a509.service_novel.dto.NovelContentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "novel_content")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NovelContent {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int novelId;
	private String content;
	private int step;
	private String query;
	private String image;
	private String caption;
	private int authorId;

	public NovelContentDto toDto() {
		return NovelContentDto.builder()
			.content(content)
			.step(step)
			.query(query)
			.image(image)
			.caption(caption)
			.build();
	}
}
