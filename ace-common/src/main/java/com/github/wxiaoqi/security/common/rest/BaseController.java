package com.github.wxiaoqi.security.common.rest;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.Query;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-15 8:48
 */
@Slf4j
public class BaseController<Biz extends BaseBiz,Entity> {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected Biz baseBiz;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<Entity> add(@RequestBody Entity entity){
        baseBiz.insertSelective(entity);
        return new ObjectRestResponse<Entity>();
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<Entity> get(@PathVariable Long id){
        ObjectRestResponse<Entity> entityObjectRestResponse = new ObjectRestResponse<>();
        Object o = baseBiz.selectById(id);
        entityObjectRestResponse.data((Entity)o);
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<Entity> update(@RequestBody Entity entity){
        baseBiz.updateSelectiveById(entity);
        return new ObjectRestResponse<Entity>();
    }
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse<Entity> remove(@PathVariable Long id){
        baseBiz.deleteById(id);
        return new ObjectRestResponse<Entity>();
    }

    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @ResponseBody
    public List<Entity> all(){
        return baseBiz.selectListAll();
    }


    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Entity> list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        return baseBiz.selectByQuery(query);
    }


    /**
     * 分页查询
     * @param query
     * @return
     *
     * example
     *
    *         Map<String, Object> queryParams = new HashMap<String, Object>();
     *         //参数后面跟着:like 或者equal 默认equal(可以不填)
    *         queryParams.put("userId:like", user.getId()); // 用户Id
    *         queryParams.put("page", model.getPage()); // 当前页
    *         queryParams.put("limit", model.getLimit()); // 每页大小
    *         Query query = new Query(queryParams);
     */
    public TableResultResponse<Entity> pageQuery(Query query) {
        Class<Entity> clazz = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example example = new Example(clazz);
        //设置排序情况
        example.setOrderByClause(query.getOrderByInfo());
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                String[] key = entry.getKey().split(":");
                if(key.length == 2  && "like".equals(key[1])){
                    if(entry.getValue()!=null)
                    criteria.andLike(key[0], "%" + entry.getValue().toString() + "%");
                }else{
                    criteria.andEqualTo(key[0], entry.getValue()!=null?entry.getValue().toString():null);
                }
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Entity> list = baseBiz.selectByExample(example);
        return new TableResultResponse<Entity>(result.getTotal(), list);
    }

    /*自定义sql分页查询列表*/
    public TableResultResponse<Entity>  queryListByCustomPage(Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Entity> list =  baseBiz.queryListByCustomPage(params);
        return new TableResultResponse<Entity>(result.getTotal(), list);
    }

    /*自定义sql查询列表,无分页*/
    public List<Entity> queryListNoPage(Map<String, Object> params) {
        return  baseBiz.queryListByCustomPage(params);
    }



    public Long getCurrentUserId(){
        return BaseContextHandler.getUserID();
    }
    public String getCurrentUserName(){
        return BaseContextHandler.getUsername();
    }
    public String getLanguage(){ return request.getHeader("locale"); }

}
