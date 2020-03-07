package com.udax.front.vo.rspvo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoticeVo implements Serializable {
	
	/**
	 * 公告id
	 */
	private Long id;

	/**
	 * 公告标题
	 */
	private String noticeTitle;

	/**
	 * 公告级别（1:普通，2:重要，3:紧急）
	 */
	private Integer noticeLevel;

	/**
	 * 发布的内容信息（以文本编辑器编辑）
	 */
	private String content;

	/**
	 * 发布时间
	 */
	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date crtTime;
}
