package com.github.wxiaoqi.security.common.entity.front;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "front_user_info")
@Data
public class FrontUserInfo extends BaseEntity {

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "id_card")
	private String idCard;

	/**
	 * 姓
	 */
	@Column(name = "first_name")
	private String firstName;

	/**
	 * 名
	 */
	@Column(name = "real_name")
	private String realName;

	/**
	 * 是否已实名认证(0未上传，1待认证，2已认证)
	 */
	@Column(name = "is_valid")
	private Integer isValid;

	/**
	 * 绑定具体区域（按域名划分）
	 */
	@Column(name = "bind_domain")
	private String bindDomain;

	/**
	 * 邀请人推荐码
	 */
	@Column(name = "recommond_code")
	private String recommondCode;

	/**
	 * 父类Id
	 */
	@Column(name = "parent_id")
	private Long parentId;

	/**
	 * 最上级的用户ID
	 */
	@Column(name = "top_id")
	private Long topId;

	/**
	 * 层级码
	 */
	@Column(name = "level_code")
	private String levelCode;

	/**
	 * 邀请码
	 */
	@Column(name = "visit_code")
	private String visitCode;

	@Column(name = "exchange_id")
	private Long exchangeId;

	/**
	 * 是否允许提币(修改信息后在一段时间内不允许提币)
	 */
	@Column(name = "is_withdraw")
	private Integer isWithdraw;

	/**
	 * 是否开启了短信验证
	 */
	@Column(name = "is_valid_phone")
	private Integer isValidPhone;

	/**
	 * 是否开启了邮箱验证s
	 */
	@Column(name = "is_valid_email")
	private Integer isValidEmail;

	/**
	 * 国家编码
	 */
	@Column(name = "location_code")
	private String locationCode;

	/**
	 * 国家
	 */
	@Column(name = "location_country")
	private String locationCountry;

	/**
	 * 身份证正面
	 */
	@Column(name = "id_card_img_zm")
	private String idCardImgZm;

	/**
	 * 身份证反面
	 */
	@Column(name = "id_card_img_fm")
	private String idCardImgFm;


	/**
	 * 头像地址
	 */
	@Column(name = "portrait")
	private String portrait;

	private Integer level;


	@Column(name = "create_time")
	private Date createTime;

	/**
	 * 审核不通过原因
	 * 这里存的是字典表主键
	 */
	@Transient
	private DictData dictData;


	@Transient
	private FrontUser frontUser;
}