package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.mapper.admin.UserTransactionModelMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataReportBiz {
    @Autowired
    private UserTransactionModelMapper modelMapper;

    @Autowired
    private DcAssetAccountMapper dcAssetAccountMapper;

    public List<UserTransactionModel> pageFrontWithdraw(Map<String, Object> param) {
        return modelMapper.selectTotalWithdraw(param);
    }

    public List<UserTransactionModel> pageFrontTransfer(Map<String, Object> param) {
        return modelMapper.selectTransferTotal(param);
    }

    public List<UserTransactionModel> pageFrontRecharge(Map<String, Object> param) {
        return modelMapper.selectTotalRecharge(param);
    }

    public List<DcAssetAccount> pageAssetAccount(Map<String, Object> param) {
        return dcAssetAccountMapper.selectTotalAccount(param);
    }

    // 收支汇总
    public TableResultResponse<UserTransactionModel> pageInAndExReport(Map<String, Object> param) {
        HashMap<String, UserTransactionModel> resultMap = new HashMap<String, UserTransactionModel>();

        //Page<Object> result = PageHelper.startPage(Integer.parseInt(param.get("page").toString()), Integer.parseInt(param.get("limit").toString()));
        List<UserTransactionModel> incomeList = modelMapper.selectTotalIncome(param);//流水表查询总收益
        if (StringUtil.listIsNotBlank(incomeList)) {
            for (UserTransactionModel incomeModel : incomeList) {
                if (!resultMap.containsKey(incomeModel.getSymbol())) {
                    resultMap.put(incomeModel.getSymbol(), incomeModel);
                }
            }
        }

        //Page<Object> rechargeResult = PageHelper.startPage(Integer.parseInt(param.get("page").toString()), Integer.parseInt(param.get("limit").toString()));
        List<UserTransactionModel> rechargeExpendList = modelMapper.selectRechargeExpendTotal(param);//充币区块链汇聚支出
        if (StringUtil.listIsNotBlank(rechargeExpendList)) {
            for (UserTransactionModel rechargeModel : rechargeExpendList) {
                if (resultMap.containsKey(rechargeModel.getSymbol())) {
                    UserTransactionModel model = resultMap.get(rechargeModel.getSymbol());
                    model.setExpendAmount(rechargeModel.getExpendAmount());
                } else {
                    resultMap.put(rechargeModel.getSymbol(), rechargeModel);
                }
            }
        }

        //Page<Object> withdrawResult = PageHelper.startPage(Integer.parseInt(param.get("page").toString()), Integer.parseInt(param.get("limit").toString()));
        List<UserTransactionModel> withdrawExpendList = modelMapper.selectWithdrawExpendTotal(param);//提币区块链汇聚支出
        if (StringUtil.listIsNotBlank(withdrawExpendList)) {
            for (UserTransactionModel withdrawModel : withdrawExpendList) {
                if (resultMap.containsKey(withdrawModel.getSymbol())) {
                    UserTransactionModel model = resultMap.get(withdrawModel.getSymbol());
                    model.setExpendAmount(model.getExpendAmount() != null ? model.getExpendAmount().add(withdrawModel.getExpendAmount()) : withdrawModel.getExpendAmount());
                } else {
                    resultMap.put(withdrawModel.getSymbol(), withdrawModel);
                }
            }
        }
        List<UserTransactionModel> resultList = InstanceUtil.newArrayList();
        if (resultMap != null) {
            resultList = new ArrayList(resultMap.values());//map转list
            Integer pageNum = Integer.parseInt(param.get("page").toString());//当前页
            Integer pageSize = Integer.parseInt(param.get("limit").toString());// 每页数目

            //每页的集合下标的开始数
            int index = (pageNum - 1) * pageSize;
            //list的大小
            int total = resultList.size();
            //对list进行分页截取
            resultList = resultList.subList(index, total - index > pageSize ? index + pageSize : total);

            return new TableResultResponse<UserTransactionModel>(total, resultList);
        }

        return new TableResultResponse<UserTransactionModel>(resultList.size(), resultList);
    }
}
