package com.github.wxiaoqi.security.admin.rest.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.TransferExchBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.admin.TransferExch;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.vo.SymbolTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transferExch")
public class TransferExchController extends BaseController<TransferExchBiz, TransferExch> {

    @Autowired
    private TransferExchBiz transferExchBiz;


    @RequestMapping(value = "/symbolTransfer", method = RequestMethod.GET)
    public TableResultResponse<SymbolTransfer> getSymbolTransfer(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<SymbolTransfer> charge = transferExchBiz.selectTransfer(params);
        return new TableResultResponse<SymbolTransfer>(result.getTotal(), charge);
    }

    @RequestMapping(value = "/{id}/transfer", method = RequestMethod.GET)
    public ObjectRestResponse<List<TransferExch>> getSymbolExch(@PathVariable Long id) {
        return new ObjectRestResponse().data(transferExchBiz.getTransferExchBiz(id)).rel(true);
    }

    @RequestMapping(value = "/{id}/transfer/add", method = RequestMethod.POST)
    public ObjectRestResponse addTransferExch(@PathVariable Long id, Long exchId) {
        transferExchBiz.modifyTransferExch(id, exchId);
        return new ObjectRestResponse().rel(true);
    }


    @RequestMapping(value = "/{id}/transfer/remove", method = RequestMethod.POST)
    public ObjectRestResponse removeElementAuthority(@PathVariable Long id, Long exchId) {
        transferExchBiz.removeTransferExch(id, exchId);
        return new ObjectRestResponse().rel(true);
    }


    @RequestMapping(value = "/transfer/{id}", method = RequestMethod.GET)
    public ObjectRestResponse<TransferExch> getTransferExch(@PathVariable Long id, Long exchId) {
        return  new ObjectRestResponse<TransferExch>().data(transferExchBiz.selectTransferByExchId(id, exchId));
    }


    @RequestMapping(value = "/{id}/status",method = RequestMethod.PUT)
    public ObjectRestResponse<TransferExch> updateStatus(@PathVariable Long id,@RequestParam Long exchId,@RequestParam Integer isOpen){
        TransferExch exch = new TransferExch();
        exch.setExchId(exchId);
        exch.setId(id);
        TransferExch transferExch = transferExchBiz.selectOne(exch);
        if (transferExch ==null){
            throw new UserInvalidException(Resources.getMessage("CONFIG_TRANSFER_OPEN"));
        }
        transferExch.setIsOpen(isOpen);
        transferExchBiz.updateById(transferExch);
        return new ObjectRestResponse<TransferExch>();
    }

}
