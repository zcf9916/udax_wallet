package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.TransferList;
import com.github.wxiaoqi.security.common.entity.front.TransferOrder;
import com.github.wxiaoqi.security.common.mapper.front.TransferListMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class TransferListBiz extends BaseBiz<TransferListMapper, TransferList> {



    public List<TransferList> getListUnionUserInfo(TransferList param){
       return mapper.selectUnionUserInfo(param);
    }

}
