package com.crell.weixin.util;

import com.alibaba.fastjson.JSONObject;
import com.crell.core.util.CommonUtil;
import com.crell.core.util.HttpClientUtil;
import com.crell.weixin.constant.Context;
import com.crell.weixin.model.Signature;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2016/3/23.
 */
@Component("weixinUtil")
public class WeixinUtil {
    @Autowired
    RedisTemplate redis;

    // 获取access_token的接口地址（GET） 限200（次/天）
    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    // 获取jsapi_ticket的接口地址（GET） 有效期 7200s=2h
    public final static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    // 获取服务器ip
    public final static String ip_list = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN";
    // 菜单创建
    public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    //获取自动回复规则
    public static String current_autoreply_url = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=ACCESS_TOKEN";
    //群发消息
    public static String message_sendall_url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";

    public String getAccessToken() throws Exception {
        BoundValueOperations<String, String> key_value = redis.boundValueOps(Context.KEY_ACCESS_TOKEN);
        String value = key_value.get();
        String accessToken = "";
        String time = "";
        if(StringUtils.isNotBlank(value)){
            time = value.split(",")[1];
            if(CommonUtil.checkDateValid(time)){
                return value.split(",")[0];
            }
        }

        String requestUrl = access_token_url.replace("APPID", Context.APPID).replace("APPSECRET", Context.APPSECRET);
        JSONObject jsonObject = HttpClientUtil.httpGet2Json(requestUrl, null);
        accessToken = jsonObject.getString("access_token");
        Date date = DateUtils.addHours(new Date(), 2);
        key_value.set(accessToken + "," + date.getTime());

        return accessToken;
    }

    public String getJsapiTicket() throws Exception {
        BoundValueOperations<String, String> key_value = redis.boundValueOps(Context.KEY_JSAPI_TICKET);
        String value = key_value.get();
        String jsapiTicket = "";
        String time = "";
        if(StringUtils.isNotBlank(value)){
            time = value.split(",")[1];
            if(CommonUtil.checkDateValid(time)){
                return value.split(",")[0];
            }
        }

        String accessToken = getAccessToken();
        String requestUrl = jsapi_ticket_url.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtil.httpGet2Json(requestUrl, null);
        jsapiTicket = jsonObject.getString("ticket");
        Date date = DateUtils.addHours(new Date(), 2);
        key_value.set(jsapiTicket + "," + date.getTime());

        return jsapiTicket;
    }

    public Signature getJssdkSign(HttpServletRequest request) throws Exception {
        //当前网页的链接
        String rootUrl = request.getScheme() + "://" + request.getServerName();
        String requestURL = rootUrl + request.getRequestURI();
        String param = request.getQueryString();
        //当前网页的URL
        String url = param==null ? requestURL : requestURL + "?" + param;
        //随机字符串
        String noncestr = UUID.randomUUID().toString();
        //有效的jsapi_ticket
        String jsapi_ticket = this.getJsapiTicket();
        //时间戳
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String sign = SignUtil.getJssdkSign(url, noncestr, jsapi_ticket, timestamp);

        Signature signature = new Signature();
        signature.setUrl(url);
        signature.setNoncestr(noncestr);
        signature.setTimestamp(timestamp);
        signature.setSignature(sign);
        signature.setAppId(Context.APPID);
        return signature;
    }

    public String getIp() throws Exception {
        String ip = "";
        String accessToken = getAccessToken();
        String requestUrl = ip_list.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtil.httpGet2Json(requestUrl, null);
        ip = jsonObject.getString("ip_list");

        return ip;
    }

    public JSONObject createMenu(String json) throws Exception {
        String accessToken = getAccessToken();
        String requestUrl = menu_create_url.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtil.httpPost2Json(requestUrl, json);

        return jsonObject;
    }

    public JSONObject getAutoreply() throws Exception {
        String accessToken = getAccessToken();
        String requestUrl = current_autoreply_url.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtil.httpPost2Json(requestUrl,"");

        return jsonObject;
    }

    public JSONObject sendall(String json) throws Exception {
        String accessToken = getAccessToken();
        String requestUrl = message_sendall_url.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtil.httpPost2Json(requestUrl, json);

        return jsonObject;
    }
}
