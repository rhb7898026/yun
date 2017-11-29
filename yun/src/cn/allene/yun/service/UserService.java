package cn.allene.yun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.allene.yun.dao.UserDao;
import cn.allene.yun.pojo.User;
import cn.allene.yun.utils.UserUtils;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;

	public User findUser(User user) {
		try {
			user.setPassword(UserUtils.MD5(user.getPassword()));
			User exsitUser = userDao.findUser(user);
			return exsitUser;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean addUser(User user){
		try {
			user.setPassword(UserUtils.MD5(user.getPassword()));
			userDao.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public User findUser(String username) {
		User user = null;
		try {
			user = userDao.findUserByUserName(username);
		} catch (Exception e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	
	public String getCountSize(String username){
		String countSize = null;
		try {
			countSize = userDao.getCountSize(username);
		} catch (Exception e) {
			e.printStackTrace();
			return countSize;
		}
		return countSize;
	}
}
