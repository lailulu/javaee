package itims.web.syslog.event.dwr;

import itims.cloud.core.bean.RemoteObject;
import itims.cloud.core.constant.ServiceType;
import itims.cloud.core.rpc.client.Client;
import itims.cloud.core.rpc.client.ICRemote;
import itims.share.corebuf.CoreBufService;
import itims.share.corebuf.model.IMONode;
import itims.share.corebuf.model.IMOType;
import itims.share.db.JdbcAbstractTemplate;
import itims.share.right.bean.Domain;
import itims.web.syslog.event.util.FileOperate;
import itims.web.syslog.event.util.Util;
import itims.web.typ.bu.ApIUDisplay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.missian.client.async.AsyncFuture;

public class BasicInfoDwr {

//	private static final Log LOG = LogFactory.getLog(BasicInfoDwr.class);

	Util u = Util.getInstance();

	//查询声音信息（注：为上海中电投特定需求，库表数据定死一条fGroupId=2015）laill
	public String getUdpSoundCfg(){
		JSONObject result = new JSONObject();
		try {
			String sql = "select * from tmSyslogSound  where fGroupId = '2015' ";
			JdbcAbstractTemplate jat = new JdbcAbstractTemplate();
			List<Map> list = jat.getListForMap(sql);
			if (list!=null && list.size()>0) {
				if (!u.isFlag()) {
					FileOperate.copydirectiory(u.getSoundPath(),
							u.getUploadPath());
				}
				String soundPath = u.getListenPath();
				LOG.info("laill :soundPath="+soundPath);
				Map map = (Map)list.get(0);//定制需求   只有一条id为2015的声音配置信息
				String groupid = map.get("fgroupid").toString();
				result.put(groupid, map);
				result.put("soundPath", soundPath);
			}
		} catch (Exception e) {
			LOG.info("laill :getSoundCfg error",e);
		}
		LOG.info("laill :cfg="+result.toString());//laill 两条数据key分别为fgroupid和soundPath
		return result.toString();
	}
	
	// 更新声音信息
	public String modifySoundCfg(String strcfg, String domain) {
		JdbcAbstractTemplate jat = null;
		JSONObject result = new JSONObject();
		result.put("success", "false");
		result.put("mesg", "声音配置操作失败！");
		try {
			String sql = "";
			JSONObject jsonObj = JSONObject.fromObject(strcfg);//{\"fgroupid\":\""+id+"\",\"colname\":\"flevel"+level+"\",\"sound\":\""+sound+"\",\"remark\":\"1\"}
			LOG.info("laill:get jsonObj=" + jsonObj);
			if (domain == null)
				jat = new JdbcAbstractTemplate();
			else
				jat = new JdbcAbstractTemplate(domain);
			String colname = jsonObj.getString("colname").toString();
			//判断是否有数据
			sql = "select * from tmsyslogsound where fgroupid="+jsonObj.getString("fgroupid");
			List<Map> l = jat.getListForMap(sql);
			if (l.size() > 0) {
			    String fgroupid = l.get(0).get("fgroupid").toString();
				if (jsonObj.getString("remark").equals("1")) {// 删除试听文件
					sql = "update tmSyslogSound set " + colname
							+ "='' where fGroupId=" + jsonObj.getString("fgroupid");
				} else {//添加声音文件
					sql = "update tmSyslogSound set " + colname + "='"
							+ jsonObj.getString("sound").toString()
							+ "' where fGroupId=" + jsonObj.getString("fgroupid");
				}
				
			}else{
				sql = "insert into tmSyslogSound(fGroupid," + colname + ") values("+jsonObj.getString("fgroupid")+",'"+jsonObj.getString("sound").toString()+"')";
			}
				
			
			if (jat.saveOrUpdate(sql)) {
				result.put("success", "true");
				result.put("mesg", "声音配置操作成功！");
				result.put("colname", colname);
				result.put("groupid", jsonObj.getString("fgroupid"));
				result.put("remark", jsonObj.getString("remark"));
				result.put("hasSound", jsonObj.getString("sound"));
				if (jsonObj.getString("remark").toString().equals("1")) {
					result.put("hasSound", "");
					String sound = jsonObj.getString("sound");
					String fileName = sound.substring(
							(sound.indexOf("&&||&&") + 6),
							sound.lastIndexOf("&&||&&"));
					File file = new File(u.getSoundPath() + fileName);
					if (!file.isDirectory()) {
						file.delete();
					}// 删除tomcat下文件
					File remotefile = new File(u.getUploadPath() + fileName);
					if (!remotefile.isDirectory()) {
						remotefile.delete();
					}// 删除云核文件
				}
				u.initSyslogSound();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	//试听声音
	public String listenSoundCfg(String fileName) {
		String flag = fileName;
		try {
			File file = new File(u.getSoundPath() + fileName);
			if (!file.exists()) {
				File filedesc = new File(u.getUploadPath() + fileName);
				if (filedesc.exists()) {
					FileOperate.copyFile(filedesc, file);
				} else {
					flag = "false";
				}
			}
		} catch (Exception e) {
			flag = "false";
			e.printStackTrace();
		}
		return flag;
	}

	public String getRellog(){
		RemoteObject remote = new RemoteObject();
		remote.setCommand("queryrel");
		remote.setObject("");
		String receive = sendlogcmd(remote);		
		JSONArray ja=JSONArray.fromObject(receive);
		JSONArray jsonDatas = new JSONArray();
		LOG.info("relglogs size:"+ja.size());
		int maxLevel = 8;
		JSONObject objtmp = new JSONObject();//记录  声音文件  放在jsonDatas数组最有一条
		// 判断是否调用声音文件复制方法，通过调用Util.isFlag(),为true说明已经同步过声音文件。
		try {
			if (!u.isFlag()) {
				FileOperate.copydirectiory(u.getSoundPath(), u.getUploadPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(ja.size()>0){
			for(int i=0;i<ja.size();i++){
				JSONObject jsonObj = JSONObject.fromObject(ja.get(i));
				jsonObj.put("fdesc", "");
				jsonObj.put("fcolor", "");
				if(jsonObj.get("level") != null){
					String msgAndDesc = u.getLevelmap().get(jsonObj.get("level").toString());
					if(msgAndDesc != null && msgAndDesc.indexOf("#")!=-1){
						jsonObj.put("fdesc",msgAndDesc.substring(7));
						jsonObj.put("fcolor", msgAndDesc.substring(0,7));
					}
					if(jsonObj.get("time") != null){
						long maxLogTime = u.getLatestUDPRealLogTime();
						long newLogTime = u.getTimeByString(jsonObj.get("time").toString());
//						LOG.info("laill newLogTime="+newLogTime+",maxLogTime="+maxLogTime);
						if(newLogTime > maxLogTime){
							maxLogTime = newLogTime;
							int level = Integer.parseInt(jsonObj.get("level").toString());
							if(level < maxLevel){
								maxLevel = level;
								u.setLatestUDPRealLogTime(maxLogTime);
							}
						}
					}
				}
				jsonDatas.add(jsonObj);
				jsonObj = null;
			}
			
			LOG.info("laill maxLevel="+maxLevel);
			String playSound = getUdpPlaySound(maxLevel);//  desc&&||&&test.mp3
			LOG.info("laill:query database playSound="+playSound);
			if(playSound != null && playSound.trim().length() > 0){
				listenSoundCfg(playSound.substring((playSound.indexOf("&&||&&") + 6)));
				playSound = u.getListenPath()+ playSound.substring((playSound.indexOf("&&||&&") + 6));///sound/998/test.mp3
			}
			LOG.info("laill:playSound="+playSound);
			objtmp.put("playSoundFile", playSound);
		}
		jsonDatas.add(objtmp);
//		LOG.info("laill:"+jsonDatas.toString());
		return jsonDatas.toString();
	}
	
	private String getUdpPlaySound(int level){
		JdbcAbstractTemplate jat = null;
		String sound = "";
		try {
			jat = new JdbcAbstractTemplate();
			String sql = "select * from tmSyslogSound where fGroupId='2015'";
			List<Map> l = jat.getListForMap(sql);
			if(l.size()>0){
				sound = l.get(0).get("flevel"+level).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return sound;
	}
	
	public String getHislog(String ip,String key,String starttime,String endtime,String index,String pageindex,String pagesize){
//		String ip,String key,String starttime,String endtime,String index,String pageindex,String pagesize
		RemoteObject remote = new RemoteObject();
		remote.setCommand("queryhis");
		JSONObject js=new JSONObject();		
		js.put("ip", ip);//may
		js.put("starttime", starttime);
		js.put("endtime", endtime);
		js.put("index", index);
		js.put("pageindex", pageindex);
		js.put("pagesize", pagesize);
		js.put("key", key);//may
		remote.setObject(js.toString());
		String receive = sendlogcmd(remote);
		JSONObject ob= JSONObject.fromObject(receive);
		JSONObject jsonresult = new JSONObject();//返回的json对象
		LOG.debug("result index:"+ob.getString("index"));
		jsonresult.put("index", ob.getString("index"));
		JSONArray jsonDatas = new JSONArray();//result
		JSONArray ja=ob.getJSONArray("result");
		LOG.info("logs size:"+ja.size());
		for(int i=0;i<ja.size();i++){
			JSONObject jsonObj = JSONObject.fromObject(ja.get(i));
			if(jsonObj.get("level") != null){
				String msgAndDesc = u.getLevelmap().get(jsonObj.get("level").toString());
				if(msgAndDesc != null && msgAndDesc.indexOf("#")!=-1){
					jsonObj.put("fdesc",msgAndDesc.substring(7));
					jsonObj.put("fcolor", msgAndDesc.substring(0,7));
				}
			}
			jsonDatas.add(jsonObj);
		}
		jsonresult.put("result", jsonDatas);
		LOG.info("laill:"+jsonresult.toString());
		return jsonresult.toString();
	}

}
