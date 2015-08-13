package itims.web.syslog.event.action;

import itims.web.syslog.event.dwr.BasicInfoDwr;
import itims.web.syslog.event.util.FileOperate;
import itims.web.syslog.event.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.LogFactory;
import org.jfree.util.Log;


public class UploadFileAction extends HttpServlet {
	private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(UploadFileAction.class);

    //private static String uploadPath = ModelCommonParams.getWorkRoot(Domain.getCurDomain());//"D:\\tomcat6\\webapps\\itims\\syslog\\event\\"; // 上传文件的目录
    
    /**public static synchronized String getWorkRoot(int dmsn);
    * 方法解释:获得工作目录路径

    * @param dmsn 域SN
    *
    * @return String
    */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
{
    try {
    	request.setCharacterEncoding("utf-8");
//        DiskFileUpload fu = new DiskFileUpload();
    	 DiskFileItemFactory factory = new DiskFileItemFactory();// 创建基于硬盘的FileItem工厂
    	 ServletFileUpload fu = new ServletFileUpload(factory);// 创建文件上传器
    	 File tempDir = new File(Util.getInstance().getSoundPath()+"temp" +File.separator);
    	 if(!tempDir.exists())
    		 tempDir.mkdir();
    	 factory.setRepository(tempDir);
    	 fu.setHeaderEncoding("utf-8");
        // 设置最大文件尺寸，这里是4MB
        fu.setSizeMax(4*1024*1024);
        // 设置缓冲区大小，这里是4kb
        //fu.getFileSizeMax()setSizeThreshold(4096);
        List fileItems = fu.parseRequest(request);
        String fgroupid = request.getParameter("fgroupid");
        String colname = request.getParameter("colname");
        String domain = request.getParameter("domain");
        String desc = "";
        String origFileName = "";
        String fileName = "";
        LOG.info("fileName"+fileName+"///////////////////"+origFileName);
        LOG.info("path==================="+ tempDir.mkdir());
        if(request.getParameter("desc")!=null)
        	desc = request.getParameter("desc");//new String(request.getParameter("desc").getBytes("ISO-8859-1"), "UTF-8");//描叙
        Iterator i = fileItems.iterator();
        // 依次处理每一个文件：
        while(i.hasNext()) {
            FileItem fi = (FileItem)i.next();
            // 写入文件
            if(!fi.isFormField()){  
            	/*
            	origFileName = fi.getName(); //laill 文件名 system.mp3
                String ext = origFileName.substring(origFileName.lastIndexOf("."));//laill  mp3
                fileName = fgroupid + "_" + colname + "_" + Util.getInstance().RandomNum(3) + ext;//laill 998003638_flevel3_441.mp3
                File file1 = new File(Util.getInstance().getUploadPath() + fileName);
            	fi.write(file1);
            	File file2 = new File(Util.getInstance().getSoundPath() + fileName);
            	FileOperate.copyFile(file1,file2);
            	LOG.info("file1==========="+Util.getInstance().getUploadPath() + fileName);
            	LOG.info("file2==========="+Util.getInstance().getSoundPath() + fileName);
            	//fi.write();*/
            	
            	origFileName = fi.getName(); //laill 文件名 system.mp3
            	File file1 = new File(Util.getInstance().getUploadPath() + origFileName);
            	fi.write(file1);
            	File file2 = new File(Util.getInstance().getSoundPath() + origFileName);
            	FileOperate.copyFile(file1,file2);
            	LOG.info("laill file1 uploadpath="+Util.getInstance().getUploadPath() + origFileName);
            	LOG.info("laill file2 soundpath="+Util.getInstance().getSoundPath() + origFileName);
            }
        }
        //保存到数据库{\"fgroupid\":\""+id+"\",\"colname\":\"flevel"+level+"\",\"sound\":\"\"}
//        String strcfg = "{\"fgroupid\":\""+fgroupid+"\",\"colname\":\""+colname+"\",\"sound\":\""+desc+"&&||&&"+fileName+"&&||&&"+origFileName+"\",\"remark\":\"0\"}";// laill desc&&||&&998003638_flevel3_441.mp3&&||&&system.mp3
        String strcfg = "{\"fgroupid\":\""+fgroupid+"\",\"colname\":\""+colname+"\",\"sound\":\""+desc+"&&||&&"+origFileName+"\",\"remark\":\"0\"}";
        BasicInfoDwr dwr = new BasicInfoDwr();
        String  result = dwr.modifySoundCfg(strcfg,domain);
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        out.write(result);
		out.flush();
		out.close();
    }
    catch(Exception e) {
    	e.printStackTrace();
        // 可以跳转出错页面
    }
}

 //如果要在配置文件中读取指定的上传文件夹，可以在init()方法中执行：
	public void init() throws ServletException {
		System.out.println(" uploadpath:----------- "+Util.getInstance().getUploadPath()+"  soundpath:--------------- "+Util.getInstance().getSoundPath());
		// 文件夹不存在就自动创建：
	    if(!new File(Util.getInstance().getUploadPath()).isDirectory())
	        new File(Util.getInstance().getUploadPath()).mkdirs();
	    if(!new File(Util.getInstance().getSoundPath()).isDirectory())
	        new File(Util.getInstance().getSoundPath()).mkdirs();
	}	
	
}



