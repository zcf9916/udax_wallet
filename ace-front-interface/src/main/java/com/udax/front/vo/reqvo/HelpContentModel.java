package com.udax.front.vo.reqvo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HelpContentModel implements Serializable{

	@NotBlank(message="{TYPEID_IS_NULL}")
	private Long id;
}
