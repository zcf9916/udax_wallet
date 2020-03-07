package com.github.wxiaoqi.security.common.mapper.fund;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FundProductInfoMapper extends WalletBaseMapper<FundProductInfo> {

    /**
     * 锁住某个ID
     * @param id
     */
    public FundProductInfo selectIdForUpdate(Long id);


    /**
     * 锁住某个ID
     * @param id
     */
    public FundProductInfo selectFundIdForUpdate(Long id);

    /**
     * 更新实际规模
     * @param param
     * @return
     */
    public int updateActualScale(@Param("cm")Map<String,Object> param);


    /**
     * 分页级联查询
     * @param param
     * @return
     */
    public List<FundProductInfo> selectUnionPage(@Param("cm") Map<String,Object> param);

}

