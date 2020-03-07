package com.udax.front.vo.reqvo.ud;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Getter
@Setter
public class QueueListModel extends PageInfo {

    @DateTimeFormat
    private String beginDate;

    @DateTimeFormat
    private String endDate;

}
