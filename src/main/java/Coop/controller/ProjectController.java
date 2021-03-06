package Coop.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Coop.mapper.ActivePointMapper;
import Coop.mapper.FileMapper;
import Coop.mapper.ICommentMapper;
import Coop.mapper.InviteMapper;
import Coop.mapper.IssueMapper;
import Coop.mapper.NoticeMapper;
import Coop.mapper.NoticeUserMapper;
import Coop.mapper.ProUserMapper;
import Coop.mapper.ProjectMapper;
import Coop.mapper.UserMapper;
import Coop.model.Active;
import Coop.model.ActivePoint;
import Coop.model.ChartData;
import Coop.model.IComment;
import Coop.model.Invite;
import Coop.model.Issue;
import Coop.model.Notice;
import Coop.model.NoticeUser;
import Coop.model.Pro_User;
import Coop.model.Project;
import Coop.model.User;
import Coop.model.UserKey;
import Coop.service.IOSPushService;
import Coop.service.MobileAuthenticationService;
import Coop.service.UserService;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;

@Controller
@RequestMapping("/project")
public class ProjectController {
	
	@Autowired ProjectMapper projectMapper;
	@Autowired UserService userService;
	@Autowired FileMapper fileMapper;
	@Autowired ProUserMapper proUserMapper;
	@Autowired UserMapper userMapper;
	@Autowired InviteMapper inviteMapper;
	@Autowired NoticeMapper noticeMapper;
	@Autowired IssueMapper issueMapper;
	@Autowired ICommentMapper iCommentMapper;
	@Autowired MobileAuthenticationService mobileAuthenticationService;
	@Autowired ActivePointMapper activePointMapper;
	@Autowired NoticeUserMapper noticeUserMapper;
	@Autowired IOSPushService iosPushService;
	
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return format.format(new Date());
    }
    
    
    
	
	@RequestMapping(value = "/{id}/create.do",method = RequestMethod.GET)
	 public String create(@PathVariable String id,Model model) {
		
		model.addAttribute("id",id);
        return "layout/project/create";
    }
	@RequestMapping(value = "/{id}/create.do",method = RequestMethod.POST)
	 public String regist(@PathVariable String id,Project project,Model model) {
		project.setCreate_time(getCurrentDate());
		projectMapper.insertProject(project);
		Pro_User pro_user = new Pro_User();
		pro_user.setProId(project.getId());
		pro_user.setUserId(id);
		proUserMapper.insertPro_user(pro_user);
		
	   model.addAttribute("ProjectList",projectMapper.selectById(userService.getCurrentUser()));
       return "layout/main/home";
	}
	@RequestMapping(value = "/{projectId}/edit.do",method = RequestMethod.POST)
	 public String edit(@PathVariable String projectId,ActivePoint activePoint,Model model) {
		Project project = projectMapper.selectByProjectId(Integer.parseInt(projectId));
		activePoint.setProjectId(project.getId());
		
		activePointMapper.insert(activePoint);
		NoticeUser noticeUser=  new NoticeUser();
		noticeUser.setProjectId(project.getId());
		noticeUser.setMember(userService.getCurrentUser().getId());
		model.addAttribute("noticeList",noticeMapper.select(noticeUser));
		model.addAttribute("project",projectMapper.selectByProjectId(project.getId()));
		model.addAttribute("fileList", fileMapper.selectByProjectId(project.getId()));
		model.addAttribute("userList",userMapper.selectProject(project.getId()));
		model.addAttribute("pro_user",proUserMapper.selectByProjectId(project.getId()));
		model.addAttribute("issueList", issueMapper.selectByProjectId(project.getId()));
		return "layout/project/info";
		
	}
	@RequestMapping(value = "/{id}/proInfo.do",method = RequestMethod.GET)
	 public String goInfo(@PathVariable String id,Model model) {
			Project project = projectMapper.selectByProjectId(Integer.parseInt(id));
			Pro_User pro = new Pro_User();
			NoticeUser noticeUser=  new NoticeUser();
			pro.setCont(1);
			pro.setProId(project.getId());
			pro.setUserId(userService.getCurrentUser().getId());
			proUserMapper.updateCont(pro);
			noticeUser.setProjectId(project.getId());
			noticeUser.setMember(userService.getCurrentUser().getId());
			List<User> userList = userMapper.selectProject(project.getId());
			List<User> pro_user = proUserMapper.selectByProjectId(project.getId());
			model.addAttribute("noticeList",noticeMapper.select(noticeUser));
			model.addAttribute("project",project);
			model.addAttribute("fileList", fileMapper.selectByProjectId(project.getId()));
			model.addAttribute("userList",userList);
			model.addAttribute("pro_user",pro_user);
			model.addAttribute("issueList", issueMapper.selectByProjectId(project.getId()));
			
			return "layout/project/info";
	}
	@RequestMapping(value="/search.do",method = RequestMethod.POST)
	public String search(@RequestParam("search") String search,Model model){
		model.addAttribute("ProjectList",projectMapper.selectBySearch(search));
		model.addAttribute("user",userService.getCurrentUser());
		Active act = new Active();
		act.setAct("active");
		model.addAttribute("act",act);
		return "layout/main/home";
	}
	@RequestMapping(value="/{userId}/{projectId}/invite.do",method = RequestMethod.GET)
	public String invite(@PathVariable String userId,@PathVariable String projectId,Model model){
		System.out.println("inv");
		Invite invite = new Invite();
		invite.setProjectId(Integer.parseInt(projectId));
		invite.setSender(userService.getCurrentUser().getId());
		invite.setRecipient(userId);
		inviteMapper.insert(invite);
		model.addAttribute("pro_user",proUserMapper.selectByProjectId(Integer.parseInt(projectId)));
		return "redirect:" + "/project/"+projectId+"/proInfo.do";
	}
	@RequestMapping(value="/{projectId}/{issueId}/issueInfo.do",method = RequestMethod.GET)
	public String issueInfo(@PathVariable String projectId,@PathVariable String issueId,Model model){
		
		model.addAttribute("issue",issueMapper.selectById(Integer.parseInt(issueId)));
		model.addAttribute("project", projectMapper.selectByProjectId(Integer.parseInt(projectId)));
		model.addAttribute("commentList",iCommentMapper.selectByIssueId(Integer.parseInt(issueId)));
		return "layout/issue/info";
	}
	@RequestMapping(value="/{projectId}/{issueId}/issueInfo.do",method = RequestMethod.POST)
	public String issueInfo(@PathVariable String projectId,@PathVariable String issueId,
			IComment icomment,Model model) throws CommunicationException, KeystoreException, InvalidDeviceTokenFormatException{
		
		icomment.setIssueId(Integer.parseInt(issueId));
		icomment.setProjectId(Integer.parseInt(projectId));
		icomment.setUserId(userService.getCurrentUser().getId());
		icomment.setUserName(userService.getCurrentUser().getName());
		
		iCommentMapper.insert(icomment);
		
		model.addAttribute("issue",issueMapper.selectById(Integer.parseInt(issueId)));
		model.addAttribute("project", projectMapper.selectByProjectId(Integer.parseInt(projectId)));
		model.addAttribute("commentList",iCommentMapper.selectByIssueId(Integer.parseInt(issueId)));
		Issue iss = issueMapper.selectById(Integer.parseInt(issueId));
		List<User> proUser = proUserMapper.selectByProjectId(Integer.parseInt(projectId));
		
		for(int i=0;i<proUser.size();i++){
			
			if(!userService.getCurrentUser().getId().equals(proUser.get(i).getId())){
				
			
				UserKey userKey = userMapper.selectByKey(proUser.get(i).getId());
				if(userKey!=null){
					iosPushService.push(userKey.getUserkey(),userService.getCurrentUser().getName()+" 님이 "+iss.getName()+" 이슈에 댓글을 남겼습니다");
				}
				
			}
		}
		return "layout/issue/info";
	}
	@RequestMapping(value="/{projectId}/issue.do",method = RequestMethod.GET)
	public String issue(@PathVariable String projectId,Model model){
		
		return "layout/project/issue";
	}
	@RequestMapping(value="/{projectId}/issue.do",method = RequestMethod.POST)
	public String issueMake(@PathVariable String projectId,Issue issue,Model model) throws CommunicationException, KeystoreException, InvalidDeviceTokenFormatException{
		issue.setProjectId(Integer.parseInt(projectId));
		issue.setUserId(userService.getCurrentUser().getId());
		issue.setUserName(userService.getCurrentUser().getName());
		
		issueMapper.insert(issue);
		Project project = projectMapper.selectByProjectId(Integer.parseInt(projectId));
		
		Notice notice = new Notice();
		notice.setProjectId(Integer.parseInt(projectId));
		notice.setUserId(userService.getCurrentUser().getId());
		String des = userService.getCurrentUser().getName()+"님이 "+project.getName()+" 에 이슈를 생성했습니다";
		notice.setDes(des);
		noticeMapper.insert(notice);
		NoticeUser noticeUser = new NoticeUser();
		noticeUser.setId(notice.getId());
		noticeUser.setProjectId(Integer.parseInt(projectId));
		
		
		
		List<User> proUser = proUserMapper.selectByProjectId(Integer.parseInt(projectId));
		
		for(int i=0;i<proUser.size();i++){
			
			if(!userService.getCurrentUser().getId().equals(proUser.get(i).getId())){
				
				noticeUser.setMember(proUser.get(i).getId());
				noticeUserMapper.insert(noticeUser);
				UserKey userKey = userMapper.selectByKey(proUser.get(i).getId());
				if(userKey!=null){
					iosPushService.push(userKey.getUserkey(),userService.getCurrentUser().getName()+" 님이 "+project.getName()+" 에 이슈를 남겼습니다");
				}
				
			}
			
      
       
	  }
		
		model.addAttribute("noticeList",noticeMapper.select(noticeUser));
		model.addAttribute("project",projectMapper.selectByProjectId(Integer.parseInt(projectId)));
		model.addAttribute("fileList", fileMapper.selectByProjectId(Integer.parseInt(projectId)));
		model.addAttribute("userList",userMapper.selectProject(Integer.parseInt(projectId)));
		model.addAttribute("pro_user",proUserMapper.selectByProjectId(Integer.parseInt(projectId)));
		model.addAttribute("issueList", issueMapper.selectByProjectId(Integer.parseInt(projectId)));
		
		return "layout/project/info";
	}
	
	
	

	/*모바일 url*/
	@ResponseBody
	@RequestMapping(value = "/{id}/proList.do",method = RequestMethod.GET)
	 public List<Project> ListDo(@PathVariable String id,HttpServletResponse response) {
			response.addHeader("Access-Control-Allow-Origin", "*");
			if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
				return projectMapper.selectById2(id);
			}
			else{
				return null;
			}
			
	        
			
	}
	@ResponseBody
	@RequestMapping(value = "/create.do",method = RequestMethod.POST)
	 public String creatDo(@RequestParam String name,@RequestParam String owner,
			 @RequestParam String des,HttpServletResponse response) {
			response.addHeader("Access-Control-Allow-Origin", "*");
			if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
				Project project = new Project();
				project.setDes(des);
				project.setName(name);
				project.setOwner(owner);
				
				projectMapper.insertProject(project);
				Pro_User user = new Pro_User();
				user.setCont(0);
				user.setProId(project.getId());
				user.setUserId(userService.getCurrentUser().getId());
				proUserMapper.insertPro_user(user);
				return "success";
			}
			else{
				return "fail";
			}
			
	        
			
	}
	@ResponseBody
	@RequestMapping(value="/issueMakeMobile.do",method = RequestMethod.POST)
	public String issueMakeMobile(@RequestParam String projectId,
			@RequestParam String label,@RequestParam String name,@RequestParam String des,Model model,HttpServletResponse response) throws CommunicationException, KeystoreException, InvalidDeviceTokenFormatException{
		response.addHeader("Access-Control-Allow-Origin", "*");
		if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
			Issue issue = new Issue();
			issue.setProjectId(Integer.parseInt(projectId));
			issue.setUserId(userService.getCurrentUser().getId());
			issue.setUserName(userService.getCurrentUser().getName());
			issue.setDes(des);
			issue.setLabel(label);
			issue.setName(name);
			issueMapper.insert(issue);
			Project project = projectMapper.selectByProjectId(Integer.parseInt(projectId));
			Notice notice = new Notice();
			notice.setProjectId(Integer.parseInt(projectId));
			notice.setUserId(userService.getCurrentUser().getId());
			String des2 = userService.getCurrentUser().getName()+"님이 "+project.getName()+" 에 이슈를 생성했습니다";
			notice.setDes(des2);
			noticeMapper.insert(notice);
			NoticeUser noticeUser = new NoticeUser();
			noticeUser.setId(notice.getId());
			noticeUser.setProjectId(Integer.parseInt(projectId));
			
			
			
			List<User> proUser = proUserMapper.selectByProjectId(Integer.parseInt(projectId));
			
			for(int i=0;i<proUser.size();i++){
				
				if(!userService.getCurrentUser().getId().equals(proUser.get(i).getId())){
					
					noticeUser.setMember(proUser.get(i).getId());
					noticeUserMapper.insert(noticeUser);
					UserKey userKey = userMapper.selectByKey(proUser.get(i).getId());
					if(userKey!=null){
						iosPushService.push(userKey.getUserkey(),userService.getCurrentUser().getName()+" 님이 "+project.getName()+" 에 이슈를 남겼습니다");
					}
					
				}
				
	      
	       
		  }
			return "success";
		}
		else{
			return "fail";
		}
		
		
		
		
	}
	@ResponseBody
	@RequestMapping(value = "/memberList.do",method = RequestMethod.GET)
	 public List<User> member(@RequestParam String id,HttpServletResponse response) {
			response.addHeader("Access-Control-Allow-Origin", "*");
			if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
				return proUserMapper.selectByProjectId(Integer.parseInt(id));
			}
			else{
				return null;
			}
		
			
	}
	@ResponseBody
	@RequestMapping(value="/chart.do", method=RequestMethod.GET)
    public List<ChartData> chart(@RequestParam String id,HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
			return proUserMapper.selectCont(Integer.parseInt(id));
		}
		else{
			return null;
		}
		
        
    }
	@ResponseBody
	@RequestMapping(value = "/proList.do",method = RequestMethod.POST)
	 public List<Project> ListProject(@RequestParam String id,HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
			return projectMapper.selectById2(id);
		}
		else{
			return null;
		}
		
		
			
	}
	@ResponseBody
	@RequestMapping(value = "/issueList.do",method = RequestMethod.GET)
	 public List<Issue> issueList(@RequestParam String id,HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
			return issueMapper.selectByProjectId(Integer.parseInt(id));
		}
		else{
			return null;
		}
		
		
			
	}
	@ResponseBody
	@RequestMapping(value="/issueMobileInfo.do",method = RequestMethod.GET)
	public Issue issueMobileInfo(@RequestParam String issueId,Model model){
		
		Issue issue = issueMapper.selectById(Integer.parseInt(issueId));
		if(issue.getUserId().equals(userService.getCurrentUser().getId())){
			return issue;
			
		}
		else{
			return null;
		}
	}
	@ResponseBody
	@RequestMapping(value = "/request.do",method = RequestMethod.GET)
	 public List<Notice> request(@RequestParam String id,HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
			NoticeUser noticeUser=  new NoticeUser();
			noticeUser.setProjectId(Integer.parseInt(id));
			noticeUser.setMember(userService.getCurrentUser().getId());
			return noticeMapper.select(noticeUser);
		}
		else{
			return null;
		}
		
			
	}
	 @RequestMapping(value = "/mobileEdit.do",method = RequestMethod.POST)
	 public String editMobile(@RequestParam String id,@RequestParam String fileUpload,@RequestParam String issueMake ,
			 Model model,@RequestParam String etc) {
		
			if(mobileAuthenticationService.AuthenticationUser(userService.getCurrentUser())){
				Project project = projectMapper.selectByProjectId(Integer.parseInt(id));
				if(project.getOwner().equals(userService.getCurrentUser().getId())){
					ActivePoint activePoint = new ActivePoint();
					activePoint.setEtc(Integer.parseInt(etc));
					activePoint.setFileUpload(Integer.parseInt(fileUpload));
					activePoint.setIssueMake(Integer.parseInt(issueMake));
					activePoint.setProjectId(project.getId());
				}
				
				return "success";
			}
			else{
				return "No Access";
			}
		
		
	}
}
