package itims.web.syslog.event.util;

import itims.model.utils.ModelCommonParams;
import itims.share.corebuf.CoreBufService;
import itims.share.corebuf.model.IMONode;
import itims.share.db.JdbcAbstractTemplate;
import itims.share.right.bean.Domain;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Util {
	
	private static Util instance = null;
	
	private static Object syncObj = new Object(); //使用了一个私有静态变量syncObj来保证线程同步
	
	private String uploadPath = "";//声音文件保存路径
	
	private String soundPath = "";//声音文件保存了两份，为了方便试听其中一份保存在tomcat下，
	
	private String listenPath = "/";//试听相对路径
	
	private Map<String,String> levelmap = new HashMap<String,String>();
	
	private Map<String,String> soundmap = new HashMap<String,String>();
	
	private Map<String ,String> reallogmap = new HashMap<String,String>(); 
	
	private  long latestRealLogTime = 0; //记录实时日志界面最新查看过的日志时间
	
	private long latestUDPRealLogTime = 0 ;//记录UDP实时日志界面最新查看过的日志时间  laill
	
	private long maxLevelId = 0;//数据库级别表tmSyslogLevel中最大的id
	
	private long maxGroupId = 0;//数据库组表tmSyslogShowGroup中最大的id
	
	private String groupOrder = "";//数据库组表tmSyslogShowGroup中组的排序
	
	private long maxComprsId = 0;//数据库组表tmSyslogShowGroup中最大的id
	
	private boolean flag = false;//声音文件是否从云核中拷贝到tomcat工程中，每次启动的时候拷贝一次。
	
	private Timer timer = null;//定时将查询的结果缓存清空
	
	private long interval = 5000;//
	
	public synchronized static Util getInstance() {
		 if(instance != null) 
			return instance;
		 synchronized (syncObj){ 
			if(instance != null) 
				return instance; 
			instance = new Util();
			} 
		 
		return instance;	
	}
	
	private Util(){
		uploadPath = ModelCommonParams.getWorkRoot(Domain.getCurDomain());
		if(!uploadPath.endsWith(File.separator))
    		uploadPath += File.separator;
		uploadPath += "syslog"+File.separator;
		listenPath = File.separator + "sound"+ File.separator + Domain.getCurDomain() +File.separator;
		initSyslogLevel();
		initSyslogSound();
		timer = new Timer();
		timer.schedule(new updateRealLog(), interval, interval);
	}
	
	/**
	 * 定时将缓存清空
	 */
	class updateRealLog extends TimerTask{
		@Override
		public void run(){
			try{
				reallogmap.clear();
			}catch(Exception e){
				System.out.println("updateRealLog is error: " + e);
			}finally{

			}
		}
	}
	
    //初始化级别key-fDesc vale-fLevel
	public void initSyslogLevel(){
		JdbcAbstractTemplate jat = null;
		List<Map> l = null;
		try{
			jat = new JdbcAbstractTemplate();
			String sql = "select fLevel,fDesc,fMsg,fLevelId from tmSyslogLevel order by fLevelId";
			l = jat.getListForMap(sql);
			 if(l.size()>0){			
				 for(int i=0;i<l.size();i++){
					 if(Integer.parseInt(l.get(i).get("flevelid").toString())<=9)
						 levelmap.put(l.get(i).get("flevel").toString(), l.get(i).get("fmsg").toString()+l.get(i).get("fdesc").toString());
				 }
				 maxLevelId = Integer.parseInt(l.get(l.size()-1).get("flevelid").toString());
			 }
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			l = null;
		}
	}
	//初始化声音信息
	public void initSyslogSound(){
		JdbcAbstractTemplate jat = null;
		List<Map> l = null;
		try{
			jat = new JdbcAbstractTemplate();
			String sql = "select * from tmSyslogSound order by fGroupId";
			l = jat.getListForMap(sql);
			 if(l.size()>0){			
				 for(int i=0;i<l.size();i++){
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel1", l.get(i).get("flevel1")==null?"":l.get(i).get("flevel1").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel2", l.get(i).get("flevel2")==null?"":l.get(i).get("flevel2").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel3", l.get(i).get("flevel3")==null?"":l.get(i).get("flevel3").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel4", l.get(i).get("flevel4")==null?"":l.get(i).get("flevel4").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel5", l.get(i).get("flevel5")==null?"":l.get(i).get("flevel5").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel6", l.get(i).get("flevel6")==null?"":l.get(i).get("flevel6").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel7", l.get(i).get("flevel7")==null?"":l.get(i).get("flevel7").toString());
					 soundmap.put(l.get(i).get("fgroupid").toString()+"_flevel8", l.get(i).get("flevel8")==null?"":l.get(i).get("flevel8").toString());
				 }
				 maxGroupId = Integer.parseInt(l.get(l.size()-1).get("fgroupid").toString());
			 }
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			l = null;
		}
	}
	
	
	public long getTimeByString(String timeStr){
		long longTime = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			longTime = df.parse(timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return longTime;
	}
	
	public String  RandomNum(int num){ 
		String rnd=""; 
		Random r = new Random();
		for(int i=0;i<3;i++) 
			rnd += r.nextInt(10); 
		return rnd; 
	} 
	
	public Map<String, String> getLevelmap() {
		return levelmap;
	}
	public Map<String, String> getSoundmap() {
		return soundmap;
	}
	public String getReallogmap(String groupid) {
		
		return reallogmap.get(groupid);
	}

	public void setReallogmap(String groupid, String result) {
		reallogmap.put(groupid, result);
	}

	public String getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(String groupOrder) {
		this.groupOrder = groupOrder;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public String getSoundPath() {
		if(soundPath==null||soundPath.length()==0)
			setSoundPath();
		return soundPath;
	}

	private void setSoundPath() {
		//String tomcatPath = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").lastIndexOf(File.separator));
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		if(System.getProperty("os.name").toLowerCase().indexOf("window")!=-1)
			path = path.substring(1,path.indexOf("WEB-INF"));//tomcatPath + File.separator + "webapps" + File.separator + ContextPath+D:\tomcat6\webapps\itimsnull\WEB-INF\classes
		else
			path = path.substring(0,path.indexOf("WEB-INF"));
		if(path.endsWith("\\")||path.endsWith("/"))
			soundPath = path + "syslog" + File.separator + "event"+ File.separator + "sound"+ File.separator + Domain.getCurDomain() +File.separator;
		else
			soundPath =  path + File.separator + "syslog" + File.separator + "event"+ File.separator + "sound"+ File.separator + Domain.getCurDomain() +File.separator;
	}

	
	public String getIpByMOSN(String mosn){
		IMONode node = CoreBufService.getInstance().getMO(Integer.parseInt(mosn));
		if (node!=null) {
			return node.getIp();
		}
		return "";
	}
	public String getListenPath() {
		return listenPath;
	}

	public  void setListenPath(String listenPath) {
		this.listenPath = listenPath;
	}

	public  long getLatestRealLogTime() {
		return latestRealLogTime;
	}

	public  void setLatestRealLogTime(long latestRealLogTime) {
		this.latestRealLogTime = latestRealLogTime;
	}
	
	public long getLatestUDPRealLogTime() {
		return latestUDPRealLogTime;
	}

	public void setLatestUDPRealLogTime(long latestUDPRealLogTime) {
		this.latestUDPRealLogTime = latestUDPRealLogTime;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public long getMaxLevelId() {
		return maxLevelId;
	}

	public void setMaxLevelId() {
		maxLevelId ++;
	}

	public long getMaxGroupId() {
		return maxGroupId;
	}

	public void setMaxGroupId() {
		maxGroupId ++;
	}

	public  long getMaxComprsId() {
		return maxComprsId;
	}

	public  void setMaxComprsId(long maxComprsId) {
		this.maxComprsId = maxComprsId;
	}




}
