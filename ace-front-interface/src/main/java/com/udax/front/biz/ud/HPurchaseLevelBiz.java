package com.udax.front.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.ud.HNodeAward;
import com.github.wxiaoqi.security.common.entity.ud.HPurchaseLevel;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HNodeAwardMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HPurchaseLevelMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HUserInfoMapper;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import jdk.nashorn.internal.runtime.options.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class HPurchaseLevelBiz extends BaseBiz<HPurchaseLevelMapper, HPurchaseLevel> {

    @Autowired
    private HNodeAwardMapper nodeAwardMapper;

    //得到用户能投资的最小等级
    public HPurchaseLevel getCurrentLevel(HUserInfo user){
        HPurchaseLevel levelParam = new HPurchaseLevel();
        levelParam.setLevel(user.getUserLevel());
        levelParam.setExchId(user.getExchangeId());
        HPurchaseLevel currentLevel = mapper.selectOne(levelParam);
        if(currentLevel != null){
            return currentLevel;
        }
        levelParam = new HPurchaseLevel();
        levelParam.setExchId(user.getExchangeId());
        //投资等级中最小的
        Optional<HPurchaseLevel> list =  mapper.select(levelParam).stream().min(Comparator.comparing(HPurchaseLevel::getLevel));
        return list.get();
    }

    //得到用户能投资的等级列表
    public List<HPurchaseLevel> getLevelListByCurrentLevel(HUserInfo user){
        BigDecimal currentAmount = user.getTotalAmount().add(user.getAddAmount());
        //用户当前等级·
        HPurchaseLevel levelParam = new HPurchaseLevel();
        levelParam.setExchId(user.getExchangeId());
        //筛选出满足用户投资额的等级  并且大于等级当前用户等级
        List<HPurchaseLevel> list =  mapper.select(levelParam).stream().filter((l) -> (l.getAmountLimit().compareTo(currentAmount) <= 0
         && l.getLevel().intValue() >= user.getUserLevel().intValue()) || l.getLevel().intValue() == user.getUserLevel().intValue()).collect(Collectors.toList());
        return list;
    }

    public static void main(String[] args) {

        List<HPurchaseLevel> list = InstanceUtil.newArrayList();
        HPurchaseLevel level = new HPurchaseLevel();
        level.setId(new Long (1));
        level.setAmountLimit(new BigDecimal(30));
        level.setLevel(1);

        HPurchaseLevel level1 = new HPurchaseLevel();
        level1.setId(new Long (2));
        level1.setAmountLimit(new BigDecimal(60));
        level1.setLevel(2);

        HPurchaseLevel level2 = new HPurchaseLevel();
        level2.setId(new Long (3));
        level2.setAmountLimit(new BigDecimal(90));
        level2.setLevel(3);


        HPurchaseLevel level3 = new HPurchaseLevel();
        level3.setId(new Long (4));
        level3.setAmountLimit(new BigDecimal(180));
        level3.setLevel(4);


        HPurchaseLevel level4 = new HPurchaseLevel();
        level4.setId(new Long (5));
        level4.setAmountLimit(new BigDecimal(300));
        level4.setLevel(5);

        list.add(level);
        list.add(level1);
        list.add(level2);
        list.add(level3);
        list.add(level4);



        List<HPurchaseLevel> listt =          list.stream().filter((l) -> (l.getAmountLimit().compareTo(new BigDecimal(120)) <= 0
                && l.getLevel() >= new Integer(1))).collect(Collectors.toList());

        List<HPurchaseLevel> list1t =          list.stream().filter((l) -> (l.getLevel().intValue() == new Integer(1).intValue())).collect(Collectors.toList());
        listt.addAll(list1t);
        listt = listt.stream().distinct().collect(Collectors.toList());

    }


    //根据当前投资量选出最接近的投资等级
    public HPurchaseLevel getProximalLevel(HUserInfo user){
        HPurchaseLevel levelParam = new HPurchaseLevel();
        levelParam.setExchId(user.getExchangeId());
        BigDecimal currentAmount = user.getTotalAmount().add(user.getAddAmount());
        //过滤出满足条件的
        Optional<HPurchaseLevel> list =  mapper.select(levelParam).stream().filter((l) -> l.getAmountLimit().compareTo(currentAmount) <= 0)
                .max(Comparator.comparing(HPurchaseLevel::getAmountLimit));
        return list.get();
    }


    //获取用户对应节点奖等级
    public int getNodeAward(HUserInfo userInfo){
        HNodeAward nodeParam = new HNodeAward();
        nodeParam.setExchId(userInfo.getExchangeId());
        List<HNodeAward> nodeAwardList = nodeAwardMapper.select(nodeParam).stream().sorted(Comparator.comparing(HNodeAward::getChildInvest))
                .collect(Collectors.toList());
        int count = -1;
        if(userInfo.getAddNodeAmount().compareTo(new BigDecimal(0)) != 0){
            // 投资额条件的等级
            for(HNodeAward award :nodeAwardList){
                if(award.getChildInvest().compareTo(userInfo.getChildInvest().add(userInfo.getAddNodeAmount())) > 0){
                    return count;
                }
                count++;
            }

            return count;
        } else {
            //满足邀请有效人数  以及   投资额条件的等级
            for(HNodeAward award :nodeAwardList){
                if(award.getChildInvest().compareTo(userInfo.getChildInvest()) > 0  || award.getChildNum() > userInfo.getAllChild()){
                    return count;
                }
                count++;
            }
            return count;
        }
    }


}