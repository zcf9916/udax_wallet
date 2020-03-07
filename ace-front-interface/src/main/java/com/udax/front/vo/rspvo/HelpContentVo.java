package com.udax.front.vo.rspvo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HelpContentVo implements Serializable {
	/**
     * 帮助内容id
     */
    private Long id;

    /**
     * 帮助标题
     */
    private String helpTitle;
	/**
	 * 帮助内容
	 */
	private String helpContent;
}
