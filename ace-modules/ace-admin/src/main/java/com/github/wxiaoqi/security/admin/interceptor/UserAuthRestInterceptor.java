package com.github.wxiaoqi.security.admin.interceptor;


import com.github.wxiaoqi.security.admin.biz.base.GateLogBiz;
import com.github.wxiaoqi.security.admin.config.RequestUrlConfig;
import com.github.wxiaoqi.security.admin.configuration.UserConfiguration;
import com.github.wxiaoqi.security.admin.service.PermissionService;
import com.github.wxiaoqi.security.admin.util.DBLog;
import com.github.wxiaoqi.security.admin.util.JwtTokenUtil;
import com.github.wxiaoqi.security.admin.vo.LogInfo;
import com.github.wxiaoqi.security.admin.vo.PermissionInfo;
import com.github.wxiaoqi.security.admin.vo.UserInfo;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.GateLog;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.exception.auth.UserTokenException;
import com.github.wxiaoqi.security.common.mapper.admin.BaseMenuTitleMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.jwt.JWTAdminInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.TOKEN_ADMIN;

@Slf4j
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserConfiguration userConfiguration;

    @Autowired
    private GateLogBiz gateLogBiz;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RequestUrlConfig requestUrlConfig;

    @Autowired
    private BaseMenuTitleMapper menuTitleMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader(userConfiguration.getUserTokenHeader());
        JWTAdminInfo  infoFromToken = jwtTokenUtil.getInfoFromToken(token);
        UserInfo info = (UserInfo) CacheUtil.getCache().get(Constants.CacheServiceType.ADMIN_USER + token);
        //一个用户可能会产生两条token,所以登陆的时候保存了他们双向的关系.
        if (infoFromToken == null || info == null) {
            throw new UserTokenException(Resources.getMessage("BASE_LOGIN_INVALID"));
        }
        String real_token = (String) CacheUtil.getCache().get(Constants.CommonType.TOKEN + info.getId());
        if (StringUtils.isBlank(real_token) || !real_token.equals(token)) {
            CacheUtil.getCache().del(token);
            throw new UserTokenException(Resources.getMessage("BASE_LOGIN_INVALID"));
        }
        BaseContextHandler.setUsername(infoFromToken.getUsername());
        BaseContextHandler.setName(infoFromToken.getName());
        BaseContextHandler.setUserID(infoFromToken.getId());
        BaseContextHandler.setExId(infoFromToken.getExchangeId());
        BaseContextHandler.setToken(token);
        Param token_param = (Param) CacheUtil.getCache().get(TOKEN_ADMIN);
        int expire = 600;//600秒
        if (token_param != null) {
            expire = Integer.parseInt(token_param.getParamValue());
        }
        CacheUtil.getCache().expire(Constants.CacheServiceType.ADMIN_USER + token, expire);
        // 判断资源是否启用权限约束
        String requestUri = request.getRequestURI();//请求地址
        if (requestUri.contains("//")) {
            requestUri = requestUri.replace("//", "/");
        }
        final String method = request.getMethod().toString();//请求http method
        Stream<PermissionInfo> stream = getPermissionIfs(requestUri, method, permissionService.getAllPermission());
        List<PermissionInfo> result = stream.collect(Collectors.toList());
        PermissionInfo[] permissions = result.toArray(new PermissionInfo[]{});
        if (permissions.length > 0 && !isRequestUrl(requestUri)) {
            if (checkUserPermission(permissions, request, infoFromToken)) {
                throw new UserInvalidException(Resources.getMessage("BASE_NO_PERMISSION"));
            }
        }
        return super.preHandle(request, response, handler);
    }


    private boolean checkUserPermission(PermissionInfo[] permissions, HttpServletRequest ctx, JWTAdminInfo user) {
        String language = ctx.getHeader("locale");
        List<PermissionInfo> permissionInfos = permissionService.getPermissionByUsername(user.getUsername(), language);
        PermissionInfo current = null;
        for (PermissionInfo info : permissions) {
            boolean anyMatch = permissionInfos.parallelStream().anyMatch(new Predicate<PermissionInfo>() {
                @Override
                public boolean test(PermissionInfo permissionInfo) {
                    return permissionInfo.getCode().equals(info.getCode());
                }
            });
            if (anyMatch) {
                current = info;
                break;
            }
        }
        if (current == null) {
            return true;
        } else {
            if (!RequestMethod.GET.toString().equals(current.getMethod())) {
                setCurrentUserInfoAndLog(ctx, user, current, language);
            }
            return false;
        }
    }

    private void setCurrentUserInfoAndLog(HttpServletRequest request, JWTAdminInfo user, PermissionInfo pm, String language) {
        String menu = "";
        if (language.equals("zh")) {
            menu = pm.getMenu();
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", pm.getMenu());
            map.put("language", language);
            menu = menuTitleMapper.selectTitleByMenuCode(map);
        }
        LogInfo logInfo = new LogInfo(menu, pm.getName(), pm.getUri(), new Date(), user.getId(), user.getName(), WebUtil.getHost(request), user.getExchangeId());
        GateLog log = new GateLog();
        BeanUtils.copyProperties(logInfo, log);
        DBLog.getInstance().setLogService(gateLogBiz).offerQueue(log);
    }

    /**
     * 获取目标权限资源
     *
     * @param requestUri
     * @param method
     * @param serviceInfo
     * @return
     */
    private Stream<PermissionInfo> getPermissionIfs(final String requestUri, final String method, List<PermissionInfo> serviceInfo) {
        return serviceInfo.parallelStream().filter(new Predicate<PermissionInfo>() {
            @Override
            public boolean test(PermissionInfo permissionInfo) {
                String uri = permissionInfo.getUri();
                if (uri.indexOf("{") > 0) {
                    uri = uri.replaceAll("\\{\\*\\}", "[a-zA-Z\\\\d]+");
                }
                String regEx = "^" + uri + "$";
                return (Pattern.compile(regEx).matcher(requestUri).find())
                        && method.equals(permissionInfo.getMethod());
            }
        });
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }


    //请求URL 是否需要验证
    private boolean isRequestUrl(String url) {
        String[] strs = requestUrlConfig.getRequestUrl().split(",");
        for (String str : strs) {
            if (str.indexOf(url) > -1) {
                return true;
            };
        }
        return false;
    }
}
