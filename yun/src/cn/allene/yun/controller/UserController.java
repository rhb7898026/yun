package cn.allene.yun.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.allene.yun.pojo.User;
import cn.allene.yun.service.FileService;
import cn.allene.yun.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	/**
	 * 登录
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request, User user){
		User exsitUser = userService.findUser(user);
		if(exsitUser != null){
			HttpSession session = request.getSession();
			session.setAttribute(User.NAMESPACE, exsitUser.getUsername());
			session.setAttribute("totalSize", exsitUser.getTotalSize());
			return "redirect:/index.action";
		}else{
			request.setAttribute("msg", "用户名或密码错误");
			return "login";
		}
	}
	/**
	 * 注册
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping("/regist")
	public String regist(HttpServletRequest request, User user){
		if(user.getUsername() == null || user.getPassword() == null){
			return "regist";
		}else{
			boolean isSuccess = userService.addUser(user);
			if(isSuccess){
				fileService.addNewNameSpace(request, user.getUsername());
				return "login";
			}else{
				request.setAttribute("msg", "注册失败");
				return "regist";
			}
		}
	}
	/**
	 * 登出
	 * @param request
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request){
		request.getSession().invalidate();
		return "redirect:/user/login.action";
	}
	
	/**
	 * 登录-移动端
	 * @param req
	 * @param rep
	 * @throws Exception
	 */
	@RequestMapping("/loginForApp")
	public void getjson(HttpServletRequest req, HttpServletResponse rep)
			throws Exception {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		System.out.println("安卓端访问中..............");

		PrintWriter writer = rep.getWriter();
		JSONObject object = new JSONObject();
		User exsitUser = userService.findUser(user);
		if(exsitUser != null){
			HttpSession session = req.getSession();
			session.setAttribute(User.NAMESPACE, exsitUser.getUsername());
			session.setAttribute("totalSize", exsitUser.getTotalSize());
			//object.put("result", exsitUser);
			object.put("ret", "1000");
			object.put("msg", "登录成功");
			object.put("data", exsitUser);
		} else {
			//object.put("result", "fail");
			object.put("ret", "1001");
			object.put("msg", "登录失败");
		}
		writer.println(object.toString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 注册-移动端
	 * @param req
	 * @param rep
	 * @throws Exception
	 */
	@RequestMapping("/registForApp")
	public void registForApp(HttpServletRequest req, HttpServletResponse rep)
			throws Exception {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		System.out.println("安卓端注册中..............");
		PrintWriter writer = rep.getWriter();
		JSONObject object = new JSONObject();
		
		if(username == null || password == null){
			//object.put("result", "error");//填写有误
			object.put("ret", "1003");
			object.put("msg", "填写有误");
		}else{
			User user = new User();
			user.setUsername(username);
			user.setPassword(password);
			User isRegUser = userService.findUser(username);
			if(isRegUser == null){
				boolean isSuccess = userService.addUser(user);
				if(isSuccess){
					//根据名字创建文件目录
					fileService.addNewNameSpace(req, user.getUsername());
					user.setPassword(password);
					User exsitUser = userService.findUser(user);
					HttpSession session = req.getSession();
					session.setAttribute(User.NAMESPACE, exsitUser.getUsername());//
					session.setAttribute("totalSize", exsitUser.getTotalSize());
					//object.put("result", exsitUser);
					object.put("ret", "1000");
					object.put("msg", "注册成功");
					object.put("data", exsitUser);
				}else{
					//object.put("result", "fail");//注册失败
					object.put("ret", "1001");
					object.put("msg", "注册失败");
				}
			}else{
				//object.put("result", "isExist");//已存在该用户
				object.put("ret", "1002");
				object.put("msg", "用户已存在");
			}
		}
		writer.println(object.toString());
		writer.flush();
		writer.close();
	}
}
