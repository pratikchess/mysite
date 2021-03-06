package com.prapps.app.chat.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prapps.app.chat.dto.Message;
import com.prapps.app.chat.dto.MessageResponse;
import com.prapps.app.chat.dto.MessageThread;
import com.prapps.app.core.dto.User;
import com.prapps.app.core.util.time.TimeUtil;

@Service
public class ChatService {

	@Autowired TimeUtil timeUtil;
	private Map<Long, Map<Long, MessageThread>> messageThreadMap = new HashMap<Long, Map<Long, MessageThread>>();
	int threadCount = 0;
	
	public int addHttpMessage(Long threadId, String msg, User user) {
		Map<Long, MessageThread> threads = messageThreadMap.get(user.getUserId());
		if (threads == null) {
			threads = new HashMap<Long, MessageThread>();
			messageThreadMap.put(user.getUserId(), threads);
		}
		
		if (threadId == null || !threads.containsKey(threadId)) {
			threadId = (long) (++threadCount);
			threads.put(threadId, new MessageThread());
		}
		
		MessageThread thread = threads.get(threadId);
		Message message = new Message(thread.getCurrentIndex() + 1, msg);
		message.setUserName(user.getUserName());
		thread.addMessage(message);
		message.setTime(timeUtil.getCurrentTime());
		return thread.getCurrentIndex();
	}
	
	public MessageResponse getMessages(Long threadId, int lastIndex, User user) {
		Map<Long, MessageThread> threads = messageThreadMap.get(user.getUserId());
		if (threads == null || !threads.containsKey(threadId)) {
			return new MessageResponse();
		}
		
		MessageThread thread = threads.get(threadId); 
		int currentIndex = thread.getCurrentIndex();
		return new MessageResponse(currentIndex, thread.getMessages().subList(lastIndex + 1, currentIndex + 1));
	}
}
