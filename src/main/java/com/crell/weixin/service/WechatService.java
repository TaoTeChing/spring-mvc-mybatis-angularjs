package com.crell.weixin.service;

import com.crell.core.util.DatetimeUtil;
import com.crell.weixin.entity.AutoResponse;
import com.crell.weixin.entity.ReceiveText;
import com.crell.weixin.entity.message.TextMessageResp;
import com.crell.weixin.util.MessageUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

@Service("wechatService")
public class WechatService {

	public String coreService(HttpServletRequest request) {
		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			String respContent = "请求处理异常，请稍候尝试！";
			// xml请求解析
			Map<String, String> requestMap = MessageUtil.parseXml(request);
			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			String msgId = requestMap.get("MsgId");
			//消息内容
			String content = requestMap.get("Content");
			// 默认回复此文本消息
			TextMessageResp textMessage = new TextMessageResp();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setContent(getMainMenu());
			// 将文本消息对象转换成xml字符串
			respMessage = MessageUtil.textMessageToXml(textMessage);
			//【微信触发类型】文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				respMessage = doTextResponse(content,toUserName,textMessage,respMessage,fromUserName,request,msgId,msgType);
			}
			//【微信触发类型】图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				respContent = "您发送的是图片消息！";
			}
			//【微信触发类型】地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				respContent = "您发送的是地理位置消息！";
			}
			//【微信触发类型】链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				respContent = "您发送的是链接消息！";
			}
			//【微信触发类型】音频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				respContent = "您发送的是音频消息！";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMessage;
	}

	/**
	 * 遍历关键字管理中是否存在用户输入的关键字信息
	 * 
	 * @param content
	 * @return
	 */
	private AutoResponse findKey(String content, String toUsername) {
		// 获取关键字管理的列表，匹配后返回信息
		List<AutoResponse> autoResponses = new ArrayList<AutoResponse>();
		AutoResponse ar1 = new AutoResponse();
		ar1.setKeyWord("你好,hello");
		ar1.setResContent("你好啊!");
		ar1.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
		AutoResponse ar2 = new AutoResponse();
		ar2.setKeyWord("提示,操作");
		ar2.setResContent("按照菜单操作吧!");
		ar2.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
		AutoResponse ar3 = new AutoResponse();
		ar3.setKeyWord("互动");
		String url = "http://1520399my8.imwork.net/ajax/jssdkTest";
		ar3.setResContent("<a href='" + url + "'>点击互动</a>");
		ar3.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);

		autoResponses.add(ar1);
		autoResponses.add(ar2);
		autoResponses.add(ar3);
		for (AutoResponse r : autoResponses) {
			// 如果包含关键字
			String kw = r.getKeyWord();
			String[] allkw = kw.split(",");
			for (String k : allkw) {
				if (k.equals(content)) {
					return r;
				}
			}
		}
		return null;
	}

	/**
	 * 针对文本消息
	 * @param content
	 * @param toUserName
	 * @param textMessage
	 * @param respMessage
	 * @param fromUserName
	 * @param request
	 * @throws Exception 
	 */
	String doTextResponse(String content,String toUserName,TextMessageResp textMessage,
			String respMessage,String fromUserName,HttpServletRequest request,String msgId,String msgType) throws Exception{
		//=================================================================================================================
		//Step.1 判断关键字信息中是否管理该文本内容。有的话优先采用数据库中的回复
		AutoResponse autoResponse = findKey(content, toUserName);
		// 根据系统配置的关键字信息，返回对应的消息
		if (autoResponse != null) {
			String resMsgType = autoResponse.getMsgType();
			if (MessageUtil.REQ_MESSAGE_TYPE_TEXT.equals(resMsgType)) {
				textMessage.setContent(autoResponse.getResContent());
				respMessage = MessageUtil.textMessageToXml(textMessage);
			}
		}
		return respMessage;
	}

	/**
	 * 欢迎语
	 * @return
	 */
	public static String getMainMenu() {
		String html = "欢迎关注crell";
		return html;
	}
}
