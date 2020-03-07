package com.github.wxiaoqi.security.admin.rest.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.base.DictDataBiz;
import com.github.wxiaoqi.security.admin.biz.front.FrontFreezeInfoBiz;
import com.github.wxiaoqi.security.admin.biz.front.FrontUserManagerBiz;
import com.github.wxiaoqi.security.admin.vo.FrontFreezeVo;
import com.github.wxiaoqi.security.admin.vo.SymbolLockVo;
import com.github.wxiaoqi.security.admin.vo.UserValidVo;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.entity.admin.FrontFreezeInfo;
import com.github.wxiaoqi.security.common.entity.admin.FrontUserManager;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("frontUserManager")
public class FrontUserManagerController extends BaseController<FrontUserManagerBiz, FrontUserManager> {

    @Autowired
    private FrontUserManagerBiz userManagerBiz;
    @Autowired
    private FrontFreezeInfoBiz freezeInfoBiz;
    @Autowired
    private DictDataBiz dictDataBiz;

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<FrontUserManager> getList(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        //查询列表数据
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<FrontUserManager> list = userManagerBiz.selectListByExchId(params);

        //查出所有的冻结字典类型
        String language = request.getHeader("locale");
        List<DictData> dictDataList = null;
        dictDataList = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.USER_FEATURES_FREEZE+":"+language);
        if (dictDataList== null) {
            dictDataList = dictDataBiz.selectListData(Constants.DirtTypeConstant.USER_FEATURES_FREEZE, language);
        }

        for(FrontUserManager model:list) {//获取用户交易功能冻结信息
            StringBuilder freezeTypeNameBui = new StringBuilder();
            List<String> freezeTypeList = InstanceUtil.newArrayList();
            ArrayList<FrontFreezeInfo> freezeInfoList = (ArrayList<FrontFreezeInfo>) CacheUtil.getCache().hget(
                    Constants.CacheServiceType.FRONT_FREEZE_INFO + (model.getId().longValue() % Constants.REDIS_MAP_BATCH),
                    model.getId().toString());
            if(freezeInfoList==null||freezeInfoList.size()<1) {
                freezeInfoList = (ArrayList<FrontFreezeInfo>) freezeInfoBiz.cacheAndReturnFreezeInfo(model.getId());
            }
            if(StringUtil.listIsNotBlank(freezeInfoList)&&StringUtil.listIsNotBlank(dictDataList)){
                for(FrontFreezeInfo freezeInfo : freezeInfoList){
                    for(DictData data:dictDataList){
                        if(EnableType.DISABLE.value().equals(freezeInfo.getEnable())&&freezeInfo.getFreezeType().toString().equals(data.getDictValue())){
                            freezeTypeNameBui.append(data.getDictLabel()).append(",");
                            freezeTypeList.add(freezeInfo.getFreezeType().toString());
                        }
                    }
                }
            }
            model.setFreezeTypeName(freezeTypeNameBui.toString());
            model.setFreezeTypeList(freezeTypeList);
        }

        return new TableResultResponse<FrontUserManager>(result.getTotal(), list);
    }


    /**
     * 查询用户资产
     */
    @RequestMapping(value = "/pageAccounts", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<DcAssetAccount> pageAccounts(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DcAssetAccount> list = userManagerBiz.pageAccounts(params);
        return new TableResultResponse<DcAssetAccount>(result.getTotal(), list);
    }


    /**
     * 查询用户资产
     */
    @RequestMapping(value = "/pageFundAccount", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<FundAccountAssert> pageFundAccount(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<FundAccountAssert> list = userManagerBiz.pageFundAccount(params);
        return new TableResultResponse<FundAccountAssert>(result.getTotal(), list);
    }


    /**
     * 查询用户信息和商家信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/userInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUserInfo(@PathVariable Long id) {
        return userManagerBiz.selectUserInfoByUserId(id);
    }

    /**
     * 用户审核 or 商家审核
     *
     * @param entity
     * @return
     */
    @RequestMapping(value = "/{id}/userInfo", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<UserValidVo> updateUserInfo(@RequestBody UserValidVo entity) {
        userManagerBiz.updateSelectiveById(entity);
        return new ObjectRestResponse<UserValidVo>();
    }


    /**
     * 用户释放锁定资产
     * @param entity
     * @return
     */
    @RequestMapping(value = "/{userId}/freedUserAsset", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<Object> freedUserAsset(@RequestBody SymbolLockVo entity) {
        userManagerBiz.freedUserAsset(entity);
        return new ObjectRestResponse<Object>();
    }


    /**
     * 批量释放用户锁定资产
     * @param entity
     * @return
     */
    @RequestMapping(value = "/freedUserAssetALL", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<Object> freedUserAssetALL(@RequestBody SymbolLockVo entity) {
//        userManagerBiz.freedUserAsset(entity);
        Integer assetALL = userManagerBiz.freedUserAssetALL(entity.getSymbol());
        return new ObjectRestResponse<>().rel(true).status(200).msg(Resources.getMessage("CONFIG_FREED_USER_ASSET_ALL") +assetALL);
    }


    /**
     * 查询充值记录汇总
     */
    @RequestMapping(value = "/pageFrontRecharge", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageFrontRecharge(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserTransactionModel> list = userManagerBiz.pageFrontRecharge(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * 查询个人资产流水表 个人转账汇总
     */
    @RequestMapping(value = "/pageTransferOrder", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageTransferOrder(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        ArrayList<Integer> types = new ArrayList<>();
        //type:  1.转账给平台其他用户 2.收到转账
        types.add(AccountLogType.RECEIVE_TRANSFER.value());
        types.add(AccountLogType.TRANSFER.value());
        params.put("types",types);
        List<UserTransactionModel> list = userManagerBiz.pageTransferOrder(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }

    /** 个人转账明细
     * TransferOrder
     * @param params
     * @return
     */
    @RequestMapping(value = "/pageTransferDetail", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<TransferOrder> getTransferDetail(@RequestParam Map<String, Object> params) {
        //查询列表数据
        params.put("status", TransferOrderStatus.PAYED.value());
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<TransferOrder> list = userManagerBiz.selectListTransferDetail(params);
        return new TableResultResponse<TransferOrder>(result.getTotal(), list);
    }


    /** 币币交易明细
     * FrontTransferDetail
     * @param params
     * @return
     */
    @RequestMapping(value = "/pageFrontTransferDetail", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<FrontTransferDetail> pageFrontTransferDetail(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<FrontTransferDetail> list = userManagerBiz.selectListFrontTransferDetail(params);
        return new TableResultResponse<FrontTransferDetail>(result.getTotal(), list);
    }

    /**
     * 查询个人资产流水表 币币交易汇总
     */
    @RequestMapping(value = "/pageFrontTransfer", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageFrontTransfer(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        ArrayList<Integer> types = new ArrayList<>();
        //type:  6.转换币支出 7.转换币收入
        types.add(AccountLogType.TRANS_COIN_PAY.value());
        types.add(AccountLogType.TRANS_COIN_INCOME.value());
        params.put("types", types);
        List<UserTransactionModel> list = userManagerBiz.pageTransferOrder(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * 查询个人资产流水表 商户交易汇总
     */
    @RequestMapping(value = "/pageMerchant", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageMerchant(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        ArrayList<Integer> types = new ArrayList<>();
        //type:  9.用户给商户支付款项 10.商户收到用户支付的款项
        types.add(AccountLogType.PAY_MERCHANT.value());
        types.add(AccountLogType.MERCHANT_SETTLE.value());
        params.put("types",types);
        List<UserTransactionModel> list = userManagerBiz.pageTransferOrder(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * 充币明细
     * @param params
     * @return
     */
    @RequestMapping(value = "/pageRechargeDetail", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> getRechargeDetail(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserTransactionModel> list = userManagerBiz.selectListRechargeDetail(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * 查询提币记录汇总
     */
    @RequestMapping(value = "/pageFrontWithdraw", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageFrontWithdraw(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        params.put("isStatus", FrontWithdrawStatus.TransError.value());
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserTransactionModel> list = userManagerBiz.pageFrontWithdraw(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * 提币明细
     * @param params
     * @return
     */
    @RequestMapping(value = "/pageFrontWithdrawDetail", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<FrontWithdraw> pageFrontWithdrawDetail(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        //处理用户详情中,提币明细记录 排除后台审核失败的状态
        params.put("isStatus", FrontWithdrawStatus.TransError.value());
        List<FrontWithdraw> list = userManagerBiz.pageFrontWithdrawDetail(params);
        return new TableResultResponse<FrontWithdraw>(result.getTotal(), list);
    }

    /**
     * 冻结用户功能
     * @param id
     * @return
     */
    @RequestMapping(value = "updateFunction", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateFunction(@RequestBody FrontFreezeVo freezeVo) {
        if(freezeVo.getUserId() == null)
        return new ObjectRestResponse().status(ResponseCode.FREEZE_USER_ISNULL);
        Long currentUserId = BaseContextHandler.getUserID();
        FrontFreezeInfo freezeInfo = new FrontFreezeInfo();
        freezeInfo.setFreezeTypeStr(StringUtils.join(freezeVo.getFreezeTypeStr(),","));
        freezeInfo.setUserId(freezeVo.getUserId());
        freezeInfo.setUpdName(currentUserId.toString());
        freezeInfoBiz.update(freezeInfo);
        return  new ObjectRestResponse();
    }

    /**
     * 查询个人资产流水表 排单汇总
     */
    @RequestMapping(value = "/pageUD", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<UserTransactionModel> pageUD(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        ArrayList<Integer> types = new ArrayList<>();
        //type: 16.ud社区排队冻结  17.ud社区解冻  18.ud社区返回利润
        //	 19.ud 解锁支出  20.UD扣除手续费 21 UD社区分成 22节点奖分成 23 超级用户全球利润分配
        //        24 UD社区充值赠送
        types.add(AccountLogType.UD_FREEZE.value());
        types.add(AccountLogType.UD_UNFREEZE.value());
        types.add(AccountLogType.UD_PROFIT.value());
        types.add(AccountLogType.UD_UNLOCK.value());
        types.add(AccountLogType.UD_CHARGE.value());
        types.add(AccountLogType.UD_CMS.value());
        types.add(AccountLogType.NODE_AWARD.value());
        types.add(AccountLogType.GLOBAL_AWARD.value());
        types.add(AccountLogType.UD_REG_SEND.value());
        params.put("types", types);
        List<UserTransactionModel> list = userManagerBiz.pageTransferOrder(params);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     * UD社区流水明细
     */
    @RequestMapping(value = "/pageUDDetail", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<DcAssetAccountLog> pageUDDetail(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        ArrayList<Integer> types = new ArrayList<>();
        //type: 16.ud社区排队冻结  17.ud社区解冻  18.ud社区返回利润
        //	 19.ud 解锁支出  20.UD扣除手续费 21 UD社区分成 22节点奖分成 23 超级用户全球利润分配
        //        24 UD社区充值赠送
        types.add(AccountLogType.UD_FREEZE.value());
        types.add(AccountLogType.UD_UNFREEZE.value());
        types.add(AccountLogType.UD_PROFIT.value());
        types.add(AccountLogType.UD_UNLOCK.value());
        types.add(AccountLogType.UD_CHARGE.value());
        types.add(AccountLogType.UD_CMS.value());
        types.add(AccountLogType.NODE_AWARD.value());
        types.add(AccountLogType.GLOBAL_AWARD.value());
        types.add(AccountLogType.UD_REG_SEND.value());
        params.put("types", types);
        List<DcAssetAccountLog> list = userManagerBiz.pageUDDetail(params);
        return new TableResultResponse<DcAssetAccountLog>(result.getTotal(), list);
    }

}
