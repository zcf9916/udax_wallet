package com.github.wxiaoqi.security.admin.util;

import com.github.wxiaoqi.security.admin.biz.front.BasicSymbolBiz;
import com.github.wxiaoqi.security.admin.biz.front.WhiteExchInfoBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class GetCommonDataUtil {
    @Autowired
    private WhiteExchInfoBiz exchInfoBiz;
    @Autowired
    private BasicSymbolBiz basicSymbolBiz;

    private static GetCommonDataUtil commonDataUtil;

    @PostConstruct
    public void init() {
        commonDataUtil = this;
    }

    /*获取所有交易所集合*/
    public static List<WhiteExchInfo> getWhiteExchList(){
        return (List<WhiteExchInfo>) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO_LIST);
    }

    /*获取所有代币(排除报价的代币)*/
    public static List<BasicSymbol> getSymbolList(){
        Example example = new Example(BasicSymbol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isQuote", EnableType.DISABLE.value());
        return   commonDataUtil.basicSymbolBiz.selectByExample(example);
    }
}
