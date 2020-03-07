package com.udax.front.vo.rspvo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpTypeVo implements Serializable {

	/**
	 * 帮助类型id
	 */
	private Long id;

    /**
     * 帮助标题
     */
    private String helpTitle;
}
